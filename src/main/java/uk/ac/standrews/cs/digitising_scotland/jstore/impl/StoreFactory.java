package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IStore;

/**
 * Created by al on 16/01/15.
 */
public class StoreFactory {

    private static Store store = null;

    public static IStore getStore() throws StoreException {

        if( store == null ) {
            store = new Store();
            try {
                store.setTransactionManager( new TransactionManager() );
            } catch (RepositoryException e) {
                throw new StoreException( e );
            }
            return store;
        } else {
            return store;
        }
    }

    public static IStore makeStore() throws StoreException {
        store = null;
        return getStore();
    }

    public static void setStorePath( String store_path ) {
        Store.set_store_path( store_path );

    }
}
