package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndexedBucket<T extends ILXP> extends DirectoryBackedBucket<T> implements IIndexedBucket<T> {

    private Map<String, IBucketIndex> indexes = new HashMap<>();

    private static final String INDEX = "INDEX";
    private static final String INDEX_DIR_NAME = "INDICES";

    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name       the name of the bucket (also used as directory name).
     * @param repository the repository in which the bucket is created.
     */
    public DirectoryBackedIndexedBucket(final String name, final IRepository repository) throws RepositoryException {
        super(name, repository, BucketKind.INDEXED);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public DirectoryBackedIndexedBucket(final String name, final IRepository repository, ILXPFactory tFactory) throws RepositoryException {
        super(name, repository, BucketKind.INDEXED);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private void initIndexes() throws IOException {

        String dirname = dirPath();
        // Ensure that the index directory exists
        File index = new File(dirname + File.separator + INDEX_DIR_NAME);
        if (!index.isDirectory() && !index.mkdir()) {
            throw new IOException("Index Directory: " + dirname + " does not exist and cannot create");
        }

        Iterator<File> iterator = FileIteratorFactory.createFileIterator(index, true, false);
        while (iterator.hasNext()) {
            File next = iterator.next();
            indexes.put(next.getName(), new BucketIndex(next.getName(), next.toPath(), this));
        }
    }

    @Override
    public void addIndex(final String label) throws IOException {

        Path path = Paths.get(this.filePath(INDEX_DIR_NAME + "/" + INDEX + label));

        if (Files.exists(path)) {
            throw new IOException("index exists");
        } else {
            Files.createDirectory(path); // create a directory to store the index
            indexes.put(label, new BucketIndex(label, path, this)); // keep the in memory index list up to date
        }
    }

    @Override
    public IBucketIndex getIndex(final String label) {
        return indexes.get(label);
    }


    @Override
    public void put(final T record) throws BucketException {

        Set<String> keys = indexes.keySet(); // all the keys currently being indexed
        for (String key : keys) {
            if (record.containsKey(key)) { // we are indexing this key
                IBucketIndex index = indexes.get(key); // so getString the index
                try {
                    index.add(record); // and add this record to the index for that key
                } catch (IOException e) {
                    throw new BucketException("I/O exception adding index");
                }
            }
        }
        super.put(record);
    }


    public IInputStream getInputStream() throws BucketException {
        // We already know that the type is compatible - checked in constructor.
        try {
            return new BucketBackedInputStream(this, this.directory);
        } catch (IOException e) {
            throw new BucketException("I/O exception getting stream");
        }
    }


    public IOutputStream getOutputStream() {
        // We already know that the type is compatible - checked in constructor.
        return new BucketBackedOutputStream(this);
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDEXED;
    }


}
