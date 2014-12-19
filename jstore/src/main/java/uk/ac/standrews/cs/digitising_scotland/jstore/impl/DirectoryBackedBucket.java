package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static uk.ac.standrews.cs.digitising_scotland.jstore.types.Types.check_label_consistency;
import static uk.ac.standrews.cs.digitising_scotland.jstore.types.Types.check_structural_consistency;

/**
 * Created by al on 19/09/2014.
 */
public class DirectoryBackedBucket<T extends ILXP> implements IBucket<T> {

    protected ILXPFactory<T> tFactory = null;
    private IRepository repository;  // the repository in which the bucket is stored
    private String name;     // the name of this bucket - used as the directory name
    protected File directory;  // the directory implementing the bucket storage
    private long type_label_id = -1; // not set
    IObjectCache objectCache = Store.getInstance().getObjectCache();

    // indirection handling

    protected final static String $INDIRECTION$ = "$INDIRECTION$";
    protected final static String BUCKET = "bucket";
    protected final static String REPOSITORY = "repository";
    protected final static String OID = "oid";

    /*
     * Creates a DirectoryBackedBucket with no factory - a persistent collection of ILXPs
     */
    public DirectoryBackedBucket(final String name, final IRepository repository, BucketKind kind) throws RepositoryException {
        this.name = name;
        this.repository = repository;
        String dir_name = dirPath();
        directory = new File(dir_name);
        setKind(kind);

        if (!directory.isDirectory()) {
            throw new RepositoryException("Bucket Directory: " + dir_name + " does not exist");
        }
    }


    public DirectoryBackedBucket(final String name, final IRepository repository, ILXPFactory<T> tFactory, BucketKind kind) throws RepositoryException {
        this(name, repository, kind);
        this.tFactory = tFactory;
        type_label_id = tFactory.getTypeLabel();
    }

