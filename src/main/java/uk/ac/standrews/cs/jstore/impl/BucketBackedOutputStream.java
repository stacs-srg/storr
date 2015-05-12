package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Created by al on 28/04/2014.
 */
public class BucketBackedOutputStream<T extends ILXP> extends BucketBackedAbstractStream<T> implements IOutputStream<T> {

    public BucketBackedOutputStream(final IBucket<T> bucket) {
        super(bucket);
    }

    @Override
    public void add(final T record) {
        try {
            bucket.makePersistent(record);

        } catch (BucketException e) {
            ErrorHandling.error("Cannot save record with id: " + record.getId() + " to bucket: " + bucket.getName() + "exception: " + e.getMessage());
        }
    }

}
