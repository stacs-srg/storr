package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.BucketKind;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IStore;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.util.HashMap;

/**
 * Created by al on 05/01/15.
 */
public class TransactionManager implements ITransactionManager {

    private static final String transaction_repo_name = "Transaction_repository";
    private static final String transaction_bucket_name = "Transactions";

    private IBucket transaction_bucket = null;
    private IRepository transaction_repo = null;

    HashMap<String, Transaction> map = new HashMap<>();


    public TransactionManager() throws RepositoryException {
        get_repo(transaction_repo_name);
        transaction_bucket = get_bucket(transaction_bucket_name);
    }

    @Override
    public ITransaction beginTransaction() {

        Transaction t = new Transaction(this);
        map.put(t.getId(), t);
        return t;
    }

    @Override
    public Transaction getTransaction(String id) {
        return map.get(id);
    }

    @Override
    public void removeTransaction(Transaction t) {
        if (!t.isActive()) {
            map.remove(t.getId());
        }
    }

    @Override
    public IBucket getLog() {
        return transaction_bucket;
    }

    /**
     * ************* Private methods ***************
     */

    private void get_repo(String transaction_repo_name) throws RepositoryException {
        IStore store = Store.getInstance();

        if (store.repoExists(transaction_repo_name)) {
            transaction_repo = store.getRepo(transaction_repo_name);
        } else {
            ErrorHandling.error("Didn't find transaction repository creating new one called: " + transaction_repo_name);
            transaction_repo = store.makeRepository(transaction_repo_name);
        }
    }

    private IBucket get_bucket(String bucket_name) throws RepositoryException {
        if (transaction_repo.bucketExists(bucket_name)) {
            return transaction_repo.getBucket(bucket_name);
        } else {
            ErrorHandling.error("Didn't find transactions bucket, creating a new transactions bucket called: " + bucket_name);
            return transaction_repo.makeBucket(bucket_name, BucketKind.DIRECTORYBACKED);
        }
    }

}
