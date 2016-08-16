package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IOutputStream;

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
