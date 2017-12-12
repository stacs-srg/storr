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

import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;

/**
 * Created by al on 27/04/2017.
 */
public class Tuple<T extends LXP> extends DynamicLXP {

    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";

    public Tuple(String key, IStoreReference<T> ref) throws PersistentObjectException {
        this.put( KEY,key );
        this.put( VALUE, ref );
    }

    public Tuple(String key, T value) throws PersistentObjectException {
        this( key, value.getThisRef() );
    }

}
