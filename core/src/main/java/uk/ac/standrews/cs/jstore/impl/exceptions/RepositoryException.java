package uk.ac.standrews.cs.jstore.impl.exceptions;

/**
 * This exception class is used to indicate all errors that occur within repositories.
 * <p/>
 * Created by al on 11/05/2014.
 */
public class RepositoryException extends Exception {

    public RepositoryException(final String message) {
        super(message);
    }

    public RepositoryException(final Throwable thrown) {
        super(thrown);
    }
}
