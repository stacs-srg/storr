package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IStore;

import java.nio.file.Path;

/**
 * Created by al on 16/01/15.
 */
public class StoreFactory {

    private static Store store = null;
    private static Path store_path = null;

    /**
     * Get the singleton store.
     * If no store was made, one will be created and returned.
     *
     * @return the global (singleton) store
     * @throws StoreException if something has gone wrong - this is pretty much fatal.
     */
    public static synchronized IStore getStore() throws StoreException {

        if (store == null) {
            store = new Store(store_path);
        }

        return store;
    }

    /**
     * Create a new store - used by tests.
     *
     * @return the newly created (singleton) store
     * @throws StoreException if something has gone wrong - this is pretty much fatal
     */
    static synchronized IStore initialiseNewStore() throws StoreException {

        store = null;
        return getStore();
    }

    /**
     * Set the path of the store. This must be set before requesting a store.
     *
     * @param store_path a path indicating where the store should be created.
     */
    public static void setStorePath(Path store_path) {
        StoreFactory.store_path = store_path;
    }
}
