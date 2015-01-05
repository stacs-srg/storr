package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransactionManager;

import java.util.HashMap;

/**
 * Created by al on 05/01/15.
 */
public class TransactionManager implements ITransactionManager {

    HashMap<String, Transaction> map = new HashMap<>();


    @Override
    public ITransaction beginTransaction() {

        Transaction t = new Transaction();
        map.put(t.getId(), t);
        return t;
    }

    @Override
    public Transaction getTransaction(String id) {
        return map.get(id);
    }
}
