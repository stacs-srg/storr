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
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This class implements a index over indexed buckets.
 * Instances of this class are returned by the @methods getIndex of the classes implementing the  @interface IIndexedBucket
 * Created by al on 23/05/2014.
 */
public interface IBucketIndex<T extends LXP> {

    /**
     * @return the key set of this index
     * @throws IOException     in the event of an IO exception
     * @throws BucketException if one is thrown during the underlying $$$bucket$$$bucket$$$ operations
     */
    Set<String> keySet() throws IOException, BucketException;

    /**
     * @param value - the value to be extracted from the index (e.g. "al" from a name labelled field)
     * @return a stream of records which (exactly) contain the specified value.
     * @throws IOException     in the event of an IO exception
     * @throws BucketException if one is thrown during the underlying $$$bucket$$$bucket$$$ operations
     */
    IInputStream<T> records(String value) throws IOException, BucketException;

    /**
     * @param value - the value
     * @return the list of record ids that (exactly) contain the specified value e.g. "al" from a set of names
     * @throws IOException     if exception is thrown during operations
     * @throws BucketException if one is thrown during the underlying $$$bucket$$$bucket$$$ operations
     */
    List<Long> values(String value) throws IOException, BucketException;

    /**
     * Adds a record to the index.
     *
     * @param record the record to be added
     * @throws IOException     if exception is thrown during operations
     * @throws BucketException if one is thrown during the underlying $$$bucket$$$bucket$$$ operations
     */
    void add(T record) throws IOException, BucketException;
}
