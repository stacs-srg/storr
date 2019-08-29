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

import java.io.IOException;

/**
 * Augments the functionality of a $$$bucket$$$bucket$$$ with indexes.
 * Indexes are double indexes: label first and then key value associated with that label.
 * Thus if you have a $$$bucket$$$bucket$$$ containing [name: string, age: int] tuples you can create an index
 * over the labels - say name and then getString the index of all people called "al".
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucket<T extends LXP> extends IBucket<T> {

    /**
     * @param label - the label to add - for example "name" will add an index of names over records such as [name: string, age: int]
     * @throws IOException if an IO exception occurs in the underlying implementation
     */
    void addIndex(String label) throws IOException;

    /**
     * @param label - the label over which you wish to acquire the index
     * @return the index associated with the label or null if there isn't one.
     */
    IBucketIndex<T> getIndex(String label);
}
