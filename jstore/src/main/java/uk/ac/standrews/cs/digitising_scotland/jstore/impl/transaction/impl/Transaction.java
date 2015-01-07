package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 05/01/15.
 */
public class Transaction implements ITransaction {

    private final TransactionManager tm;
    String id;
    boolean active = true;
    List<Pair> buckets = new ArrayList<>();

    public Transaction(TransactionManager tm) {
        this.tm = tm;
        id = Long.toString(Thread.currentThread().getId()); // TODO this is good enough for a single machine - need to do more work for multiple node support
    }

    @Override
    public synchronized void commit() throws TransactionFailedException {
        if (!active) {
            throw new TransactionFailedException();
        }
        close_transaction();

        // TODO if we fail now we need to delete everything from the persistent store.

        swizzle_records();
    }


    @Override
    public synchronized void rollback() {
        if (!active) {
            throw new IllegalStateException("Transaction " + getId() + " inactive");
        }
        close_transaction();
        tidy_up();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getId() {
        return id;
    }

    public synchronized void add(IBucket bucket, long record) {
        if (active) {
            buckets.add(new Pair(bucket, record));
        }
    }

    //**************** private ****************//

    private void close_transaction() {

        active = false;
        tm.removeTransaction(this);
    }

    private void swizzle_records() {
        for (Pair p : buckets) {
            p.bucket.swizzle(p.oid);
        }
    }

    private void tidy_up() {
        for (Pair p : buckets) {
            p.bucket.cleanup(p.oid);    // get rid of shadows from bucket
            buckets.remove(p);
        }
    }

    private class Pair {

        public IBucket bucket;
        public long oid;

        public <T extends ILXP> Pair(IBucket bucket, long oid) {
            this.bucket = bucket;
            this.oid = oid;
        }
    }


}
