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

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.util.Map;

/**
 * Created by al on 27/04/2017.
 */
public class IdtoILXPMap<T extends ILXP> implements IIdtoILXPMap<T> {

    private final StringtoILXPMap<T> pmap;
    private final IRepository repository;

    /**
     * Creates a handle on a persistent map, implemented by an IndexedBucket
     * Assumes that persistent map has been created already using a factory - i.e. the directory already exists.
     *
     * @param repository the repository in which the bucket is created.
     * @param map_name   the name of the map/bucket (also used as directory name).
     * @param tFactory
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    IdtoILXPMap(final String map_name, final IRepository repository, ILXPFactory<T> tFactory, boolean create_map) throws RepositoryException {
        pmap = new StringtoILXPMap<>( map_name, repository, BucketKind.IDMAP, tFactory, create_map );
        this.repository = repository;
    }

    @Override
    public ILXP lookup(Long id) throws IOException, BucketException, RepositoryException {

        return pmap.lookup( id.toString() );
    }

    @Override
    public void put(Long key, IStoreReference<T> value) throws PersistentObjectException, BucketException {
        pmap.put( key.toString(), value );
    }

    @Override
    public void injestRefMap(Map<Long, StoreReference<T>> map) throws BucketException, PersistentObjectException {
        for (Map.Entry<Long, StoreReference<T>> entry : map.entrySet()) {
            put( entry.getKey(), entry.getValue() );
        }
    }


    @Override
    public void injestMap(Map<Long, T> map) throws BucketException, PersistentObjectException {
        for (Map.Entry<Long,T> entry : map.entrySet()) {
            put( entry.getKey(), entry.getValue().getThisRef() );
        }
    }



}