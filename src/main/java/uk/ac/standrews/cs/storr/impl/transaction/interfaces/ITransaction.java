package uk.ac.standrews.cs.storr.impl.transaction.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;

/**
 *
 * Modelled on com.google.appengine.api.datastore API
 *
 * Created by al on 05/01/15.
 */
public interface ITransaction {

    public void commit() throws TransactionFailedException, StoreException;

    public void rollback() throws IllegalStateException;

    public boolean isActive();

    public String getId();

}
