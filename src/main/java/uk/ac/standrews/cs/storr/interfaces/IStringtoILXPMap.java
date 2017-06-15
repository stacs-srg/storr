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
public interface IStringtoILXPMap<T extends ILXP> {
    T lookup(String key) throws IOException, BucketException, RepositoryException;

    void put(String key, IStoreReference<T> value) throws PersistentObjectException, BucketException;

    void injestRefMapValues(Map<Long, StoreReference<T>> map);

    void injestMap(Map<Long, T> map);
}
