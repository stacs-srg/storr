/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IObjectCache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by al on 25/11/14.
 * This class maintains a cache of all in-memory objects that are stored in buckets
 * Note that transient objects are not in this cache.
 * It maps from $$$$id$$$$id$$$$ to a Bucket in which the OID with that $$$$id$$$$id$$$$ is contained
 * All loaded and newly created OID instances are loaded into it.
 */
public class ObjectCache implements IObjectCache {

    private final Map<Long, Data> map = new WeakHashMap<>();

    /**
     * Adds the data to the object cache.
     *
     * @param oid    of the record being added to the cache
     * @param bucket the $$$bucket$$$bucket$$$ from which the object came.
     * @param tuple  the tuple added to the cache
     */
    public void put(long oid, IBucket bucket, LXP tuple) {

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
    public LXP getObject(long oid) {

        Data data = map.get(oid);
        return (data == null) ? null : data.tuple;
    }

    /*
     * This private class is used to track tuples stored in buckets
     */
    static class Data {

        IBucket bucket;
        LXP tuple;

        Data(IBucket bucket, LXP tuple) {
            this.bucket = bucket;
            this.tuple = tuple;
        }
    }
}
