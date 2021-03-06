/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.impl.transaction.impl.Transaction;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.utilities.FileManipulation;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static uk.ac.standrews.cs.storr.impl.Repository.bucketNameIsLegal;
import static uk.ac.standrews.cs.storr.types.Types.checkLabelConsistency;

public class DirectoryBackedBucket<T extends PersistentObject> implements IBucket<T> {

    public static final String META_BUCKET_NAME = "META";
    private static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
    private static final String TYPE_LABEL_FILE_NAME = "TYPELABEL";
    protected final File directory;           // the directory implementing the bucket storage
    private final IRepository repository;     // the repository in which the bucket is stored
    private final IStore store;               // the store
    private final String bucket_name;         // the name of this bucket - used as the directory name
    private Class<T> bucketType = null;       // the type of records in this bucket if not null.
    private long type_label_id = -1;          // -1 == not set
    private Cache<Long, PersistentObject> object_cache;
    private int size = -1; // number of items in Bucket.
    private List<Long> cached_oids = null;
    private static final int DEFAULT_CACHE_SIZE = 10000; // almost certainly too small for serious apps.
    private int cache_size = DEFAULT_CACHE_SIZE;

    /**
     * Creates a DirectoryBackedBucket with no factory - a persistent collection of ILXPs
     *
     * @param repository  the repository in which to create the bucket
     * @param bucket_name the name of the bucket to be created
     * @param kind        the kind of Bucket to be created
     * @throws RepositoryException if the bucket cannot be created in the repository
     */
    protected DirectoryBackedBucket(final IRepository repository, final String bucket_name, final BucketKind kind, final boolean create_bucket) throws RepositoryException {

        if (create_bucket) {
            createBucket(bucket_name, repository, kind);
        }

        if (!bucketNameIsLegal(bucket_name)) {
            throw new RepositoryException("Illegal name <" + bucket_name + ">");
        }

        this.bucket_name = bucket_name;
        this.repository = repository;
        this.store = repository.getStore();

        final Path dir_name = dirPath();
        directory = dir_name.toFile();

        if (!directory.isDirectory()) {
            throw new RepositoryException("Bucket Directory: " + dir_name + " does not exist");
        }

        if (!checkKind(bucket_name, repository, kind)) {
            throw new RepositoryException("Bucket kind mismatch: " + bucket_name + "not of kind: " + kind.name());
        }

        watchBucket(repository);

        object_cache = newCache(repository, DEFAULT_CACHE_SIZE, this);
    }

    /**
     * Creates a DirectoryBackedBucket with a factory - a persistent collection of ILXPs tied to some particular Java and store type.
     *
     * @param repository  the repository in which to create the bucket 
     * @param bucket_name the name of the bucket to be created
     * @param kind        the kind of Bucket to be created
     * @throws RepositoryException if the bucket cannot be created in the repository
     */
    DirectoryBackedBucket(final IRepository repository, final String bucket_name, final BucketKind kind, final Class<T> bucketType, final boolean create_bucket) throws RepositoryException {

        this.bucketType = bucketType;
        this.bucket_name = bucket_name;
        this.repository = repository;
        this.store = repository.getStore();
        final long class_type_label_id;

        if (!bucketNameIsLegal(bucket_name)) {
            throw new RepositoryException("Illegal name <" + bucket_name + ">");
        }

        checkKind(bucket_name, repository, kind);

        try {
            final T instance = bucketType.newInstance(); // guarantees meta data creation.
            final PersistentMetaData md = instance.getMetaData();
            class_type_label_id = md.getType().getId();
        } catch (final IllegalAccessException | InstantiationException e) {
            throw new RepositoryException(e);
        }

        if (create_bucket) {
            createBucket(bucket_name, repository, kind, class_type_label_id);
            directory = dirPath().toFile(); // Dir does not exist until after this call.
            type_label_id = class_type_label_id;
        } else {
            directory = dirPath().toFile(); // The dir must exist since we are not creating it, and needed for getTypeLabelID()
            if (!directory.isDirectory()) {
                throw new RepositoryException("Bucket Directory: " + dirPath() + " does not exist");
            }
            type_label_id = getTypeLabelID();
            if (type_label_id != class_type_label_id) {
                throw new RepositoryException("Bucket label incompatible with class: " + bucketType.getName() + " doesn't match bucket label:" + type_label_id);
            }
        }

        watchBucket(repository);
        object_cache = newCache(repository, DEFAULT_CACHE_SIZE, this);
    }

    public void setCacheSize(final int cache_size ) throws Exception {
        if( cache_size < object_cache.size() ) {
            throw new Exception( "Object cache cannot be dynamically made smaller" );
        }
        final LoadingCache<Long, PersistentObject> new_cache = newCache(repository, cache_size, this);
        new_cache.putAll( object_cache.asMap() );
        this.cache_size = cache_size;
        object_cache = new_cache;
    }

