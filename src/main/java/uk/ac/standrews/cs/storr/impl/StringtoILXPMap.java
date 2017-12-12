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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by al on 27/04/2017.
 */
public class StringtoILXPMap<T extends LXP> implements IStringtoILXPMap<T> {

    private final DirectoryBackedIndexedBucket<Tuple<T>> bucket; // bucket used to store the field_storage.
    private final IRepository repository;

    /**
     * Creates a handle on a persistent field_storage, implemented by an IndexedBucket
     * Assumes that persistent field_storage has been created already using a factory - i.e. the directory already exists.
     *
     * @param repository the repository in which the bucket is created.
     * @param map_name   the name of the field_storage/bucket (also used as directory name).
     * @param bucketType
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    public StringtoILXPMap(final String map_name, final IRepository repository,  Class<T> bucketType, boolean create_map) throws RepositoryException {
        this( map_name, repository, BucketKind.STRINGMAP, bucketType, create_map );
    }

    protected StringtoILXPMap(final String map_name, final IRepository repository, BucketKind kind, Class<T> bucketType, boolean create_map) throws RepositoryException {

            bucket = new DirectoryBackedMapBucket(repository, map_name, kind, bucketType, create_map);
            this.repository = repository;
    }

    @Override
    public T lookup(String key) throws IOException, BucketException, RepositoryException {
        IInputStream<LXP> values = bucket.getIndex(Tuple.KEY).records(key);

        Iterator<LXP> iter = values.iterator();

        if( iter.hasNext() ) {

            LXP value = iter.next();

            if( iter.hasNext() ) {
                throw new IOException("Multiple values found for key: " + key);
            }

            IStoreReference ref = (IStoreReference) value.get(Tuple.VALUE);
            return (T) ref.getReferend();

        } else return null;
    }

    @Override
    public void put(String key, IStoreReference<T> value) throws PersistentObjectException, BucketException {
        bucket.makePersistent( new Tuple<T>( key, value ) );
    }

    @Override
    public void injestRefMap(Map<String, StoreReference<T>> map) throws BucketException, PersistentObjectException {
        for (Map.Entry<String, StoreReference<T>> entry : map.entrySet()) {
            put( entry.getKey(), entry.getValue() );
        }

    }

    @Override
    public void injestMap(Map<String, T> map) throws BucketException, PersistentObjectException {
        for (Map.Entry<String,T> entry : map.entrySet()) {
            put( entry.getKey(), new StoreReference<T>( repository.getStore(), entry.getValue() ) );
        }
    }


}