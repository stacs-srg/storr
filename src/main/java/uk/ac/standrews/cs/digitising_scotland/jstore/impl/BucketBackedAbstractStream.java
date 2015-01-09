package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;

/**
 * Created by al on 28/04/2014.
 */
public abstract class BucketBackedAbstractStream {

    protected final IBucket bucket;

    public BucketBackedAbstractStream(final IBucket bucket) {
        this.bucket = bucket;
    }
}
