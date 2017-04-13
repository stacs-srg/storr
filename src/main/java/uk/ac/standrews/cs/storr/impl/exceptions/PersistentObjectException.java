package uk.ac.standrews.cs.storr.impl.exceptions;

/**
 * Created by graham on 13/04/2017.
 */
public class PersistentObjectException extends Exception {
    public PersistentObjectException(Exception e) {
        super(e);
    }

    public PersistentObjectException(String s) {
        super(s);
    }
}
