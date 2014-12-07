package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

/**
 * This interface represents the type of the caches used to store in memory objects.
 * It permits the association between buckets and object ids to be maintained.
 * Created by al on 25/11/14.
 */
public interface IObjectCache {
    /**
     * Adds the bucket X oid tuple to the object cache.
     *
     * @param bucket - the bucket of the registered object
     * @param oid    - the object id of the registered object
     */
    public void put(IBucket bucket, long oid);

    /**
     * @param oid - the oid to be loooked up
     * @return the Bucket from which the oid was loaded
     */
    public IBucket getBucket(long oid);

    /**
     * @param oid - the oid to be loooked up
     * @return true if the oid is loaded
     */
    public boolean contains(long oid);
}
