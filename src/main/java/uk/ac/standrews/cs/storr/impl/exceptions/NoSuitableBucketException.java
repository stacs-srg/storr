package uk.ac.standrews.cs.storr.impl.exceptions;

/**
 * This exception class is used to indicate that no bucket can be determined during blocking.
 * Created by al on 9/9/16
 */
public class NoSuitableBucketException extends Exception {

    public NoSuitableBucketException(final String message) {
        super(message);
    }

    public NoSuitableBucketException(final Throwable thrown) {
        super(thrown);
    }
}
