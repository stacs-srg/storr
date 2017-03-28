package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.ReferenceException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.lang.ref.WeakReference;

/**
 * Created by al on 23/03/15.
 */
public class StoreReference<T extends ILXP> extends LXP implements IStoreReference {

    protected final static String $INDIRECTION$ = "$INDIRECTION$";
    protected final static String REPOSITORY = "repository";
    protected final static String BUCKET = "bucket";
    protected final static String OID = "oid";

    private static final String SEPARATOR = "/";

    private WeakReference<T> ref = null;
    private IStore store;

    /**
     * @param serialized - a String of form repo_name SEPARATOR bucket_name SEPARATOR oid
     */
    public StoreReference(String serialized, IStore store) throws ReferenceException {

        this.store = store;

        try {
            String[] tokens = serialized.split(SEPARATOR);
            put($INDIRECTION$, "true");
            put(REPOSITORY, tokens[0]);
            put(BUCKET, tokens[1]);
            put(OID, Long.parseLong(tokens[2]));
            // don't bother looking up cache reference on demand
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new ReferenceException(e.getMessage());
        }
    }

    public StoreReference(String repo_name, String bucket_name, long oid, IStore store) {

        super();
        this.store = store;
        this.put($INDIRECTION$, "true");
        this.put(REPOSITORY, repo_name);
        this.put(BUCKET, bucket_name);
        this.put(OID, oid);
        // don't bother looking up cache reference on demand or by caller
    }

    public StoreReference(IRepository repo, IBucket bucket, T reference, IStore store) {
        this(repo.getName(), bucket.getName(), reference, store);
    }

    private StoreReference(String repo_name, String bucket_name, T reference, IStore store) {
        this(repo_name, bucket_name, reference.getId(), store);
        ref = new WeakReference<T>(reference);
    }

    public StoreReference(ILXP record, IStore store) {
        this(record.getString(REPOSITORY), record.getString(BUCKET), record.getLong(OID), store);
        // don't bother looking up cache reference on demand
    }

    @Override
    public String getRepositoryName() {
        return getString(REPOSITORY);
    }

    @Override
    public String getBucketName() {
        return getString(BUCKET);
    }

    @Override
    public Long getOid() {
        return getLong(OID);
    }

    @Override
    public T getReferend() throws BucketException {

        // First see if we have a cached reference.
        if (ref != null) {
            T result = ref.get();
            if (result != null) {
                return result;
            }
        }
        try {
            return (T) store.getRepository(getRepositoryName()).getBucket(getBucketName()).getObjectById(getOid());
        } catch (RepositoryException | StoreException e) {
            throw new BucketException(e);
        }
    }

    public String toString() {
        return getRepositoryName() + SEPARATOR + getBucketName() + SEPARATOR + getOid();
    }
}
