package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static uk.ac.standrews.cs.storr.impl.BucketKind.INDEXED;

/**
 * Created by al on 27/04/2017.
 */
public class PersistentStringtoILXPMap<T extends ILXP> {

    private final DirectoryBackedIndexedBucket<Tuple<T>> bucket; // bucket used to store the map.
    private final IRepository repository;     // the repository in which the bucket is stored
    private final IStore store;               // the store

    /**
     * Creates a handle on a persistent map, implemented by an IndexedBucket
     * Assumes that persistent map has been created already using a factory - i.e. the directory already exists.
     *
     * @param repository the repository in which the bucket is created.
     * @param map_name   the name of the map/bucket (also used as directory name).
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    PersistentStringtoILXPMap(final IRepository repository, final String map_name, boolean create_bucket) throws RepositoryException {
        this.repository = repository;
        this.store = repository.getStore();
        bucket = new DirectoryBackedIndexedBucket(repository, map_name, create_bucket);

    }

    static void createMap(final String name, IRepository repository) throws RepositoryException, IOException {

        DirectoryBackedIndexedBucket.createBucket(name, repository, INDEXED);
        IIndexedBucket bucket = new DirectoryBackedIndexedBucket(repository, name, false);
        bucket.addIndex(Tuple.KEY);
    }


    public ILXP lookup(String key) throws IOException, BucketException, RepositoryException {
        IInputStream<Tuple<T>> values = bucket.getIndex(Tuple.KEY).records(key);
        ArrayList<Tuple<T>> list = new ArrayList();
        for( Tuple<T> value : values ) {
           list.add( value );
        }

        if( list.size() < 0 ) {
            return null;
        } else if( list.size() > 1 ) {
            throw new IOException( "Multiple values found for key: " + key );
        } else {
            Tuple<T> value = list.get(0);
            IStoreReference ref = value.getValue();
            return ref.getReferend();
        }
    }

    public void put(String key, IStoreReference<T> value) throws PersistentObjectException, BucketException {
        bucket.makePersistent( new Tuple<T>( key, value ) );
    }

    public void injestRefMapValues(Map<Long, StoreReference<T>> map) {

    }

    public void injestMap(Map<Long, T> map) {

    }


}