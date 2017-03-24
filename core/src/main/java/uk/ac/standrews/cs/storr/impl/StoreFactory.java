package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IStore;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by al on 16/01/15.
 */
public class StoreFactory {

    private static Store store = null;

    /**
     * Get the latest made store.
     * If no store was made, one will be created and returned.
     * @return the global (singleton) store
     * @throws StoreException if something has gone wrong - this is pretty much fatal.
     */
    public static IStore getStore() throws StoreException {

        if (store == null) {
            store = new Store();
            try {
                store.setTransactionManager( new TransactionManager() );
                TypeFactory.makeTypeFactory();
            } catch ( RepositoryException e) {
                throw new StoreException(e);
            }
        }

        return store;
    }

    /**
     * Create a new store
     * @return the newly created (singleton) store
     * @throws StoreException if something has gone wrong - this is pretty much fatal
     */
    public static IStore makeStore() throws StoreException {
        store = null;
        return getStore();
    }

    /**
     * Set the path of the store. This must be set before requesting a store.
     * @param store_path a path indicating where the store should be created.
     */
    public static void setStorePath( Path store_path ) {
        Store.set_store_path( store_path );
    }
}
