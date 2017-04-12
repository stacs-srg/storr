package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IObjectCache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by al on 25/11/14.
 * This class maintains a cache of all in-memory objects that are stored in buckets
 * Note that transient objects are not in this cache.
 * It maps from id to a Bucket in which the OID with that id is contained
 * All loaded and newly created OID instances are loaded into it.
 */
public class ObjectCache implements IObjectCache {

    private final Map<Long, Data> map = new WeakHashMap<>();

    /**
     * Adds the data to the object cache.
     *
     * @param oid    of the record being added to the cache
     * @param bucket the bucket from which the object came.
     * @param tuple  the tuple added to the cache
     */
    public void put(long oid, IBucket bucket, ILXP tuple) {

        map.put(oid, new Data(bucket, tuple));
    }

    /**
     * @param oid - the oid to be looked up
     * @return true if the oid is loaded
     */
    public boolean contains(long oid) {
        return map.containsKey(oid);
    }

    /**
     * @param oid - the oid to be looked up
     * @return the Bucket from which the oid was loaded
     */
    public IBucket getBucket(long oid) {

        Data data = map.get(oid);
        return data == null ? null : data.bucket;
    }

    @Override
    public ILXP getObject(long oid) {

        Data data = map.get(oid);
        return (data == null) ? null : data.tuple;
    }

    /*
     * This private class is used to track tuples stored in buckets
     */
    static class Data {

        IBucket bucket;
        ILXP tuple;

        Data(IBucket bucket, ILXP tuple) {
            this.bucket = bucket;
            this.tuple = tuple;
        }
    }
}
