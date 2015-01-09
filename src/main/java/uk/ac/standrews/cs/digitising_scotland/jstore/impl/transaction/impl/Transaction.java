package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
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

    private static final String LOG = "LOG";

    private final TransactionManager tm;
    private final IBucket log;
    String id;
    boolean active = true;
    List<Pair> updates = new ArrayList<>();

    public Transaction(TransactionManager tm) {
        this.tm = tm;
        id = Long.toString(Thread.currentThread().getId()); // TODO this is good enough for a single machine - need to do more work for multiple node support
        log = tm.getLog();
    }

    @Override
    public synchronized void commit() throws TransactionFailedException {
        if (!active) {
            throw new TransactionFailedException();
        }
        close_transaction();

        // TODO if we fail now we need to delete everything from the persistent store.
        write_commit_record();
        swizzle_records();
        remove_commit_record();
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
            updates.add(new Pair(bucket, record));
        }
    }

    //**************** private ****************//

    private static final String SEPARATOR = "\n";

    /*
     * @return the updates log as a string which can be written to an LXP field
     *
     */
    private StringBuffer stringify() {
        StringBuffer sb = new StringBuffer();
        for (Pair p : updates) {
            sb.append(p.bucket.getName());
            sb.append(SEPARATOR);
            sb.append(p.oid);
        }
    }

    /*
     * Write a commit record to the store to support durability in the event of machine or JVM crash
     */
    private void write_commit_record() {

        LXP commit_record = new LXP();


        try {
            commit_record.put(LOG, stringify().toString());
        } catch (IllegalKeyException e) {
            // This can not happen!
        }

        try {
            log.makePersistent(commit_record); // this is atomic and
        } catch (BucketException e) {
            e.printStackTrace();
        }


    }

    /*
     * Remove the commit record from the store
    */
    private void remove_commit_record() {
    }

    private void close_transaction() {

        active = false;
        tm.removeTransaction(this);
    }

    private void swizzle_records() {
        for (Pair p : updates) {
            p.bucket.swizzle(p.oid);
        }
    }

    private void tidy_up() {
        for (Pair p : updates) {
            p.bucket.cleanup(p.oid);    // get rid of shadows from bucket
            updates.remove(p);
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
