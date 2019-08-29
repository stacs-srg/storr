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

import uk.ac.standrews.cs.storr.impl.PersistentObject;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

/**
 * Provides the interface to an output stream of labelled cross product records.
 * Created by al on 28/04/2014.
 */
public interface IOutputStream<T extends PersistentObject> {

    /**
     * Add a record to the stream
     *
     * @param record - the record to be added to a stream
     * @throws BucketException if one is thrown during the underlying $$$bucket$$$bucket$$$ operations
     */
    void add(T record) throws BucketException;
}
