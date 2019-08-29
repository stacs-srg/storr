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
import uk.ac.standrews.cs.storr.impl.LXPReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by al on 09/06/2017.
 */
public interface IStringtoILXPMap<T extends LXP> {
    T lookup(String key) throws IOException, BucketException, RepositoryException;

    void put(String key, IStoreReference<T> value) throws PersistentObjectException, BucketException;

    void injestRefMap(Map<String, LXPReference<T>> map) throws PersistentObjectException, BucketException;

    void injestMap(Map<String, T> map) throws PersistentObjectException, BucketException;
}
