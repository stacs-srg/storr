package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryBackedBucketTypedOLD<T extends ILXP> implements IBucketTypedOLD<T> {

    private String base_path; // the name of the parent directory in which this bucket (itself a directory) is stored.
    private String name;     // the name of this bucket - used as the directory name
    protected File directory;  // the directory implementing the bucket storage
    private final ILXPFactory<T> tFactory;


    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name      the name of the bucket (also used as directory name)
     * @param base_path the path of the parent directory
     */
    public DirectoryBackedBucketTypedOLD(final String name, final String base_path, ILXPFactory<T> tFactory) throws IOException {

        this.name = name;
        this.tFactory = tFactory;
        this.base_path = base_path;
        String dir_name = dirPath();
        directory = new File(dir_name);
        if (!directory.isDirectory()) {
            throw new IOException("Bucket Directory: " + dir_name + " does not exist");
        }
    }

    @Override
    public T get(final int id) throws PersistentObjectException, IOException {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            return tFactory.create(id, new JSONReader(reader));
        }
    }

    // @Override
    public void put(final T record) throws IOException, JSONException {

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
    public ILXPInputStreamTypedOld getInputStream() {

        try {
            return new BucketBackedInputStreamTypedOld(this,directory);
        } catch (IOException e) {
            e.printStackTrace(); // todo decide how to handle
            return null;
        }
    }

    @Override
    public ILXPOutputStreamTypedOLD getOutputStream() {
        return new BucketBackedOutputStreamOLD(this);
    }


}
