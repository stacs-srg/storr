package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

/**
 * Provides the interface to an output stream of labelled cross product records.
 * Created by al on 28/04/2014.
 */
public interface IOutputStream<T extends ILXP> {

    /**
     * Add a record to the stream
     *
     * @param record - the record to be added to a stream
     * @throws BucketException if one is thrown during the underlying bucket operations
     */
    void add(T record) throws BucketException;
}
