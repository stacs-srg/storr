package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;

public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    DirectoryBackedIndirectBucket(IRepository repository, String bucket_name, boolean create_bucket) throws RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT, create_bucket);
    }

    DirectoryBackedIndirectBucket(IRepository repository, String bucket_name, ILXPFactory<T> tFactory, boolean create_bucket) throws RepositoryException {
        super(repository, bucket_name, BucketKind.INDIRECT, tFactory, create_bucket);
    }

    @Override
    // Writes an indirection record into the file system
    public void makePersistent(final T record) throws BucketException {

        try {
            writeLXP((ILXP) record.getThisRef(), filePath(record.getId()));
        } catch (IllegalKeyException | PersistentObjectException e) {
            throw new BucketException("Error creating indirection");
        }
    }

    @Override
    public IInputStream<T> getInputStream() throws BucketException {
        try {
            return new BucketBackedInputStream(this);

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
