package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Created by al on 28/04/2014.
 */
public class BucketBackedOutputStream<T> extends BucketBackedAbstractStream implements IOutputStream {

    public BucketBackedOutputStream(final IBucket bucket) {
        super(bucket);
    }

    @Override
    public void add(final ILXP record) {
        try {
            bucket.makePersistent(record);

        } catch (BucketException e) {
            ErrorHandling.error("Cannot save record with id: " + record.getId() + " to bucket: " + bucket.getName() + "exception: " + e.getMessage());
        }
    }
}
