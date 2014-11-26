package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.io.IOException;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    public DirectoryBackedIndirectBucket(String name, IRepository repository) throws IOException, RepositoryException {
        super(name, repository);
    }

    public DirectoryBackedIndirectBucket(String name, IRepository repository, ILXPFactory<T> tFactory) throws IOException, RepositoryException {
        super(name, repository, tFactory);
    }

    public static <T extends ILXP> IBucket<T> createBucket(String name, Repository repository, ILXPFactory<T> tFactory) throws RepositoryException {

        try {
            DirectoryBackedBucket.createBucket(name, repository, tFactory);
            return new DirectoryBackedIndirectBucket(name, repository, tFactory);
        } catch (IOException e) {

            ErrorHandling.error("I/O Exception creating bucket");
            return null;
        }
    }

    @Override
    /**
     * Writes an indirection record into the file system
     */
    public void put(final T record) throws IOException, JSONException {

        writeLXP(record, create_indirection(record));

// OLD VERSION
//        Path path = Paths.get(filePath(record.getId()));
//
//        if (Files.exists(path)) {
//            throw new IOException("File already exists - LXP records in buckets may not be overwritten");
//        }
//
//        // create a file containing nothing whose name is the id of the record
//
//        Path p = Files.createFile(Paths.get(filePath(record.getId())), null);
//
//        // Sym link could be created by finding the original at this point and putting it in - but may not work on all platforms?

    }

    // No need to overwrite get since the code in DirectoryBackedBucket can handle indirections.

// public T get(final int id) throws PersistentObjectException, IOException {
// OLD VERSION
//        if (Files.exists(Paths.get(filePath(id)), NOFOLLOW_LINKS)) {
//
//            return Store.getInstance().get(id); // go find the record where ever it is (
//            // Store.getInstance().getObjectCache().getBucket(id).get(id);
//            // TODO need to rewrite this.
//
//        } else {
//            throw new PersistentObjectException("Record does not exist in indexed bucket");
//        }
//        return null;
//    }


    @Override
    public IInputStream<T> getInputStream() throws IOException {
        return new BucketBackedInputStream(this, directory);
    }


    @Override
    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream(this);
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDIRECT;
    }


}
