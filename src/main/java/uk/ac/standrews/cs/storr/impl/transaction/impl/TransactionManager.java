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

import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by al on 05/01/15.
 */
public class TransactionManager implements ITransactionManager {

    private static final String TRANSACTION_REPOSITORY_NAME = "Transaction_repository";
    private static final String TRANSACTION_BUCKET_NAME = "Transactions";

    private IBucket transaction_bucket = null;
    private IRepository transaction_repo = null;
    private IStore store;

    private HashMap<String, Transaction> map = new HashMap<>();

    public TransactionManager(IStore store) throws RepositoryException {

        this.store = store;
        setupRepository(TRANSACTION_REPOSITORY_NAME);
        transaction_bucket = getBucket(TRANSACTION_BUCKET_NAME);
        cleanupAfterRestart();
    }

    private static String replaceLast(String string, String toReplace, String replacement) {

        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    @Override
    public ITransaction beginTransaction() throws TransactionFailedException {

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

    private void cleanupAfterRestart() {

        if (cleanUpNeeded()) {

            System.out.println("Running recovery");

            // go through each of the records in the log
            // These are either committed or not.
            // Committed records have well formed OID records written in the log, incomplete ones will have start records

            // for each committed transaction we need to:
            //  1. swizzle_records();                       // over-writes the records in the store with the shadows.
            //  2. remove_commit_record(commit_record_id);  // cleans up the log
            //  as defined in commit.

            // for incomplete:
            // A. delete the shadow records
            // B. remove_commit_record(commit_record_id) (incomplete records only)

            Iterator<LXP> transaction_records;

            try {
                transaction_records = transaction_bucket.getInputStream().iterator();

                while (transaction_records.hasNext()) {
                    LXP transaction_record = transaction_records.next(); // this might be a commit record or a start record
                    long record_id = transaction_record.getId();

                    try {
                        if (transaction_record.getMetaData().containsLabel(Transaction.LOG_KEY)) {

                            recoverCommitRecord(record_id, transaction_record);

                        }
                    } catch (TypeMismatchFoundException | KeyNotFoundException | RepositoryException | StoreException e) {
                        // We cannot get the repo or the store - we need to 1. delete the write records
                        ErrorHandling.exceptionError(e, "Cannot recover update record information during failure recovery");
                    }
                    // delete the record
                    try {
                        transaction_bucket.delete(record_id);          // 2. & B. remove_commit_record(commit_record_id)
                    } catch (BucketException e) {
                        // Not much to do here.
                    }
                }
            } catch (BucketException e) {
                ErrorHandling.error("Cannot get records to clean up transaction in failure recovery");
            }

            // If we get to here all we can do is delete the shadow records as in A. above.
            // we don't know where they are, and they may or may not exist.
            // need to search all.

            deleteAllShadows();
        }
    }

    private void recoverCommitRecord(long commit_record_id, LXP transaction_record) throws KeyNotFoundException, TypeMismatchFoundException, StoreException, RepositoryException {

        String updateString = (String) transaction_record.get(Transaction.LOG_KEY);

        // we need to ensure that this is well formed

        if (!updateString.startsWith(Transaction.START_RECORD_MARKER) && updateString.endsWith(Transaction.END_RECORD_MARKER)) {
            // We do not have a good transaction record.
            // abort and go to clean up.
            try {
                transaction_bucket.delete(commit_record_id);          // B. remove_commit_record(commit_record_id)
                // this potentially leaves shadow copies behind
            } catch (BucketException e) {
                ErrorHandling.error("Cannot delete commit_record with transaction id: " + commit_record_id);
                return;
            }
        }

        updateString = updateString.replaceFirst(Transaction.START_RECORD_MARKER, ""); // Good record - remove head marker.
        updateString = replaceLast(updateString, Transaction.END_RECORD_MARKER, ""); // remove the end marker

        String[] updates = updateString.split(Transaction.LOG_RECORD_SEPARATOR); // a list of update records in string form - each is a triple as described 2 lines down.

        for (String update_pair : updates) {
            String[] parts = update_pair.split(Transaction.UPDATE_RECORD_SEPARATOR); // a repo name, a bucket name and an oid

            IRepository repo = store.getRepository(parts[0]);
            IBucket bucket = repo.getBucket(parts[1]);

            Long updated_record_oid = Long.getLong(parts[2]);

            bucket.swizzle(updated_record_oid);             //  1. swizzle_records();   // over-writes the records in the store with the shadows.
        }
    }

    /**
     * @return true if we need to store recovery
     */
    private boolean cleanUpNeeded() {

        try {
            Iterator iter = transaction_bucket.getInputStream().iterator();
            // return true if the transaction bucket contains start records or commit records.
            return iter.hasNext();

        } catch (BucketException e) {
            return true; // safe
        }
    }

    private void deleteAllShadows() {

        try {
            Iterator<IRepository> repo_iterator = store.getIterator();

            while (repo_iterator.hasNext()) {
                IRepository repo = repo_iterator.next();
                Iterator<String> bucket_names = repo.getBucketNameIterator();
                while (bucket_names.hasNext()) {
                    String bucket_name = bucket_names.next();
                    try {
                        IBucket bucket = repo.getBucket(bucket_name);
                        bucket.tidyUpTransactionData();
                    } catch (RepositoryException e) {
                        ErrorHandling.error("Cannot get bucket during recovery: " + bucket_name);
                    }
                }
            }
        } catch (StoreException e) {
            ErrorHandling.error("Cannot get store during recovery");
        }
    }

    private void setupRepository(String transaction_repo_name) throws RepositoryException {

        if (store.repositoryExists(transaction_repo_name)) {
            transaction_repo = store.getRepository(transaction_repo_name);
        } else {
            transaction_repo = store.makeRepository(transaction_repo_name);
        }
    }

    private IBucket getBucket(String bucket_name) throws RepositoryException {

        if (transaction_repo.bucketExists(bucket_name)) {
            return transaction_repo.getBucket(bucket_name);
        } else {
            return transaction_repo.makeBucket(bucket_name, BucketKind.DIRECTORYBACKED);
        }
    }
}
