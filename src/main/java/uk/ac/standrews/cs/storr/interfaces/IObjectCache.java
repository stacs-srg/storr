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
package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.LXP;

/**
 * This interface represents the type of the caches used to store in memory objects.
 * It permits the association between buckets, tuples and object ids to be maintained.
 * Created by al on 25/11/14.
 */
public interface IObjectCache {
    /**
     * Adds the $$$bucket$$$bucket$$$ X oid tuple to the object cache.
     *
     * @param oid    - the object $$$$id$$$$id$$$$ of the registered object
     * @param bucket - the $$$bucket$$$bucket$$$ of the registered object
     * @param tuple  - the tuple to add.
     */
    void put(long oid, IBucket bucket, LXP tuple);

    /**
     * @param oid - the oid to be loooked up
     * @return the Bucket from which the oid was loaded
     */
    IBucket getBucket(long oid);

    /**
     * @param oid - the oid to be loooked up
     * @return true if the oid is loaded
     */
    boolean contains(long oid);

    /**
     * @param id - the $$$$id$$$$id$$$$ of the tuple to be looked up
     * @return the tuple with the given $$$$id$$$$id$$$$ if it exists in the cache and null otherwise
     */
    LXP getObject(long id);
}
