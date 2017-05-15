package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;

import java.io.IOException;
import java.util.Map;

/**
 * Created by al on 27/04/2017.
 */
public class PersistentIdtoILXPMap<T extends ILXP> {

    private final PersistentStringtoILXPMap pmap;

    /**
     * Creates a handle on a persistent map, implemented by an IndexedBucket
     * Assumes that persistent map has been created already using a factory - i.e. the directory already exists.
     *
     * @param repository the repository in which the bucket is created.
     * @param map_name   the name of the map/bucket (also used as directory name).
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    PersistentIdtoILXPMap(final IRepository repository, final String map_name, boolean create_bucket) throws RepositoryException {
        pmap = new PersistentStringtoILXPMap( repository, map_name, create_bucket );

    }

    static void createMap(final String name, IRepository repository) throws RepositoryException, IOException {

        PersistentStringtoILXPMap.createMap(name,repository);
    }


    public ILXP lookup(Long id) throws IOException, BucketException, RepositoryException {

        return pmap.lookup( id.toString() );
    }

    public void put(Long key, IStoreReference<T> value) throws PersistentObjectException, BucketException {
        pmap.put( key.toString(), value );
    }

    public void injestRefMapValues(Map<Long, StoreReference<T>> map) {

    }

    public void injestMap(Map<Long, T> map) {

    }


}