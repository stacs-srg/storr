package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    public DirectoryBackedIndirectBucket(IRepository repository, String bucket_name) throws IOException, RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT);
    }

    public DirectoryBackedIndirectBucket(IRepository repository, String bucket_name, ILXPFactory<T> tFactory) throws RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT, tFactory);
    }

    @Override
    /**
     * Writes an indirection record into the file system
     */
    public void makePersistent(final T record) throws BucketException {

        try {
            writeLXP((ILXP) record.getThisRef(), filePath(record.getId()));
        } catch ( IllegalKeyException | PersistentObjectException e) {
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
