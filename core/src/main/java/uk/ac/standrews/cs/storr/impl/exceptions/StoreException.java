package uk.ac.standrews.cs.storr.impl.exceptions;

/**
 * This exception class is used to indicate all errors that occur within stores.
 * Created by al on 11/05/2014.
 */
public class StoreException extends RuntimeException {

    public StoreException(final Throwable cause) {
        super(cause);
    }

    public StoreException(String message) {
        super( message );
    }
}
