package uk.ac.standrews.cs.storr.impl.transaction.interfaces;

import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.storr.impl.transaction.impl.Transaction;
import uk.ac.standrews.cs.storr.interfaces.IBucket;

/**
 * Created by al on 05/01/15.
 */
public interface ITransactionManager {

    public ITransaction beginTransaction() throws TransactionFailedException;

    public Transaction getTransaction(String id);

    public void removeTransaction(Transaction t);

    public IBucket getLog();
}
