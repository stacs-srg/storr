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

public class DirectoryBackedLXPBucket implements IBucketLXP {

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
    public DirectoryBackedLXPBucket(final String name, final String base_path) throws IOException {

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

    @Override
    public ITypeLabel getBucketContentType() {
        Path metapath = Paths.get(base_path).resolve("META");
        Path typepath = metapath.resolve("TYPELABEL");

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) { // Paths.get(filePath(id))

            String idasstring = reader.readLine(); // each file contains a single id field (if it exists)
            int id_of_label = Integer.parseInt(idasstring);
            ILXP rep = Store.getInstance().get(id_of_label);
            return new TypeLabel(rep);

        } catch (PersistentObjectException e1) {
                e1.printStackTrace(); // TODO fix me *******
                return null;
        } catch (IOException e) { // file is not there
            return null;
        }
    }

    // Stream operations

    @Override
    public LXPInputStream getInputStream() {

        try {
            return new LXPBucketBackedInputStream(this,directory);
        } catch (IOException e) {
            e.printStackTrace(); // todo decide how to handle
            return null;
        }
    }

    @Override
    public ILXPOutputStreamUnTypedNEW getOutputStream() {
        return new BucketBackedOutputLXPStream(this);
    }


}
