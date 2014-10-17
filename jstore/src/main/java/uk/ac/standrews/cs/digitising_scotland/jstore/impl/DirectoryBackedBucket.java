package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by al on 19/09/2014.
 */
public class DirectoryBackedBucket<T extends ILXP> implements IBucket<T> {

    protected ILXPFactory<T> tFactory = null;
    private String base_path; // the name of the parent directory in which this bucket (itself a directory) is stored.
    private String name;     // the name of this bucket - used as the directory name
    protected File directory;  // the directory implementing the bucket storage
    private int type_label_id = -1; // not set

    /*
     * Creates a DirectoryBackedBucket with no factory - a persistent collection of ILXPs
     */
    public DirectoryBackedBucket(final String name, final String base_path) throws RepositoryException, IOException {
        this.name = name;
        this.base_path = base_path;
        String dir_name = dirPath();
        directory = new File(dir_name);

        if (!directory.isDirectory()) {
            throw new IOException("Bucket Directory: " + dir_name + " does not exist");
        }
    }


    public DirectoryBackedBucket(final String name, final String base_path, ILXPFactory<T> tFactory) throws RepositoryException, IOException {
        this(name,base_path);
        this.tFactory = tFactory;
        int type_label_id = this.getTypeLabelID();
        if (type_label_id == -1) { // no types associated with this bucket.
            throw new RepositoryException("no type label associated with bucket");
        }
        try {
            if (!tFactory.checkConsistentWith(type_label_id)) {
                throw new RepositoryException("incompatible types");
            }
        } catch (PersistentObjectException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public static IBucket createBucket(final String name, IRepository repo ) throws RepositoryException  {
        if (bucketExists(name, repo)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name, repo);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            return new DirectoryBackedBucket(name, repo.getRepo_path() );
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    public static IBucket createBucket(final String name, IRepository repo, ILXPFactory tFactory ) throws RepositoryException  {
        try {
            IBucket bucket = createBucket(name, repo);
            bucket.setTypeLabelID( tFactory.getLabel() );

            return new DirectoryBackedBucket(name, repo.getRepo_path(), tFactory);
        } catch (IOException e) {
                throw new RepositoryException(e.getMessage());
        }
    }

    public T get(int id) throws IOException, PersistentObjectException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            if( tFactory == null ) { //  No java type specified
                return (T) ( new LXP(id, new JSONReader(reader) ) ); // TODO is this legal??? - talk to Graham!
            } else {
                return tFactory.create(id, new JSONReader(reader));
            }
        }
    }

    public void put(final T record) throws IOException, JSONException {

        Path path = Paths.get(this.filePath(record.getId()));

        if (Files.exists(path)) {
            throw new IOException("File already exists - LXP records in buckets may not be overwritten");
        }

        try (Writer writer = Files.newBufferedWriter(path, FileManipulation.FILE_CHARSET)) {

            record.serializeToJSON(new JSONWriter(writer));
        }
    }

    public String filePath(final int id) {
        return filePath(Integer.toString(id));
    }

    protected File baseDir() {
        return directory;
    }

    protected String dirPath() {

        return base_path + File.separator + name;
    }

    public String filePath(final String id) {
        return dirPath() + File.separator + id;
    }

    public String getName() {
        return this.name;
    }

    public boolean contains(int id) {

        return Paths.get(filePath(id)).toFile().exists();
    }


    // Stream operations

    public IInputStream<T> getInputStream() throws IOException {

        try {
            return new BucketBackedInputStream(this,directory);
        } catch (IOException e) {
            ErrorHandling.error("I/O Exception getting stream");
            return null;
        }
    }

    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream(this);
    }

    public BucketKind getKind() {
        return BucketKind.DIRECTORYBACKED;
    }

    public void setKind( BucketKind kind  ) {
        Path path = directory.toPath();
        Path metapath = path.resolve("META");
        Path labelpath = metapath.resolve(kind.name());
        try {
            FileManipulation.createDirectoryIfDoesNotExist(labelpath);  // create a directory labelled with the kind in the new bucket dir
        } catch (IOException e) {
            ErrorHandling.error("I/O Exception setting kind");
        }
    }

    public void setTypeLabelID(int type_label_id) throws IOException {
        if( this.type_label_id != -1 ) {
            throw new IOException( "Type label already set");
        }
        this.type_label_id = type_label_id; // cache it and keep a persistent copy of the label.
        Path path = directory.toPath();
        Path metapath = path.resolve("META");
        FileManipulation.createDirectoryIfDoesNotExist(metapath);

        Path typepath = metapath.resolve("TYPELABEL");
        if( Files.exists(typepath) ) {
            throw new IOException( "Type label already set");
        }
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(Integer.toString(type_label_id)); // Write the id of the typelabel LXP into this field.
            writer.newLine();
        }
    }

    public int getTypeLabelID() {
        if( type_label_id != -1 ) { return type_label_id; } // only look it up if not cached.

        Path path = directory.toPath();
        Path typepath = path.resolve("META").resolve("TYPELABEL");

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            String id_as_string = reader.readLine();
            type_label_id = Integer.parseInt(id_as_string);
            return type_label_id;

        } catch (IOException e) {
            ErrorHandling.error( "I/O Exception getting labelID");
            return -1;
        }
    }


    //******** Private methods *********

    public static boolean bucketExists(final String name, IRepository repo) {

        return Files.exists(getBucketPath(name, repo));
    }

    private static Path getBucketPath(final String name, IRepository repo) {

        return Paths.get(repo.getRepo_path()).resolve(name);
    }


    public static BucketKind getKind(String name, String repo_filename) {

        // TODO look at usages of Paths and filenames and tidy up

        Path meta_path = Paths.get( repo_filename,name,"META" ); // repo/bucketname/meta

        if( Files.exists( meta_path.resolve( BucketKind.DIRECTORYBACKED.name() ) ) ) {
            return BucketKind.DIRECTORYBACKED;
        }
        if( Files.exists( meta_path.resolve( BucketKind.INDEXED.name() ) ) ) {
            return BucketKind.INDEXED;
        }
        if( Files.exists( meta_path.resolve( BucketKind.INDIRECT.name() ) ) ) {
            return BucketKind.INDIRECT;
        }
        return BucketKind.UNKNOWN;

    }
}
