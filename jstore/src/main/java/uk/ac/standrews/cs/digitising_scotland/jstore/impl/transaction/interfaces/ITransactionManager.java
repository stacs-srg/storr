package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl.Transaction;

/**
 * Created by al on 05/01/15.
 */
public interface ITransactionManager {

    public ITransaction beginTransaction();

    public Transaction getTransaction(String id);


}
