package uk.ac.standrews.cs.storr.impl.transaction.exceptions;

/**
 * Created by al on 06/01/15.
 */
public class TransactionFailedException extends Exception {
    public TransactionFailedException(Throwable throwable) {
        super( throwable );
    }

    public TransactionFailedException(String s) {
        super( s );
    }
}
