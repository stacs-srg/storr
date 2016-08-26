package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IObjectCache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by al on 25/11/14.
 * This class maintains a cache of all in-memory objects that are stored in buckets
 * Note that transient objects are not in this cache.
 * It maps from id to a Bucket in which the OID with that id is contained
 * All loaded and newly created OID instances are loaded into it.
 * <p/>
 */
public class ObjectCache implements IObjectCache {

    Map<Long, Data> map = new WeakHashMap<Long, Data>();

    public ObjectCache() {}

    /**
     * Adds the triple to the object cache.
     *  @param bucket
    //     * @param result
     * @param oid
     */
    public void put(long oid, IBucket bucket, ILXP tuple) {

        System.out.println( "** CACHE ADD ** " + oid );
        map.put(oid, new Data(bucket, tuple));
    }

    /**
     * @param oid - the oid to be loooked up
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
            System.out.println( "** CACHE MISS ** " + oid );
            return null;
        }
        System.out.println( "** CACHE HIT ** " + oid );
        return d.tuple;
    }

    private class Data {
        public IBucket bucket;
        public ILXP tuple;

        public Data(IBucket bucket, ILXP tuple) {
            this.bucket = bucket;
            this.tuple = tuple;
        }
    }

}
