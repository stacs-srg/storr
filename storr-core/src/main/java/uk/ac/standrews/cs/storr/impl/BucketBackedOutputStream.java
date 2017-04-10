package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;

/**
 * Created by al on 28/04/2014.
 */
public class BucketBackedOutputStream<T extends ILXP> extends BucketBackedAbstractStream<T> implements IOutputStream<T> {

    public BucketBackedOutputStream(final IBucket<T> bucket) {
        super(bucket);
    }

    @Override
    public void add(final T record) throws BucketException {
        bucket.makePersistent(record);
    }
}
