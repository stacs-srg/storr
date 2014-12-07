package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IObjectCache;

import java.util.HashMap;

/**
 * Created by al on 25/11/14.
 * This class maintains a cache of all in-memory objects that are stored in buckets
 * Note that transient objects are not in this cache.
 * It maps from id to a Bucket in which the LXP with that id is contained
 * All loaded and newly created LXP instances are loaded into it.
 * <p/>
 * TODO consider cache eviction
 */
public class ObjectCache implements IObjectCache {

    HashMap<Long, IBucket> map = new HashMap<Long, IBucket>();

    public ObjectCache() {

    }

    /**
     * Adds the triple to the object cache.
     *
     * @param bucket
     * @param oid
     */
    public void put(IBucket bucket, long oid) {
        map.put(oid, bucket);
    }

    /**
     * @param oid - the oid to be loooked up
     * @return the Bucket from which the oid was loaded
     */
    public IBucket getBucket(long oid) {
        return map.get(oid);
    }

    /**
     * @param oid - the oid to be loooked up
     * @return true if the oid is loaded
     */
    public boolean contains(long oid) {
        return map.containsKey(oid);
    }

}
