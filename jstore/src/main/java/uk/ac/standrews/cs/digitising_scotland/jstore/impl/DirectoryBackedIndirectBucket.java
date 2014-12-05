package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;

import java.io.IOException;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    public DirectoryBackedIndirectBucket(String name, IRepository repository) throws IOException, RepositoryException {

        super(name, repository);
        setKind(BucketKind.INDIRECT);
    }

    public DirectoryBackedIndirectBucket(String name, IRepository repository, ILXPFactory<T> tFactory) throws RepositoryException {
        super(name, repository, tFactory);
        setKind(BucketKind.INDIRECT);
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
