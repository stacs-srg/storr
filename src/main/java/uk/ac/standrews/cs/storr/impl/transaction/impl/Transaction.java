/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl.transaction.impl;

import uk.ac.standrews.cs.storr.impl.DynamicLXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.storr.interfaces.IBucket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 05/01/15.
 */
public class Transaction implements ITransaction {

    static final String LOG_KEY = "LOG";
    static final String START_RECORD_MARKER = "%/n";
    static final String END_RECORD_MARKER = "/n$";
    static final String UPDATE_RECORD_SEPARATOR = "|";
    static final String LOG_RECORD_SEPARATOR = "/n";
    private static final String START_KEY = "START";
    private final TransactionManager transaction_manager;
    private final IBucket log;
    private final long start_record_id;
    private final String transaction_id;
    private final List<OverwriteRecord> updates = new ArrayList<>();
    private boolean active = true;

    Transaction(TransactionManager transaction_manager) throws TransactionFailedException {

        this.transaction_manager = transaction_manager;
        transaction_id = Long.toString(Thread.currentThread().getId()); // TODO this is good enough for a single machine - need to do more work for multiple node support
        log = transaction_manager.getLog();
        try {
            start_record_id = writeStartRecord();
        } catch (StoreException | BucketException e) {
            throw new TransactionFailedException(e.getMessage());
        }
    }

    @Override
    public synchronized void commit() throws TransactionFailedException {

        if (!active) {
            throw new TransactionFailedException("Transaction not active");
        }
        closeTransaction();

        long commit_record_id = 0;
        try {
            commit_record_id = writeCommitRecord();   // Once this returns the transaction is durable
            deleteStartRecord();
        } catch (StoreException | BucketException e) {
            throw new TransactionFailedException(e);
        }
        swizzle_records();                             // over-writes the records in the store with the shadows.
        removeCommitRecord(commit_record_id);      // cleans up the log
    }

    @Override
    public synchronized void rollback() {
        if (!active) {
            throw new IllegalStateException("Transaction " + getId() + " inactive");
        }

        closeTransaction();
        deleteStartRecord();
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
        sb.append(START_RECORD_MARKER);

        for (OverwriteRecord p : updates) {
            sb.append(p.bucket.getRepository().getName());
            sb.append(UPDATE_RECORD_SEPARATOR);
            sb.append(p.bucket.getName());
            sb.append(UPDATE_RECORD_SEPARATOR);
            sb.append(p.oid);
            sb.append(LOG_RECORD_SEPARATOR);
        }
        sb.append(END_RECORD_MARKER);

        return sb;
    }

    /*
     * Write a start record to the store
     * Only those start records that are left in the log after a JVM crash need tidied up.
     * If there are any of these extant we go into tidy up following a JVM (re)start.
     * This is a trade off - one more write and delete during a transaction versus more length recovery.
     * This could be removed and the correctness of the transaction mechanism would not be compromised.
     */
    private long writeStartRecord() throws StoreException, BucketException {

        return writeXXXRecord(START_KEY, transaction_id);
    }

    private void deleteStartRecord() {
        try {
            log.delete(start_record_id);
        } catch (BucketException e) {
            // nothing can be done here - second level recovery will clean up.
        }
    }

    /*
     * Write a commit record to the store to support durability in the event of machine or JVM crash
     */
    private long writeCommitRecord() throws StoreException, BucketException {

        return writeXXXRecord(LOG_KEY, stringify().toString());
    }

    /*
     * Write some kind of record to the store to support durability in the event of machine or JVM crash
     */
    private long writeXXXRecord(String key, String data) throws StoreException, BucketException {

        DynamicLXP commit_record = new DynamicLXP();

        commit_record.put(key, data);   // TODO Will be fixed when this method is added to LXP.

        log.makePersistent(commit_record);  // Once this returns we can recover the transaction.

        return commit_record.getId();

    }

    private void closeTransaction() {

        active = false;
        transaction_manager.removeTransaction(this);
    }

    /*
     * Remove the commit record from the store
     */
    private void removeCommitRecord(long commit_record_id) {

        try {
            log.delete(commit_record_id);
        } catch (BucketException e) {
            throw new RuntimeException("Cannot delete transaction commit record: " + commit_record_id, e);
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
