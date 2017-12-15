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
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static uk.ac.standrews.cs.storr.impl.Repository.bucketNameIsLegal;
import static uk.ac.standrews.cs.storr.types.Types.checkLabelConsistency;

public class DirectoryBackedBucket<T extends LXP> implements IBucket<T> {

    public static final String META_BUCKET_NAME = "META";
    private static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
    private static final String TYPE_LABEL_FILE_NAME = "TYPELABEL";
    protected final File directory;           // the directory implementing the bucket storage
    private final IRepository repository;     // the repository in which the bucket is stored
    private final IStore store;               // the store
    private final String bucket_name;         // the name of this bucket - used as the directory name
    private Class<T> bucketType = null;       // the type of records in this bucket if not null.
    private long type_label_id = -1;          // -1 == not set
    private Cache<Long, LXP> object_cache; // = new ObjectCache();
    private int size = -1; // number of items in Bucket.
    private List<Long> cached_oids = null;

    /**
     * Creates a DirectoryBackedBucket with no factory - a persistent collection of ILXPs
     *
     * @param repository  the repository in which to create the bucket
     * @param bucket_name the name of the bucket to be created
     * @param kind        the kind of Bucket to be created
     * @throws RepositoryException if the bucket cannot be created in the repository
     */
    protected DirectoryBackedBucket(final IRepository repository, final String bucket_name, final BucketKind kind, boolean create_bucket) throws RepositoryException {

        if (create_bucket) {
            createBucket(bucket_name, repository, kind);
        }

        if (!bucketNameIsLegal(bucket_name)) {
            throw new RepositoryException("Illegal name <" + bucket_name + ">");
        }

        this.bucket_name = bucket_name;
        this.repository = repository;
        this.store = repository.getStore();

        Path dir_name = dirPath();
        directory = dir_name.toFile();

        if (!directory.isDirectory()) {
            throw new RepositoryException("Bucket Directory: " + dir_name + " does not exist");
        }

        if( ! checkKind(bucket_name, repository, kind) ) {
            throw new RepositoryException("Bucket kind mismatch: " + bucket_name + "not of kind: " + kind.name() );
        }

        watchBucket(repository);

        object_cache = newCache(repository, this);
    }

    /**
     * Creates a DirectoryBackedBucket with a factory - a persistent collection of ILXPs tied to some particular Java and store type.
     *
     * @param repository  the repository in which to create the bucket
     * @param bucket_name the name of the bucket to be created
     * @param kind        the kind of Bucket to be created
     * @throws RepositoryException if the bucket cannot be created in the repository
     */
    DirectoryBackedBucket(final IRepository repository, final String bucket_name, BucketKind kind, Class<T> bucketType, boolean create_bucket) throws RepositoryException  {

        this.bucketType = bucketType;
        this.bucket_name = bucket_name;
        this.repository = repository;
        this.store = repository.getStore();
        long class_type_label_id;

        try {
            T instance = bucketType.newInstance(); // guarantees meta data creation.
            Metadata md = instance.getMetaData();
            class_type_label_id = md.getType().getId();
        } catch (IllegalAccessException |  InstantiationException e) {
            throw new RepositoryException( e );
        }

        if (create_bucket) {
            createBucket(bucket_name, repository, kind, class_type_label_id);
            type_label_id = class_type_label_id;
        } else
            type_label_id = getTypeLabelID();
            if ( type_label_id != class_type_label_id ) {
                throw new RepositoryException("Bucket label incompatible with class: " + bucketType.getName() + " doesn't match bucket label:" + type_label_id);
        }

        if (!bucketNameIsLegal(bucket_name)) {
            throw new RepositoryException("Illegal name <" + bucket_name + ">");
        }

        Path dir_name = dirPath();
        directory = dir_name.toFile();

        if (!directory.isDirectory()) {
            throw new RepositoryException("Bucket Directory: " + dir_name + " does not exist");
        }

        checkKind(bucket_name, repository, kind);

        watchBucket(repository);
        object_cache = newCache(repository, this);
    }

    private LoadingCache<Long, LXP> newCache(IRepository repository, DirectoryBackedBucket<T> my_bucket) {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .weakValues()
                .build(
                        new CacheLoader<Long, LXP>() {

                            public LXP load(Long id) throws BucketException { // no checked exception
                                return loader(id);
                            }
                        }
                );
    }

