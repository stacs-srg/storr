package uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction;

import sun.jvm.hotspot.oops.Mark;

/**
 * Created by al on 05/01/15.
 */
public class Transaction implements EntityTransaction {

    public void begin() {
    }

    public void commit() {
    }

    public boolean getRollbackOnly() {
        return false;
    }

    public boolean isActive() {
    }

    public void rollback() {
    }


    /*
     * Mark the current resource transaction so that the only possible outcome of the transaction is for the transaction to be rolled back.
     */
    public void setRollbackOnly() {
    }
}
