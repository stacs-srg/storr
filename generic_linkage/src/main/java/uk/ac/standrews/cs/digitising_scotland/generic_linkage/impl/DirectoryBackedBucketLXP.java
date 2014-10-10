package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryBackedBucketLXP implements IBucketLXP {

    private String base_path; // the name of the parent directory in which this bucket (itself a directory) is stored.
    private String name;     // the name of this bucket - used as the directory name
    protected File directory;  // the directory implementing the bucket storage


    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the persistent directory structure already exists.
     *
     * @param name      the name of the bucket (also used as directory name)
     * @param base_path the path of the parent directory
     */
    public DirectoryBackedBucketLXP(final String name, final String base_path) throws IOException {

        this.name = name;
        this.base_path = base_path;
        String dir_name = dirPath();
        directory = new File(dir_name);
        if (!directory.isDirectory()) {
            throw new IOException("Bucket Directory: " + dir_name + " does not exist");
        }
    }

    @Override
    public ILXP get(final int id) throws PersistentObjectException, IOException {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            return new LXP(id, new JSONReader(reader));
        }
    }

    // @Override
    public void put(final ILXP record) throws IOException, JSONException {

        Path path = Paths.get(this.filePath(record.getId()));

        if (Files.exists(path)) {
            throw new IOException("File already exists - LXP records in buckets may not be overwritten");
        }

        try (Writer writer = Files.newBufferedWriter(path, FileManipulation.FILE_CHARSET)) {

            record.serializeToJSON(new JSONWriter(writer));
        }
    }

    @Override
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean contains(int id) {

        return Paths.get(filePath(id)).toFile().exists();
    }

    @Override
    public BucketKind kind() {
        return BucketKind.DIRECTORYBACKED;
    }

    // Stream operations

    @Override
    public ILXPInputStream getInputStream() {

        try {
            return new BucketBackedLXPInputStream(this,directory);
        } catch (IOException e) {
            ErrorHandling.error("I/O Exception getting stream");
            return null;
        }
    }

    @Override
    public ILXPOutputStream getOutputStream() {
        return new BucketBackedLXPOutputStream(this);
    }

    public static IBucketLXP createBucket(final String name, IRepository repo ) throws RepositoryException {
        if (bucketExists(name, null)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name, repo);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            return new DirectoryBackedBucketLXP( name, repo.getRepo_path() );

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
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

    @Override
    public void setTypeLabelID(int typeLabelID) throws IOException {
        Path path = directory.toPath();
        Path metapath = path.resolve("META");
        FileManipulation.createDirectoryIfDoesNotExist(metapath);

        Path typepath = metapath.resolve("TYPELABEL");
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (Writer writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(typeLabelID); // Write the id of the typelabel LXP into this field.
        }
    }

    @Override
    public int getTypeLabelID() {
        Path path = directory.toPath();
        Path typepath = path.resolve("META").resolve("TYPELABEL");

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            String id_as_string = reader.readLine();
            return Integer.parseInt(id_as_string);

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

}
