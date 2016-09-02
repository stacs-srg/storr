package uk.ac.standrews.cs.storr.interfaces;

/**
 * This interface represents the type of the caches used to store in memory objects.
 * It permits the association between buckets, tuples and object ids to be maintained.
 * Created by al on 25/11/14.
 */
public interface IObjectCache {
    /**
     * Adds the bucket X oid tuple to the object cache.
     * @param oid    - the object id of the registered object
     * @param bucket - the bucket of the registered object
     * @param tuple - the tuple to add.
     */
    public void put(long oid, IBucket bucket, ILXP tuple);

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

    /**
     * @param id - the id of the tuple to be looked up
     * @return the tuple with the given id if it exists in the cache and null otherwise
     */
    ILXP getObject(long id);
}
