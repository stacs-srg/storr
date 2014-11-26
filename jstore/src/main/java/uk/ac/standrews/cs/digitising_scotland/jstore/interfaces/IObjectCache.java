package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

/**
 * Created by al on 25/11/14.
 */
public interface IObjectCache {
    /**
     * Adds the triple to the object cache.
     *
     * @param bucket
     * @param oid
     */

    public void put(IBucket bucket, int oid);

    /**
     * @param oid - the oid to be loooked up
     * @return the Bucket from which the oid was loaded
     */
    public IBucket getBucket(int oid);

    /**
     * @param oid - the oid to be loooked up
     * @return true if the oid is loaded
     */
    public boolean contains(int oid);
}
