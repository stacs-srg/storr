package uk.ac.standrews.cs.storr.impl.exceptions;

/**
 * This exception class is used to indicate all errors that occur within Buckets.
 * Created by al on 11/05/2014.
 */
public class BucketException extends Exception {

    public BucketException(final String message) {
        super(message);
    }

    public BucketException(final Throwable thrown) {
        super(thrown);
    }
}
