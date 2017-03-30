package uk.ac.standrews.cs.storr.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.impl.transaction.impl.Transaction;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.storr.util.ErrorHandling;
import uk.ac.standrews.cs.storr.util.FileManipulation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static uk.ac.standrews.cs.storr.types.Types.checkLabelConsistency;

public class DirectoryBackedBucket<T extends ILXP> implements IBucket<T> {

    protected ILXPFactory<T> tFactory = null;
    private final IRepository repository;     // the repository in which the bucket is stored
    private final IStore store;               // the store
    private final String bucket_name;         // the name of this bucket - used as the directory name
    protected final File directory;           // the directory implementing the bucket storage
    private long type_label_id = -1;          // -1 == not set
    private IObjectCache object_cache = new ObjectCache();

    private static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
    public static final String META_BUCKET_NAME = "META";
    private static final String TYPE_LABEL_FILE_NAME = "TYPELABEL";

    private long size = -1; // number of items in Bucket.
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

        if (!Repository.legalName(bucket_name)) {
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

        setKind(kind);

        try {
            Watcher watcher = repository.getStore().getWatcher();
            watcher.register(directory.toPath(), this);

        } catch (IOException e) {
            throw new RepositoryException("Failure to add watcher for Bucket " + bucket_name);
        }
    }

