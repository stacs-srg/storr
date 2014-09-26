package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Provides the interface to an output stream of labelled cross product records.
 * Created by al on 28/04/2014.
 */
public interface ILXPOutputStreamTypedNew<T extends ILXP> {

    void add(T record);
}
