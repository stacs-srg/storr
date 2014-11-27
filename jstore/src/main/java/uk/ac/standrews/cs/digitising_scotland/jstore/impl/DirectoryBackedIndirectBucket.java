package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
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
    public void put(final T record) throws BucketException {

        try {
            writeLXP(record, create_indirection(record));
        } catch (IOException | JSONException e) {
            throw new BucketException("Error creating indirection");
        }

    }


    @Override
    public IInputStream<T> getInputStream() throws BucketException {
        try {
            return new BucketBackedInputStream(this, directory);
        } catch (IOException e) {
            throw new BucketException("I/O exception getting stream");
        }
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