    public static IBucket createBucket(final String name, IRepository repository, BucketKind kind) throws RepositoryException {
        if (bucketExists(name, repository)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name, repository);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            return new DirectoryBackedBucket(name, repository, kind);
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public T get(long id) throws BucketException {
        T result;

        if (this.contains(id) && objectCache.contains(id)) {
            ILXP o = objectCache.getObject(id);
            if (o == null) {
                ErrorHandling.error("Could not find cached object in object cache");
            }
            return (T) o; // this is safe since the value couldn't get into the cache unless of teh right type.
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            if (tFactory == null) { //  No java type specified
                result = (T) (new LXP(id, new JSONReader(reader)));
            } else result = tFactory.create(id, new JSONReader(reader));

            // Now check for indirection
            if (result.containsKey($INDIRECTION$)) { // try and load the record that the indirection record points to
                result = resolve_indirection(result);
            }
            objectCache.put(id, this, (LXP) result);                             // Putting this call here ensures that all records that are in a bucket and loaded are in the cache
            return result;

        } catch (IOException e) {
            throw new BucketException("I/O error");
        } catch (PersistentObjectException e) {
            throw new BucketException("Persistent object error");
        } catch (IllegalKeyException e) {
            throw new BucketException("Illegal key error");
        }
    }


    public void put(final T record) throws BucketException {
        try {
            long id = record.getId();
            if (this.contains(id)) {
                throw new BucketException("records may bot be overwritten");
            }
            if (objectCache.contains(id)) { // the object is already in the store so write an indirection
                writeLXP(record, create_indirection(record));
            } else {
                writeLXP(record, record); // normal object write
            }
        } catch (IOException e) {
            throw new BucketException("Persistent object  error");
        } catch (JSONException e) {
            throw new BucketException("JSONException error");
        } catch (IllegalKeyException e) {
            throw new BucketException("Illegal key error");
        }
    }


    protected void writeLXP(ILXP record_to_check, ILXP record_to_write) throws BucketException {

        if (type_label_id != -1) { // we have set a type label in this bucket there must check for consistency

            if (!(check_label_consistency(record_to_check, type_label_id))) { // Keep these separate for more error precision
                throw new BucketException("Label incompatibility");
            }
            try {
                if (!check_structural_consistency(record_to_check, type_label_id)) {
                    throw new BucketException("Structural integrity incompatibility");
                }
            } catch (IOException e) {
                throw new BucketException("I/O exception checking Structural integrity");
            }
        } else if (record_to_check.containsKey(Types.LABEL)) { // no type label on bucket but record has a type label so check structure

            try {
                if (!check_structural_consistency(record_to_check, record_to_check.getLong(Types.LABEL))) {
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
        Path path = Paths.get(this.filePath(record_to_write.getId()));

        try (Writer writer = Files.newBufferedWriter(path, FileManipulation.FILE_CHARSET)) { // auto close and exception

            objectCache.put(record_to_write.getId(), this, record_to_write);  // Putting this call here ensures that all records that are in a bucket and loaded are in the cache
            record_to_write.serializeToJSON(new JSONWriter(writer));
        } catch (IOException e) {
            throw new BucketException("I/O exception writing record");
        } catch (JSONException e) {
            throw new BucketException("JSON exception writing record");
        }
    }

    public String filePath(final long id) {
        return filePath(Long.toString(id));
    }

    protected File baseDir() {
        return directory;
    }

    protected String dirPath() {

        return repository.getRepo_path() + File.separator + name;
    }

    public String filePath(final String id) {
        return dirPath() + File.separator + id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public IRepository getRepository() {
        return this.repository;
    }

    public boolean contains(long id) {

        return Paths.get(filePath(id)).toFile().exists();
    }

    // Stream operations

    public IInputStream<T> getInputStream() throws BucketException {

        try {
            return new BucketBackedInputStream(this, directory);
        } catch (IOException e) {
            ErrorHandling.error("I/O Exception getting stream");
            throw new BucketException(e.getMessage());
        }
    }

    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream(this);
    }

    public BucketKind getKind() {
        return BucketKind.DIRECTORYBACKED;
    }

    protected void setKind(BucketKind kind) {
        Path path = directory.toPath();
        Path metapath = path.resolve("META");
        Path labelpath = metapath.resolve(kind.name());
        try {
            FileManipulation.createDirectoryIfDoesNotExist(labelpath);  // create a directory labelled with the kind in the new bucket dir
        } catch (IOException e) {
            ErrorHandling.error("I/O Exception setting kind");
        }
    }

    public static BucketKind getKind(String name, IRepository repo) {

        Path meta_path = Paths.get(repo.getRepo_path(), name, "META"); // repo/bucketname/meta

        if (Files.exists(meta_path.resolve(BucketKind.DIRECTORYBACKED.name()))) {
            return BucketKind.DIRECTORYBACKED;
        }
        if (Files.exists(meta_path.resolve(BucketKind.INDEXED.name()))) {
            return BucketKind.INDEXED;
        }
        if (Files.exists(meta_path.resolve(BucketKind.INDIRECT.name()))) {
            return BucketKind.INDIRECT;
        }
        return BucketKind.UNKNOWN;
    }

    public void setTypeLabelID(long type_label_id) throws IOException {
        if (this.type_label_id != -1) {
            throw new IOException("Type label already set");
        }
        this.type_label_id = type_label_id; // cache it and keep a persistent copy of the label.
        Path path = directory.toPath();
        Path metapath = path.resolve("META");
        FileManipulation.createDirectoryIfDoesNotExist(metapath);

        Path typepath = metapath.resolve("TYPELABEL");
        if (Files.exists(typepath)) {
            throw new IOException("Type label already set");
        }
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(Long.toString(type_label_id)); // Write the id of the typelabel LXP into this field.
            writer.newLine();
        }
    }

    //******** Private methods *********

    private long getTypeLabelID() {
        if (type_label_id != -1) {
            return type_label_id;
        } // only look it up if not cached.

        Path path = directory.toPath();
        Path typepath = path.resolve("META").resolve("TYPELABEL");

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            String id_as_string = reader.readLine();
            type_label_id = Long.parseLong(id_as_string);
            return type_label_id;

        } catch (IOException e) {
            ErrorHandling.error("I/O Exception getting labelID");
            return -1;
        }
    }

    protected ILXP create_indirection(final T record) throws IOException, JSONException, IllegalKeyException {

        long oid = record.getId();
        IBucket b = objectCache.getBucket(oid);
        IRepository r = b.getRepository();

        ILXP indirection_record = new LXP();
        indirection_record.put($INDIRECTION$, "true");
        indirection_record.put(BUCKET, b.getName());
        indirection_record.put(REPOSITORY, r.getName());
        indirection_record.put(OID, Long.toString(oid));

        return indirection_record;
    }

    private T resolve_indirection(T record) throws BucketException {

        try {

            String bucket_name = record.getString(BUCKET);
            String repo_name = record.getString(REPOSITORY);
            long oid = Long.parseLong(record.getString(OID));
            return Store.getInstance().getRepo(repo_name).getBucket(bucket_name, tFactory).get(oid);
        } catch (KeyNotFoundException e) {
            throw new BucketException("Indirection Key not found");
        } catch (RepositoryException e) {
            throw new BucketException("Repository exception");
        } catch (TypeMismatchFoundException e) {
            throw new BucketException("Type mismatch exception: " + e.getMessage());
        }
    }

    //******** Private methods *********


    public static boolean bucketExists(final String name, IRepository repo) {

        return Files.exists(getBucketPath(name, repo));
    }

    private static Path getBucketPath(final String name, IRepository repo) {

        return Paths.get(repo.getRepo_path()).resolve(name);
    }


}
