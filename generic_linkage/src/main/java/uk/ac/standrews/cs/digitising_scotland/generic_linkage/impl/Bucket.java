package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class Bucket implements IBucket {

    private String base_path; // the name of the parent directory in which this bucket (itself a directory) is stored.
    private String name;     // the name of this bucket - used as the directory name
    protected File directory;  // the directory implementing the bucket storage

    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name      the name of the bucket (also used as directory name)
     * @param base_path the path of the parent directory
     */
    public Bucket(final String name, final String base_path) throws IOException {

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

    @Override
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
    public ILXPInputStream getInputStream() {

        return new BucketBackedInputStream();
    }

    @Override
    public ILXPOutputStream getOutputStream() {
        return new BucketBackedOutputStream(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean contains(int id) {

        return Paths.get(filePath(id)).toFile().exists();
    }

    private Iterator<File> getFileIterator() {
        return FileIteratorFactory.createFileIterator(directory, true, false);
    }

    private class BucketBackedInputStream implements ILXPInputStream {

        private Iterator<File> file_iterator;

        public BucketBackedInputStream() {
            file_iterator = getFileIterator();
        }

        public Iterator<ILXP> iterator() {
            return new ILXPIterator();
        }

        private class ILXPIterator implements Iterator<ILXP> {

            public boolean hasNext() {
                return file_iterator.hasNext();
            }

            @Override
            public ILXP next() {

                try {
                    return get(Integer.parseInt(file_iterator.next().getName()));

                } catch (PersistentObjectException | IOException e) {
                    ErrorHandling.exceptionError(e, "Exception in iterator");
                    return null;
                }
            }

            @Override
            public void remove() {
                ErrorHandling.error("remove called on stream - unsupported");
                throw new UnsupportedOperationException("remove called on stream - unsupported");
            }
        }
    }
}
