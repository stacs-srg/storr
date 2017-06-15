package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by al on 09/06/2017.
 */
public interface IIdtoILXPMap<T extends ILXP> {
    ILXP lookup(Long id) throws IOException, BucketException, RepositoryException;

    void put(Long key, IStoreReference<T> value) throws PersistentObjectException, BucketException;

    void injestRefMap(Map<Long, StoreReference<T>> map) throws PersistentObjectException, BucketException;

    void injestMap(Map<Long, T> map) throws PersistentObjectException, BucketException;
}
