package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.impl.exceptions.ReferenceException;
import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.jstore.interfaces.IStoreReference;

/**
 * Created by al on 23/03/15.
 */
public class StoreReference extends LXP implements IStoreReference {

    public final static String $INDIRECTION$ = "$INDIRECTION$";
    protected final static String REPOSITORY = "repository";
    protected final static String BUCKET = "bucket";
    protected final static String OID = "oid";

    // AL here

    private static final String SEPARATOR = "/";

    /**
     * @param serialized - a String of form repo_name SEPARATOR bucket_name SEPARATOR oid
     */
    public StoreReference( String serialized ) throws ReferenceException {
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
        this.put(REPOSITORY, repo_name);
        this.put(BUCKET, bucket_name);
        this.put(OID, oid);
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

    public String toString() {
        return getRepoName() + SEPARATOR + getBucketName() + SEPARATOR + getOid();
    }

    public ILXP get_referend() throws BucketException {
        try {
            return StoreFactory.getStore().getRepo(getRepoName()).getBucket(getBucketName()).getObjectById(getOid());
        } catch (BucketException | RepositoryException | StoreException e) {
            throw new BucketException( e );
        }
    }

    public <T extends ILXP> T get_referend( ILXPFactory<T> tFactory ) throws BucketException {
        try {
            return StoreFactory.getStore().getRepo(getRepoName()).getBucket(getBucketName(), tFactory).getObjectById(getOid());
        } catch (BucketException | RepositoryException | StoreException e) {
            throw new BucketException( e );
        }
    }
}