    public int getCacheSize() {
        return cache_size;
    }

    private LoadingCache<Long, PersistentObject> newCache(final IRepository repository, final int cacheSize, final DirectoryBackedBucket<T> my_bucket) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .weakValues()
                .build(
                        new CacheLoader<Long, PersistentObject>() {

                            public PersistentObject load(final Long id) throws BucketException { // no checked exception
                                return loader(id);
                            }
                        }
                );
    }

    public PersistentObject loader(final Long id) throws BucketException { // no checked exception

        final PersistentObject result;

        try (final BufferedReader reader = Files.newBufferedReader(filePath(id), FileManipulation.FILE_CHARSET)) {

            if (bucketType == null) { //  No java constructor specified
                try {
                    result = new DynamicLXP(id, new JSONReader(reader), this);
                } catch (final PersistentObjectException e) {
                    throw new BucketException("Could not create new LXP for object with id: " + id + " in directory: " + directory );
                }
            } else {
                final Constructor<?> constructor;
                try {
                    final Class[] param_classes = new Class[] { long.class, JSONReader.class, IBucket.class };
                    constructor = bucketType.getConstructor( param_classes );
                }
                catch ( final NoSuchMethodException e ) {
                    throw new BucketException("Error in reflective constructor call - class " + bucketType.getName() + " must implement constructors with the following signature: Constructor(long persistent_object_id, JSONReader reader, IBucket bucket )" );
                }
                try {
                    result = (PersistentObject) constructor.newInstance( id, new JSONReader(reader), this);
                } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new BucketException("Error in reflective call of constructor in class " + bucketType.getName() + ": " + e.getMessage() );
                }

            }
        } catch (final IOException e) {
            throw new BucketException( "Error creating JSONReader for id: " + id + " in bucket " + bucket_name );
        }
        return result;
    }

    private static void createBucket(final String name, final IRepository repository, final BucketKind kind) throws RepositoryException {

        if (!bucketNameIsLegal(name)) {
            throw new RepositoryException("Illegal name <" + name + ">");
        }
        if (bucketExists(name, repository)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            final Path path = getBucketPath(name, repository);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            // set up directory for transaction support...
            FileManipulation.createDirectoryIfDoesNotExist(path.resolve(TRANSACTIONS_BUCKET_NAME));

            setKind(path, kind);

        } catch (final IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private static void createBucket(final String name, final IRepository repository, final BucketKind kind, final long type_label) throws RepositoryException {

        createBucket(name, repository, kind);
        saveTypeLabel(name, repository, type_label);
    }

    private static void saveTypeLabel(final String name, final IRepository repository, final long type_label) throws RepositoryException {

        if (type_label == -1) {  // only write the label if it has been set.
            return;
        }

        final Path path = getBucketPath(name, repository);
        final Path typepath = path.resolve(META_BUCKET_NAME).resolve(TYPE_LABEL_FILE_NAME);
        try {
            FileManipulation.createFileIfDoesNotExist(typepath);
        } catch (final IOException e) {
            throw new RepositoryException(e);
        }

        try (final BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {
            writer.write(String.valueOf(type_label));
            writer.flush();

        } catch (final IOException e1) {
            throw new RepositoryException(e1);
        }
    }

    private static boolean bucketExists(final String name, final IRepository repo) {

        return bucketNameIsLegal(name) && Files.exists(getBucketPath(name, repo));
    }

    //****************** Getters ******************//

    private static Path getBucketPath(final String name, final IRepository repo) {

        return repo.getRepositoryPath().resolve(name);
    }

    private static void setKind(final Path path, final BucketKind kind) {

        try {
            FileManipulation.createDirectoryIfDoesNotExist(path.resolve(META_BUCKET_NAME).resolve(kind.name()));  // create a directory labelled with the kind in the new bucket dir

        } catch (final IOException e) {
            throw new RuntimeException("I/O Exception setting kind");
        }
    }

    /**
     * @param name       - the name of this bucket 
     * @param repository - the repo the bucket is in
     * @param kind       - the expected kind of the bucket 
     * @return true if the bucket is of that kind
     */
    private static boolean checkKind(final String name, final IRepository repository, final BucketKind kind) {
        final Path path = getBucketPath(name, repository);
        return Files.exists(path.resolve(META_BUCKET_NAME).resolve(kind.name()));
    }

    private void watchBucket(final IRepository repository) throws RepositoryException {
        try {
            final Watcher watcher = repository.getStore().getWatcher();
            watcher.register(directory.toPath(), this);

        } catch (final IOException e) {
            throw new RepositoryException("Failure to add watcher for Bucket " + bucket_name);
        }
    }

    public T getObjectById(final long id) throws BucketException {

        try {
            return (T) object_cache.get(id, () -> loader(id));
            // this is safe since this.contains(id) and also the cache contains the object.

        } catch (final ExecutionException e) {
            throw new BucketException( "Cannot get object by id: " + id + " Exception " + e.getMessage() );
        }
    }

    @Override
    public IRepository getRepository() {
        return this.repository;
    }

    public BucketKind getKind() {
        return BucketKind.DIRECTORYBACKED;
    }

    public String getName() {
        return bucket_name;
    }

    public Class<T> getBucketType() {
        return bucketType;
    }

    public boolean contains(final long id) {

        return filePath(id).toFile().exists();
    }

    public IInputStream<T> getInputStream() throws BucketException {

        try {
            return new BucketBackedInputStream<>(this);

        } catch (final IOException e) {
            throw new BucketException(e.getMessage());
        }
    }

    //***********************************************************//

    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream<>(this);
    }

    /**
     * @return the oids of records that are in this bucket 
     */
    public synchronized List<Long> getOids() {

        if (cached_oids == null) {

            cached_oids = new ArrayList<>();

            final Iterator<File> iterator = new FileIterator(directory, true, false);
            while (iterator.hasNext()) {
                cached_oids.add(Long.parseLong(iterator.next().getName()));
            }
        }
        return cached_oids;
    }

    private long getTypeLabelID() {

        if (type_label_id != -1) {
            return type_label_id;
        } // only look it up if not cached.

        final Path path = directory.toPath();
        final Path typepath = path.resolve(META_BUCKET_NAME).resolve(TYPE_LABEL_FILE_NAME);

        try (final BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            final String id_as_string = reader.readLine();
            type_label_id = Long.parseLong(id_as_string);
            return type_label_id;

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTypeLabelID(final long type_label_id) throws IOException {

        if (this.type_label_id != -1) {
            throw new IOException("Type label already set");
        }
        this.type_label_id = type_label_id; // cache it and keep a persistent copy of the label.

        final Path path = directory.toPath();
        final Path meta_path = path.resolve(META_BUCKET_NAME);
        FileManipulation.createDirectoryIfDoesNotExist(meta_path);

        final Path typepath = meta_path.resolve(TYPE_LABEL_FILE_NAME);
        if (Files.exists(typepath)) {
            throw new IOException("Type label already set");
        }
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (final BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(Long.toString(type_label_id)); // Write the id of the typelabel OID into this field.
            writer.newLine();
        }
    }

    public void makePersistent(final T record) throws BucketException {

        final long id = record.getId();
        if (contains(id)) {
            throw new BucketException("records may not be overwritten - use update");
        } else {
            writePersistentObject(record, filePath(record.getId())); // normal object write
        }
    }

    @Override
    public synchronized void update(final T record) throws BucketException {

        final long id = record.getId();
        if (!contains(id)) {
            throw new BucketException("bucket does not contain specified id");
        }
        final Transaction t;
        try {
            t = store.getTransactionManager().getTransaction(Long.toString(Thread.currentThread().getId()));
        } catch (final StoreException e) {
            throw new BucketException(e);
        }
        if (t == null) {
            throw new BucketException("No transactional context specified");
        }

        final Path new_record_write_location = transactionsPath(record.getId());
        if (new_record_write_location.toFile().exists()) { // we have a transaction conflict.
            t.rollback();
            return;
        }
        t.add(this, record.getId());

        writePersistentObject(record, new_record_write_location); //  write to transaction log
    }

    private void writePersistentObject(final PersistentObject record_to_write, final Path filepath) throws BucketException {
        if( record_to_write instanceof LXP ) {
            writeLXP( (LXP) record_to_write, filepath );
        } else if( record_to_write instanceof JPO ) {
            writeJPO( (JPO) record_to_write, filepath );
        }
    }

    private void writeJPO(final JPO record_to_write, final Path filepath) throws BucketException {
        // Bucket is appropriately typed or we cannot get to here
        // Therefore just need to write the record out.
        writeData(record_to_write, filepath);
    }

    void writeLXP(final LXP record_to_write, final Path filepath) throws BucketException {

        if (type_label_id != -1) { // we have set a type label in this bucket there must check for consistency

            if (record_to_write.getMetaData().containsLabel(Types.LABEL)) { // if there is a label it must be correct
                if (!(checkLabelConsistency(record_to_write, type_label_id, store))) { // check that the record label matches the bucket label - throw exception if it doesn't
                    throw new BucketException("Label incompatibility");
                }
            }
            // get to here -> there is no record label on record
            try {
                if (!Types.checkStructuralConsistency(record_to_write, type_label_id, store)) {
                    // Temporarily output more information, for diagnostics
                    throw new BucketException("Structural integrity incompatibility"
                            + "\nrecord_to_write: " + record_to_write + "\n"
                            + "\ntype_label_id: " + type_label_id + "\n");
                }
            } catch (final IOException e) {
                throw new BucketException("I/O exception checking Structural integrity");
            }
        } else // get to here and bucket has no type label on it.
            if (record_to_write.getMetaData().containsLabel(Types.LABEL)) { // no type label on bucket but record has a type label so check structure
                try {
                    if (!Types.checkStructuralConsistency(record_to_write, (long) record_to_write.get(Types.LABEL), store)) {
                        throw new BucketException("Structural integrity incompatibility");
                    }
                } catch (final KeyNotFoundException e) {
                    // this cannot happen - label checked in if .. so .. just let it go
                } catch (final IOException e) {
                    throw new BucketException("I/O exception checking consistency");
                } catch (final TypeMismatchFoundException e) {
                    throw new BucketException("Type mismatch checking consistency");
                }
            }

        writeData(record_to_write, filepath);
    }

    private void writeData(final PersistentObject record_to_write, final Path filepath) throws BucketException {

        try (final Writer writer = Files.newBufferedWriter(filepath, FileManipulation.FILE_CHARSET)) { // auto close and exception

            record_to_write.serializeToJSON(new JSONWriter(writer), this);
            object_cache.put(record_to_write.getId(), record_to_write);  // Putting this call here ensures that all records that are in a bucket and loaded are in the cache

        } catch (final IOException | JSONException e) {
            throw new BucketException(e);
        }
    }

    public synchronized int size() throws BucketException {

        if (size == -1) {
            try {
                size = (int) Files.list(directory.toPath()).count() - 2; // do not count . and ..
            } catch (final IOException e) {
                throw new BucketException("Cannot determine size - I/O error");
            }
        }
        return size;
    }

    /**
     * `
     * called by Watcher service
     */
    public synchronized void invalidateCache() {

        size = -1;
        cached_oids = null;
        object_cache = newCache(repository, cache_size, this); // There may be extent references to these objects in the heap which should be invalidated.
    }

    /**
     * ******** Transaction support **********
     */

    @Override
    public void swizzle(final long oid) {

        final Path shadow_location = transactionsPath(oid);
        if (!shadow_location.toFile().exists()) {
            throw new RuntimeException("******* Transaction error:Shadow file does not exist *******");
        }
        final Path primary_location = filePath(oid);
        if (!primary_location.toFile().exists()) {
            throw new RuntimeException("******* Transaction error: Primary file does not exist *******");
        }
        if (!primary_location.toFile().delete()) {
            throw new RuntimeException("******* Transaction error: Primary file cannot be deleted *******");
        }
        try {
            Files.createLink(primary_location, shadow_location);
        } catch (final IOException e) {
            throw new RuntimeException("******* Transaction error: Primary file cannot be linked from shadow *******");
        }
        if (!shadow_location.toFile().delete()) {
            throw new RuntimeException("******* Transaction error: Shadow file cannot be deleted *******");
        }
    }

    @Override
    public void cleanup(final long oid) {

        final Path shadow_location = transactionsPath(oid);
        if (!shadow_location.toFile().exists()) {
            throw new RuntimeException("******* Transaction error: Shadow file does not exist *******");
        }
        if (!shadow_location.toFile().delete()) {
            throw new RuntimeException("******* Transaction error: Shadow file cannot be deleted *******");
        }
    }

    @Override
    public void delete(final long oid) throws BucketException {

        final Path record_location = filePath(oid);
        if (!record_location.toFile().exists()) {
            throw new BucketException("Record with id: " + oid + " does not exist");
        }
        if (!record_location.toFile().delete()) {
            throw new BucketException("Unsuccessful delete of oid: " + oid);
        }
    }

    /*
     * Tidies up transaction data that may be left over following a crash
     */
    @Override
    public void tidyUpTransactionData() {

        final Iterator<File> iterator = new FileIterator(filePath(TRANSACTIONS_BUCKET_NAME).toFile(), true, false);

        while (iterator.hasNext()) {
            final File f = iterator.next();
            if (!f.delete()) {
                throw new RuntimeException("******* Transaction error: error tidying up transaction data on recovery");
            }
        }
    }

    /**
     * ******** Path manipulation **********
     */

    private Path transactionsPath(final long oid) {
        return dirPath().resolve(TRANSACTIONS_BUCKET_NAME).resolve(String.valueOf(oid));
    }

    //******** Private methods *********

    Path dirPath() {
        return repository.getRepositoryPath().resolve(bucket_name);
    }

    //******** Private methods *********

    public Path filePath(final long id) {
        return filePath(String.valueOf(id));
    }

    private Path filePath(final String id) {
        return dirPath().resolve(id);
    }
}
