package uk.ac.standrews.cs.digitising_scotland.util;

/**
 * Created by graham on 02/05/2014.
 */
public interface Condition<T> {
    public boolean test(T t);
}
