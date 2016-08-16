package uk.ac.standrews.cs.jstore.interfaces;

import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;

/**
 * Provides the interface to an output stream of labelled cross product records.
 * Created by al on 28/04/2014.
 */
public interface IOutputStream<T extends ILXP> {

    /**
     * Add a record to the stream
     *
     * @param record - the record to be added to a stream
     */
    void add(T record) throws BucketException;
}
