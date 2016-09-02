package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.ReferenceException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.util.ErrorHandling;

/**
 * Created by al on 23/03/15.
 */
public class StoreReference<T extends ILXP> extends LXP implements IStoreReference {

    protected final static String $INDIRECTION$ = "$INDIRECTION$";
    protected final static String REPOSITORY = "repository";
    protected final static String BUCKET = "bucket";
    protected final static String OID = "oid";

    private static final String SEPARATOR = "/";

    /**
     * @param serialized - a String of form repo_name SEPARATOR bucket_name SEPARATOR oid
     */
    public StoreReference(String serialized) throws ReferenceException {
        try {
            String[] tokens = serialized.split(SEPARATOR);
            this.put($INDIRECTION$, "true");
            this.put(REPOSITORY, tokens[0]);
            this.put(BUCKET, tokens[1]);
            this.put(OID, new Long(tokens[2]));
        } catch( ArrayIndexOutOfBoundsException | NumberFormatException e ) {
            throw new ReferenceException(e.getMessage());
        }
    }

    public StoreReference( String repo_name, String bucket_name, long oid ) {
        super();
        this.put($INDIRECTION$, "true");
        this.put(REPOSITORY, repo_name );
        this.put(BUCKET, bucket_name);
        this.put(OID, oid);
    }

    public StoreReference( String repo_name, String bucket_name, T reference ) {
        super();
        this.put($INDIRECTION$, "true");
        this.put(REPOSITORY, repo_name );
        this.put(BUCKET, bucket_name);
        this.put(OID, reference.getId());
    }

    public StoreReference( IRepository repo, IBucket bucket, long oid ) {
        this(repo.getName(), bucket.getName(), oid);
    }

    public StoreReference(IRepository repo, IBucket bucket, T reference) {
        this(repo.getName(), bucket.getName(), reference.getId());
    }

    public StoreReference(ILXP record)  {
        this(record.getString(REPOSITORY), record.getString(BUCKET), record.getLong(OID));
    }

    @Override
    public String getRepoName() {
        return this.getString(REPOSITORY);
    }

    @Override
    public String getBucketName() {
        return this.getString(BUCKET);
    }

    @Override
    public Long getOid() {
        return this.getLong(OID);
    }

    @Override
    public T getReferend() throws BucketException {

        IStore store = null;
        try {
            store = StoreFactory.getStore();

            T reference = getCachedObjectReference(store);

            if (reference == null) { // didn't find object in cache
                reference = getRepoObjectReference(store);

                if (reference == null) { // still not found it.
                    ErrorHandling.error("Returning null: cannot resolve reference to LXP: " + this.toString());
                    return null;
                }
            }
            return reference;

        } catch (StoreException  e1) {
            throw new BucketException(e1.getMessage());
        } catch(RepositoryException e2) {
            throw new BucketException(e2.getMessage());
        }
    }

    public String toString() {
        return getRepoName() + SEPARATOR + getBucketName() + SEPARATOR + getOid();
    }

    private T getCachedObjectReference(IStore store) throws StoreException {
        return (T) store.getObjectCache().getObject(getOid());
    }

    private T getRepoObjectReference(IStore store) throws RepositoryException, BucketException {
        return (T) store.getRepo(getRepoName()).getBucket(getBucketName()).getObjectById(getOid());
    }
}
