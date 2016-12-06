package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    public DirectoryBackedIndirectBucket(String name, IRepository repository) throws IOException, RepositoryException {
        super(name, repository, BucketKind.INDIRECT);
    }

    public DirectoryBackedIndirectBucket(String name, IRepository repository, ILXPFactory<T> tFactory) throws RepositoryException {
        super(name, repository, BucketKind.INDIRECT);
    }

    @Override
    /**
     * Writes an indirection record into the file system
     */
    public void makePersistent(final T record) throws BucketException {

        try {
            // TODO delete later OLD CODE: writeLXP(record, create_indirection(record), Paths.get(this.filePath(record.getId())));
            writeLXP((ILXP) record.getThisRef(), Paths.get(this.filePath(record.getId())));
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
