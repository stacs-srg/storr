package uk.ac.standrews.cs.storr.impl.transaction.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;

/**
 * Modelled on com.google.appengine.api.datastore API
 * <p>
 * Created by al on 05/01/15.
 */
public interface ITransaction {

    void commit() throws TransactionFailedException, StoreException;

    void rollback() throws IllegalStateException;

    boolean isActive();

    String getId();

}
