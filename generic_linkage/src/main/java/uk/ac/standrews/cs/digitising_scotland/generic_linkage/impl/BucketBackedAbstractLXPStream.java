package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketLXP;

/**
 * Created by al on 28/04/2014.
 */
public abstract class BucketBackedAbstractLXPStream {

    protected final IBucketLXP bucket;

    public BucketBackedAbstractLXPStream(final IBucketLXP bucket) {
        this.bucket = bucket;
    }
}
