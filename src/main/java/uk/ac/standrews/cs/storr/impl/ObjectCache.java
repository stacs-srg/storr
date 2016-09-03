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

    Map<Long, Data> map = new WeakHashMap<Long, Data>();

    public ObjectCache() {}

    /**
     * Adds the data to the object cache.
     * @param oid of the record being added to the cache
     * @param bucket the bucket from which the object came.
     * @param tuple the tuple added to the cache
     */
    public void put(long oid, IBucket bucket, ILXP tuple) {

//        Diagnostic.trace( "** CACHE ADD ** " + oid + " tuple: " + tuple + " class: " + tuple.getClass().getName());
        map.put(oid, new Data(bucket, tuple));
    }

    /**
     * @param oid - the oid to be looked up
     * @return the Bucket from which the oid was loaded
     */
    public IBucket getBucket(long oid) {
        Data d = map.get(oid);
        if (d == null) {
            return null;
        }
        return d.bucket;
    }

    /**
     * @param oid - the oid to be loooked up
     * @return true if the oid is loaded
     */
    public boolean contains(long oid) {
        return map.containsKey(oid);
    }

    @Override
    public ILXP getObject(long oid) {
        Data d = map.get(oid);
        if (d == null) {
//            Diagnostic.trace( "** CACHE MISS ** " + oid );
            return null;
        }
//        Diagnostic.trace( "** CACHE HIT ** " + oid + " tuple: " + d.tuple + " class: " + d.tuple.getClass().getName());
        return d.tuple;
    }

    /*
     * This private class is used to track tuples stored in buckets
     */
    private class Data {
        public IBucket bucket;
        public ILXP tuple;

        public Data(IBucket bucket, ILXP tuple) {
            this.bucket = bucket;
            this.tuple = tuple;
        }
    }

}