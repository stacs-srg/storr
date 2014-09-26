package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketTypedOLD;

/**
 * Created by al on 28/04/2014.
 */
public abstract class BucketBackedAbstractStreamOLD {

    protected final IBucketTypedOLD bucket;

    public BucketBackedAbstractStreamOLD(final IBucketTypedOLD bucket) {
        this.bucket = bucket;
    }
}
