package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
     * @param repository the repository in which the bucket is created.
     * @param bucket_name       the name of the bucket (also used as directory name).
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    public DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name) throws RepositoryException {

        super(repository, bucket_name, BucketKind.INDEXED);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name, ILXPFactory tFactory) throws RepositoryException {

        super(repository, bucket_name, BucketKind.INDEXED, tFactory);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private void initIndexes() throws IOException {

        // Ensure that the index directory exists
        File index = dirPath().resolve(INDEX_DIR_NAME).toFile();
        if (!index.isDirectory() && !index.mkdir()) {
            throw new IOException("Index Directory: " + dirPath() + " does not exist and cannot create");
        }

        Iterator<File> iterator = new FileIterator(index, true, false);
        while (iterator.hasNext()) {
            File next = iterator.next();
            indexes.put(next.getName(), new BucketIndex(next.getName(), next.toPath(), this));
        }
    }

    @Override
    public void addIndex(final String label) throws IOException {

        Path path = dirPath().resolve(INDEX_DIR_NAME).resolve(INDEX + label);

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
    public void makePersistent(final T record) throws BucketException {

        for (Map.Entry<String, IBucketIndex> entry : indexes.entrySet()) {

            String key = entry.getKey();
            IBucketIndex index = entry.getValue();

            if (record.containsKey(key)) { // we are indexing this key

                try {
                    index.add(record); // and add this record to the index for that key
                } catch (IOException e) {
                    throw new BucketException("I/O exception adding index");
                }
            }

        }

        super.makePersistent(record);
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
