package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;

/**
 * Created by al on 28/04/2014.
 */
public abstract class BucketBackedAbstractStream<T extends ILXP> {

    protected final IBucket<T> bucket;

    public BucketBackedAbstractStream(final IBucket<T> bucket) {
        this.bucket = bucket;
    }
}