    public LXP loader(Long id) throws BucketException { // no checked exception

        LXP result;

        try (BufferedReader reader = Files.newBufferedReader(filePath(id), FileManipulation.FILE_CHARSET)) {

            if (bucketType == null) { //  No java constructor specified
                try {
                    result = (T) (new DynamicLXP(id, new JSONReader(reader), this));
                } catch (PersistentObjectException e) {
                    throw new BucketException("Could not create new LXP for object with id: " + id + " in directory: " + directory );
                }
            } else {
                try {
                    // result = (LXP) bucketType.getDeclaredMethod("create").invoke( id, new JSONReader(reader), this); // got rid of requirement for this method - specified constructor now defined in LXP.
                    Constructor<?> constructor = bucketType.getConstructor( Long.class, JSONReader.class, IBucket.class );
                    result = (LXP) constructor.newInstance( id, new JSONReader(reader), this);
                }
                catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                    throw new BucketException("Error in reflective call of create" );
                }
            }

//            // Now check for indirection
//            if (result.containsKey(StoreReference.$INDIRECTION$)) {
//                // we have an indirection
//                // so try and load the record that the indirection record points to.
//                StoreReference<T> ref = new StoreReference<T>(store, (String) result.get(StoreReference.REPOSITORY), (String) result.get(StoreReference.BUCKET), (long) result.get(StoreReference.OID));
//                result = ref.getReferend();
//            }
        } catch (IOException e1) {
            throw new BucketException("Exception creating reader for LXP with id: " + id + " in directory: " + directory);
        }
        return result;

    }

    private static void createBucket(final String name, IRepository repository, BucketKind kind) throws RepositoryException {

        if (!bucketNameIsLegal(name)) {
            throw new RepositoryException("Illegal name <" + name + ">");
        }
        if (bucketExists(name, repository)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name, repository);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            // set up directory for transaction support...
            FileManipulation.createDirectoryIfDoesNotExist(path.resolve(TRANSACTIONS_BUCKET_NAME));

            setKind(path, kind);

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private static void createBucket(final String name, IRepository repository, BucketKind kind, long type_label) throws RepositoryException {

        createBucket(name, repository, kind);
        saveTypeLabel(name, repository, type_label);
    }

    //****************** Getters ******************//

    private static void saveTypeLabel(String name, IRepository repository, long type_label) throws RepositoryException {

        if (type_label == -1) {  // only write the label if it has been set.
            return;
        }

        Path path = getBucketPath(name, repository);
        Path typepath = path.resolve(META_BUCKET_NAME).resolve(TYPE_LABEL_FILE_NAME);
        try {
            FileManipulation.createFileIfDoesNotExist(typepath);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {
            writer.write(String.valueOf(type_label));
            writer.flush();
            writer.close();

        } catch (IOException e1) {
            throw new RepositoryException(e1);
        }
    }

    private static boolean bucketExists(final String name, IRepository repo) {

        return bucketNameIsLegal(name) && Files.exists(getBucketPath(name, repo));
    }

    private static Path getBucketPath(final String name, IRepository repo) {

        return repo.getRepositoryPath().resolve(name);
    }

    public T getObjectById(long id) throws BucketException {

        T result;

        LXP o = null;
        try {
            o = object_cache.get(id,
                        new Callable<LXP>() {
                            public LXP call() throws BucketException {
                                return loader(id);
                            }

                        });
        } catch (ExecutionException e) {
            throw new BucketException( e );
        }
        return (T) o; // this is safe since this.contains(id) and also the cache contains the object.


//        if (!this.contains(id)) {
//            // ErrorHandling.error("Bucket does not contain object with id: " + id);
//            throw new BucketException("Bucket does not contain id: " + id);
//
//        }
//        try (BufferedReader reader = Files.newBufferedReader(filePath(id), FileManipulation.FILE_CHARSET)) {
//
//            if (tFactory == null) { //  No java constructor specified
//                try {
//                    result = (T) (new LXP(id, new JSONReader(reader), getRepository(), this));
//                } catch (PersistentObjectException e) {
//                    throw new BucketException("Could not create new LXP for object with id: " + id + " in directory: " + directory);
//                }
//            } else {
//                try {
//                    result = tFactory.create(id, new JSONReader(reader), this.repository, this);
//                } catch (PersistentObjectException e) {
//                    throw new BucketException("Could not create new LXP (using factory) for object with id: " + id + " in directory: " + directory);
//                }
//            }
//
//            // Now check for indirection
//            if (result.containsKey(StoreReference.$INDIRECTION$)) {
//                // we have an indirection
//                // so try and load the record that the indirection record points to.
//                StoreReference<T> ref = new StoreReference<T>(store, result.getString(StoreReference.REPOSITORY), result.getString(StoreReference.BUCKET), result.getLong(StoreReference.OID));
//                result = ref.getReferend();
//            }
//        } catch (IOException e1) {
//            throw new BucketException("Exception creating reader for LXP with id: " + id + " in directory: " + directory);
//        }
//        object_cache.put(id, this, result);           // Putting this call here ensures that all records that are in a bucket and loaded are in the cache
//        return result;
    }

    @Override
    public IRepository getRepository() {
        return this.repository;
    }

    public BucketKind getKind() {
        return BucketKind.DIRECTORYBACKED;
    }

    // Stream operations

    private static void setKind(Path path, BucketKind kind) {

        try {
            FileManipulation.createDirectoryIfDoesNotExist(path.resolve(META_BUCKET_NAME).resolve(kind.name()));  // create a directory labelled with the kind in the new bucket dir

        } catch (IOException e) {
            ErrorHandling.error("I/O Exception setting kind");
        }
    }

    /**
     *
     * @param name - the name of this bucket
     * @param repository - the repo the bucket is in
     * @param kind - the expected kind of the bucket
     * @return true if the bucket is of that kind
     */
    private static boolean checkKind(String name, IRepository repository, BucketKind kind) {
        Path path = getBucketPath(name, repository);
        return Files.exists( path.resolve(META_BUCKET_NAME).resolve(kind.name()));
    }

    private void watchBucket(IRepository repository) throws RepositoryException {
        try {
            Watcher watcher = repository.getStore().getWatcher();
            watcher.register(directory.toPath(), this);

        } catch (IOException e) {
            throw new RepositoryException("Failure to add watcher for Bucket " + bucket_name);
        }

    }

    public String getName() {
        return bucket_name;
    }

    public Class<T> getBucketType() {
        return bucketType;
    }

    public boolean contains(long id) {

        return filePath(id).toFile().exists();
    }

    public IInputStream<T> getInputStream() throws BucketException {

        try {
            return new BucketBackedInputStream<>(this);

        } catch (IOException e) {
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

            Iterator<File> iterator = new FileIterator(directory, true, false);
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

        Path path = directory.toPath();
        Path typepath = path.resolve(META_BUCKET_NAME).resolve(TYPE_LABEL_FILE_NAME);

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            String id_as_string = reader.readLine();
            type_label_id = Long.parseLong(id_as_string);
            return type_label_id;

        } catch (IOException e) {
            ErrorHandling.error("I/O Exception getting labelID");
            return -1;
        }
    }

    public void setTypeLabelID(long type_label_id) throws IOException {

        if (this.type_label_id != -1) {
            throw new IOException("Type label already set");
        }
        this.type_label_id = type_label_id; // cache it and keep a persistent copy of the label.

        Path path = directory.toPath();
        Path meta_path = path.resolve(META_BUCKET_NAME);
        FileManipulation.createDirectoryIfDoesNotExist(meta_path);

        Path typepath = meta_path.resolve(TYPE_LABEL_FILE_NAME);
        if (Files.exists(typepath)) {
            throw new IOException("Type label already set");
        }
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(Long.toString(type_label_id)); // Write the id of the typelabel OID into this field.
            writer.newLine();
        }
    }

//    public IObjectCache getObjectCache() {
//        return object_cache;
//    }


    public IObjectCache getObjectCache() {
        return null; // TODO FIX LATER - Al ****
    }

    public void makePersistent(final T record) throws BucketException {

        long id = record.getId();
        if (contains(id)) {
            throw new BucketException("records may not be overwritten - use update");
        } else {
            writeLXP(record, filePath(record.getId())); // normal object write
        }
    }

    @Override
    public synchronized void update(T record) throws BucketException {

        long id = record.getId();
        if (!contains(id)) {
            throw new BucketException("bucket does not contain specified id");
        }
        Transaction t;
        try {
            t = store.getTransactionManager().getTransaction(Long.toString(Thread.currentThread().getId()));
        } catch (StoreException e) {
            throw new BucketException(e);
        }
        if (t == null) {
            throw new BucketException("No transactional context specified");
        }

        Path new_record_write_location = transactionsPath(record.getId());
        if (new_record_write_location.toFile().exists()) { // we have a transaction conflict.
            t.rollback();
            return;
        }
        t.add(this, record.getId());

        writeLXP(record, new_record_write_location); //  write to transaction log
    }

    void writeLXP(LXP record_to_write, Path filepath) throws BucketException {

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
            } catch (IOException e) {
                throw new BucketException("I/O exception checking Structural integrity");
            }
        } else // get to here and bucket has no type label on it.
            if (record_to_write.getMetaData().containsLabel(Types.LABEL)) { // no type label on bucket but record has a type label so check structure
                try {
                    if (!Types.checkStructuralConsistency(record_to_write, (long) record_to_write.get(Types.LABEL), store)) {
                        throw new BucketException("Structural integrity incompatibility");
                    }
                } catch (KeyNotFoundException e) {
                    // this cannot happen - label checked in if .. so .. just let it go
                } catch (IOException e) {
                    throw new BucketException("I/O exception checking consistency");
                } catch (TypeMismatchFoundException e) {
                    throw new BucketException("Type mismatch checking consistency");
                }
            }

        writeData(record_to_write, filepath);
    }

    private void writeData(LXP record_to_write, Path filepath) throws BucketException {

        try (Writer writer = Files.newBufferedWriter(filepath, FileManipulation.FILE_CHARSET)) { // auto close and exception

            record_to_write.serializeToJSON(new JSONWriter(writer), this);
            object_cache.put(record_to_write.getId(), record_to_write);  // Putting this call here ensures that all records that are in a bucket and loaded are in the cache

        } catch (IOException | JSONException e) {
            throw new BucketException(e);
        }
    }

    public synchronized int size() throws BucketException {

        if (size == -1) {
            try {
                size = (int) Files.list(directory.toPath()).count();
            } catch (IOException e) {
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
        object_cache = newCache(repository, this); //TODO I am worried about this - there maybe extant refs to these objects in the heap. *****
    }

    /**
     * ******** Transaction support **********
     */

    @Override
    public void swizzle(long oid) {

        Path shadow_location = transactionsPath(oid);
        if (!shadow_location.toFile().exists()) {
            ErrorHandling.error("******* Transaction error:Shadow file does not exist *******");
        }
        Path primary_location = filePath(oid);
        if (!primary_location.toFile().exists()) {
            ErrorHandling.error("******* Transaction error: Primary file does not exist *******");
        }
        if (!primary_location.toFile().delete()) {
            ErrorHandling.error("******* Transaction error: Primary file cannot be deleted *******");
        }
        try {
            Files.createLink(primary_location, shadow_location);
        } catch (IOException e) {
            ErrorHandling.error("******* Transaction error: Primary file cannot be linked from shadow *******");
        }
        if (!shadow_location.toFile().delete()) {
            ErrorHandling.error("******* Transaction error: Shadow file cannot be deleted *******");
        }
    }

    @Override
    public void cleanup(long oid) {

        Path shadow_location = transactionsPath(oid);
        if (!shadow_location.toFile().exists()) {
            ErrorHandling.error("******* Transaction error: Shadow file does not exist *******");
        }
        if (!shadow_location.toFile().delete()) {
            ErrorHandling.error("******* Transaction error: Shadow file cannot be deleted *******");
        }
    }

    @Override
    public void delete(long oid) throws BucketException {

        Path record_location = filePath(oid);
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

        Iterator<File> iterator = new FileIterator(filePath(TRANSACTIONS_BUCKET_NAME).toFile(), true, false);

        while (iterator.hasNext()) {
            File f = iterator.next();
            if (!f.delete()) {
                ErrorHandling.error("******* Transaction error: error tidying up transaction data on recovery");
            }
        }
    }

    /**
     * ******** Path manipulation **********
     */

    private Path transactionsPath(long oid) {
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
