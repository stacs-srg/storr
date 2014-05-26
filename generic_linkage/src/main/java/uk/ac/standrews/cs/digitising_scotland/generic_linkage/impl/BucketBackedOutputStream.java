package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;

/**
 * Created by al on 28/04/2014.
 */
public class BucketBackedOutputStream extends BuckedBackedAbstractStream implements ILXPOutputStream {

    public BucketBackedOutputStream(IBucket bucket) {
        super(bucket);
    }

    @Override
    public void add(ILXP record) {
        try {
            bucket.save(record);

        } catch (IOException | JSONException e) {
            ErrorHandling.error( "Cannot save record with id: " + record.getId() + " to bucket: "  + bucket.getName() + "exception: " + e.getMessage());
        }
    }
}
