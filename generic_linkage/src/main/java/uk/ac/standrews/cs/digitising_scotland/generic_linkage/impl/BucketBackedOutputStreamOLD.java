package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketTypedOLD;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStreamTypedOLD;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;

/**
 * Created by al on 28/04/2014.
 */
public class BucketBackedOutputStreamOLD<T extends ILXP> extends BucketBackedAbstractStreamOLD implements ILXPOutputStreamTypedOLD<T> {

    public BucketBackedOutputStreamOLD(final IBucketTypedOLD bucket) {
        super(bucket);
    }

    @Override
    public void add(final T record) {
        try {
            bucket.put(record);

        } catch (IOException | JSONException e) {
            ErrorHandling.error("Cannot save record with id: " + record.getId() + " to bucket: " + bucket.getName() + "exception: " + e.getMessage());
        }
    }
}
