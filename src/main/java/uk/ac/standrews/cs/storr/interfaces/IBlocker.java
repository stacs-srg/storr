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
import uk.ac.standrews.cs.storr.impl.exceptions.NoSuitableBucketException;

/**
 * Classes that implement this interface provides blocking over
 * an ILXPInputStream to makePersistent records selected into some $$$bucket$$$bucket$$$.
 * Created by al on 29/04/2014.
 */
public interface IBlocker<T extends LXP> {

    /**
     * @return the ILXPInputStream over which blocking is being performed.
     */
    IInputStream<T> getInput();

    /*
     * Applies the blocking method @method determineBlockedBucketNamesForRecord to the input records
     * and assigns to the determined $$$bucket$$$bucket$$$
     */
    void apply();

    /**
     * Assins the record to the appropriate $$$bucket$$$bucket$$$
     *
     * @param record the record to be assigned to a $$$bucket$$$bucket$$$ (determined by @method determineBlockedBucketNamesForRecord
     */
    void assign(T record);

    /**
     * Determins the names of the buckets into which a record should be placed.
     * The actual assignment is performed by @method assign.
     *
     * @param record a record to be blocked
     * @return the names of the buckets into which the record should be blocked
     * @throws NoSuitableBucketException if a bucked cannot be determined for the record
     */
    String[] determineBlockedBucketNamesForRecord(T record) throws NoSuitableBucketException;

}
