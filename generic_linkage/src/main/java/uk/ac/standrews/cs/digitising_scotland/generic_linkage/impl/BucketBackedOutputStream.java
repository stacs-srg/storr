package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;

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
            bucket.put(record);

        } catch (IOException | JSONException e) {
            ErrorHandling.error("Cannot save record with id: " + record.getId() + " to bucket: " + bucket.getName() + "exception: " + e.getMessage());
        }
    }
}
