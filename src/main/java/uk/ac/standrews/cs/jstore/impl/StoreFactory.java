package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.jstore.interfaces.IStore;

import java.nio.file.Path;

/**
 * Created by al on 16/01/15.
 */
public class StoreFactory {

    private static Store store = null;

    /**
     * Get the latest made store.
     * If no store was made, one will be created and returned.
     * @return
     * @throws StoreException
     */
    public static IStore getStore() throws StoreException {

        if (store == null) {
            store = new Store();
            try {
                store.setTransactionManager(new TransactionManager());
            } catch (RepositoryException e) {
                throw new StoreException(e);
            }
        }

        return store;
    }

    /**
     * Create a new store
     * @return
     * @throws StoreException
     */
    public static IStore makeStore() throws StoreException {
        store = null;
        return getStore();
    }

    /**
     * Set the path of the store. This must be set before requesting a store.
     * @param store_path
     */
    public static void setStorePath( Path store_path ) {
        Store.set_store_path( store_path );
    }
}
