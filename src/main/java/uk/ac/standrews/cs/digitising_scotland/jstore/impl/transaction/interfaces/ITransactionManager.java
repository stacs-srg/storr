package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl.Transaction;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;

/**
 * Created by al on 05/01/15.
 */
public interface ITransactionManager {

    public ITransaction beginTransaction();

    public Transaction getTransaction(String id);

    public void removeTransaction(Transaction t);

    public IBucket getLog();
}
