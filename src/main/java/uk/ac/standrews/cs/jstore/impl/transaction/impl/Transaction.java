package uk.ac.standrews.cs.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.jstore.impl.LXP;
import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.jstore.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 05/01/15.
 */
public class Transaction implements ITransaction {

    public static final String LOG_KEY = "LOG";
    public static final String START_KEY = "START";

    public static final String START_RECORD_MARKER = "%/n";
    public static final String END_RECORD_MARKER = "/n$";
    public static final String UPDATE_RECORD_SEPARATOR = "|";
    public static final String LOG_RECORD_SEPARATOR = "/n";

    private final TransactionManager tm;
    private final IBucket log;
    private final long start_record_id;
    String transaction_id;
    boolean active = true;
    List<OverwriteRecord> updates = new ArrayList<>();

    public Transaction(TransactionManager tm) throws TransactionFailedException {
        this.tm = tm;
        transaction_id = Long.toString(Thread.currentThread().getId()); // TODO this is good enough for a single machine - need to do more work for multiple node support
        log = tm.getLog();
        try {
            start_record_id = write_start_record();
        } catch (StoreException | BucketException e) {
            throw new TransactionFailedException( e.getMessage() );
        }
    }

    @Override
    public synchronized void commit() throws TransactionFailedException {
        if (!active) {
            throw new TransactionFailedException( "Transaction not active" );
        }
        close_transaction();

        long commit_record_id = 0;
        try {
            commit_record_id = write_commit_record();   // Once this returns the transaction is durable
            delete_start_record();
        } catch (StoreException | BucketException e) {
            throw new TransactionFailedException(e);
        }
        swizzle_records();                             // over-writes the records in the store with the shadows.
        remove_commit_record( commit_record_id );      // cleans up the log
    }

    @Override
    public synchronized void rollback() {
        if (!active) {
            throw new IllegalStateException("Transaction " + getId() + " inactive");
        }

        close_transaction();
        delete_start_record();
        tidy_up();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getId() {
        return transaction_id;
    }

    public synchronized void add(IBucket bucket, long record) {
        if (active) {
            updates.add(new OverwriteRecord(bucket, record));
        }
    }

    //**************** private ****************//

    /*
     * @return the updates log as a string which can be written to an OID field
     *
     */
    private StringBuffer stringify() {

        StringBuffer sb = new StringBuffer();
        sb.append( START_RECORD_MARKER );

        for (OverwriteRecord p : updates) {
            sb.append( p.bucket.getRepository().getName() );
            sb.append(UPDATE_RECORD_SEPARATOR);
            sb.append( p.bucket.getName() );
            sb.append(UPDATE_RECORD_SEPARATOR);
            sb.append( p.oid );
            sb.append(LOG_RECORD_SEPARATOR);
        }
        sb.append( END_RECORD_MARKER );

        return sb;
    }

    /*
     * Write a start record to the store
     * Only those start records that are left in the log after a JVM crash need tidied up.
     * If there are any of these extant we go into tidy up following a JVM (re)start.
     * This is a trade off - one more write and delete during a transaction versus more length recovery.
     * This could be removed and the correctness of the transaction mechanism would not be compromised.
    */
    private long write_start_record() throws StoreException, BucketException {

        return write_xxx_record(START_KEY,transaction_id );
    }

    private void delete_start_record() {
        try {
            log.delete( start_record_id );
        } catch (BucketException e) {
            // nothing can be done here - second level recovery will clean up.
        }
    }


    /*
     * Write a commit record to the store to support durability in the event of machine or JVM crash
     */
    private long write_commit_record() throws StoreException, BucketException {

        return write_xxx_record(LOG_KEY, stringify().toString());

    }

    /*
     * Write some kind of record to the store to support durability in the event of machine or JVM crash
     */
    private long write_xxx_record( String key, String data ) throws StoreException, BucketException {

        LXP commit_record = new LXP();

        try {
            commit_record.put(key,data);
        } catch (IllegalKeyException e) {
            // This can not happen!
        }

        log.makePersistent(commit_record);  // Once this returns we can recover the transaction.

        return commit_record.getId();

    }

    private void close_transaction() {

        active = false;
        tm.removeTransaction(this);
    }

    /*
     * Remove the commit record from the store
    */
    private void remove_commit_record( long commit_record_id ) {

        try {
            log.delete( commit_record_id );
        } catch (BucketException e) {
            ErrorHandling.error( "Cannot delete transaction commit record: " + commit_record_id);
        }
    }

    private void tidy_up() {
        for (OverwriteRecord p : updates) {
            p.bucket.cleanup(p.oid);            // get rid of shadows from bucket
            updates.remove(p);
        }
    }

    private void swizzle_records() {
        for (OverwriteRecord p : updates) {
            p.bucket.swizzle(p.oid);
        }
    }




}