    /**
     * Creates a DirectoryBackedBucket with a factory - a persistent collection of ILXPs tied to some particular Java and store type.
     *
     * @param repository  the repository in which to create the bucket
     * @param bucket_name the name of the bucket to be created
     * @param kind        the kind of Bucket to be created
     * @param tFactory    the factory to use when importing objects from the store
     * @throws RepositoryException if the bucket cannot be created in the repository
     */
    DirectoryBackedBucket(final IRepository repository, final String bucket_name, BucketKind kind, ILXPFactory<T> tFactory, boolean create_bucket) throws RepositoryException {

        if (create_bucket) {
            createBucket(bucket_name, repository, kind, tFactory.getTypeLabel());
        }

        if (!Repository.legalName(bucket_name)) {
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

        setKind(kind);

        try {
            Watcher watcher = repository.getStore().getWatcher();
            watcher.register(directory.toPath(), this);

        } catch (IOException e) {
            throw new RepositoryException("Failure to add watcher for Bucket " + bucket_name);
        }

        this.tFactory = tFactory;

        type_label_id = getTypeLabelID();
        if (type_label_id != tFactory.getTypeLabel()) {
            throw new RepositoryException("Bucket label incompatible with supplied factory: " + tFactory.getTypeLabel() + " doesn't match bucket label:" + type_label_id);
        }
    }

    static void createBucket(final String name, IRepository repository, BucketKind kind) throws RepositoryException {

        if (!Repository.legalName(name)) {
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

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    static void createBucket(final String name, IRepository repository, BucketKind kind, long type_label) throws RepositoryException {

        createBucket(name, repository, kind);
        saveTypeLabel(name, repository, type_label);
    }

    //****************** Getters ******************//

    public T getObjectById(long id) throws BucketException {

        T result;

        ILXP o = object_cache.getObject(id);
        if (o != null) {
            return (T) o; // this is safe since this.contains(id) and also the cache contains the object.
        }

        if (!this.contains(id)) {
            ErrorHandling.error("Bucket does not contain object with id: " + id);

        }
        // if we get to here, it means teh object is in the bucket but is not in the cache

        try (BufferedReader reader = Files.newBufferedReader(filePath(id), FileManipulation.FILE_CHARSET)) {

            if (tFactory == null) { //  No java constructor specified
                try {
                    result = (T) (new LXP(id, new JSONReader(reader), getRepository(), this));
                } catch (PersistentObjectException e) {
                    throw new BucketException("Could not create new LXP for object with id: " + id + " in directory: " + directory);
                }
            } else {
                try {
                    result = tFactory.create(id, new JSONReader(reader), this.repository, this);
                } catch (PersistentObjectException e) {
                    throw new BucketException("Could not create new LXP (using factory) for object with id: " + id + " in directory: " + directory);
                }
            }

            // Now check for indirection
            if (result.containsKey(StoreReference.$INDIRECTION$)) {
                // we have an indirection
                // so try and load the record that the indirection record points to.
                StoreReference<T> ref = new StoreReference<T>(store, result.getString(StoreReference.REPOSITORY), result.getString(StoreReference.BUCKET), result.getLong(StoreReference.OID));
                result = (T) ref.getReferend();
            }
        } catch (IOException e1) {
            throw new BucketException("Exception creating reader for LXP with id: " + id + " in directory: " + directory);
        }
        object_cache.put(id, this, (LXP) result);           // Putting this call here ensures that all records that are in a bucket and loaded are in the cache
        return result;
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

    public ILXPFactory<T> getFactory() {
        return tFactory;
    }

    public boolean contains(long id) {

        return filePath(id).toFile().exists();
    }

    // Stream operations

    public IInputStream<T> getInputStream() throws BucketException {

        try {
            return new BucketBackedInputStream(this);

        } catch (IOException e) {
            throw new BucketException(e.getMessage());
        }
    }

    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream(this);
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

    public IObjectCache getObjectCache() {
        return object_cache;
    }

    //***********************************************************//

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

    void writeLXP(ILXP record_to_write, Path filepath) throws BucketException {

        if (type_label_id != -1) { // we have set a type label in this bucket there must check for consistency

            if (record_to_write.containsKey(Types.LABEL)) { // if there is a label it must be correct
                if (!(checkLabelConsistency(record_to_write, type_label_id, store))) { // check that the record label matches the bucket label - throw exception if it doesn't
                    throw new BucketException("Label incompatibility");
                }
            }
            // get to here -> there is no record label on record
            try {
                if (!Types.checkStructuralConsistency(record_to_write, type_label_id, store)) {
                    throw new BucketException("Structural integrity incompatibility");
                }
            } catch (IOException e) {
                throw new BucketException("I/O exception checking Structural integrity");
            }
        } else // get to here and bucket has no type label on it.
            if (record_to_write.containsKey(Types.LABEL)) { // no type label on bucket but record has a type label so check structure
                try {
                    if (!Types.checkStructuralConsistency(record_to_write, record_to_write.getLong(Types.LABEL), store)) {
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

    private void writeData(ILXP record_to_write, Path filepath) throws BucketException {

        try (Writer writer = Files.newBufferedWriter(filepath, FileManipulation.FILE_CHARSET);) { // auto close and exception

            record_to_write.serializeToJSON(new JSONWriter(writer), getRepository(), this);
            object_cache.put(record_to_write.getId(), this, record_to_write);  // Putting this call here ensures that all records that are in a bucket and loaded are in the cache

        } catch (IOException | JSONException e) {
            throw new BucketException(e);
        }
    }

    private void setKind(BucketKind kind) {

        try {
            FileManipulation.createDirectoryIfDoesNotExist(directory.toPath().resolve(META_BUCKET_NAME).resolve(kind.name()));  // create a directory labelled with the kind in the new bucket dir

        } catch (IOException e) {
            ErrorHandling.error("I/O Exception setting kind");
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

    public synchronized long size() throws BucketException {

        if (size == -1) {
            try {
                size = Files.list(directory.toPath()).count();
            } catch (IOException e) {
                throw new BucketException("Cannot determine size - I/O error");
            }
        }
        return size;
    }

    /**
     * called by Watcher service
     */
    public synchronized void invalidateCache() {

        size = -1;
        cached_oids = null;
        object_cache = new ObjectCache(); //TODO I am worried about this - there maybe extant refs to these objects in the heap. *****
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

    Path dirPath() {
        return repository.getRepositoryPath().resolve(bucket_name);
    }

    public Path filePath(final long id) {
        return filePath(String.valueOf(id));
    }

    Path filePath(final String id) {
        return dirPath().resolve(id);
    }

    //******** Private methods *********

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

    //******** Private methods *********

    private static boolean bucketExists(final String name, IRepository repo) {

        return Repository.legalName(name) && Files.exists(getBucketPath(name, repo));
    }

    private static Path getBucketPath(final String name, IRepository repo) {

        return repo.getRepositoryPath().resolve(name);
    }
}
