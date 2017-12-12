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
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.util.HashMap;

/**
 * A Factory interface that permits typed views over LXPs
 * Instances of this factory may be passed into Buckets to provide a method of typing the buckets and also
 * constructing instances of some typed interface which may be passed to, for example, typed streams of objects.
 * Created by al on 22/08/2014.
 */
public interface ILXPFactory<T extends LXP> {
    /*
     * create an instance of a <T extends LXP> from the reader
     */
    T create(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException;

    /**
     * @return the label id required by type T
     */
    long getTypeLabel();

    /**
     * @return the (expected to be shared) field map for this type.
     */
    HashMap<String,Integer> getFieldMap();
}
