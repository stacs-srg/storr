package uk.ac.standrews.cs.jstore.impl.exceptions;

/**
 * This exception class is used to indicate all errors that occur within stores.
 * <p/>
 * <p/>
 * Created by al on 11/05/2014.
 */
public class StoreException extends Exception {

    public StoreException(final Throwable cause) {
        super(cause);
    }

    public StoreException(String message) {
        super( message );
    }
}
