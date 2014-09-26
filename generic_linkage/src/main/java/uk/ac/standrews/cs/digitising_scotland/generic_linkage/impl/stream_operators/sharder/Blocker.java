package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.util.HashMap;
import java.util.Map;

/**
 * Blocker takes a stream and blocks it into buckets based on the assigner
 * Created by al on 29/04/2014.
 */
public abstract class Blocker<T extends ILXP> implements IBlocker<T> {

    private final ILXPInputStreamTypedOld<T> input;
    private final IRepository output_repo;
    private final Map<String, IBucketTypedOLD> names = new HashMap<>();
    private ILXPFactory<T> factory;

    /**
     * @param input       the stream over which to block
     * @param output_repo - the repository into which results are written
     */
    public Blocker(final ILXPInputStreamTypedOld<T> input, final IRepository output_repo, ILXPFactory<T> factory ) {

        this.input = input;
        this.output_repo = output_repo;
        this.factory = factory;
    }

    public ILXPInputStreamTypedOld getInput() {
        return input;
    }

    /**
     * Apply the method assign to all (non-null) records in the stream
     */
    @Override
    public void apply() {

        for (T record : input) {
            if (record != null) {
                assign(record);
            }
        }
    }

    @Override
    public void assign(final T record) {

        for (String bucket_name : determineBlockedBucketNamesForRecord(record)) {

            IBucketTypedOLD bucket = names.get(bucket_name);

            if (bucket == null) { // not seen the field before
                // Need to create a new bucket
                if (output_repo.bucketExists(bucket_name)) {
                    try {
                        output_repo.getBucket(bucket_name, factory).getOutputStream().add(record);
                    } catch (RepositoryException e) {
                        ErrorHandling.exceptionError(e, "RepositoryException obtaining bucket instance");
                    }
                } else { // need to create it
                    try {
                        output_repo.makeBucket(bucket_name,factory).getOutputStream().add(record);
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                    }
                }
            } else { // we have seen this name before and hence have a cached bucket
                bucket.getOutputStream().add(record);
            }
        }
    }

    public abstract String[] determineBlockedBucketNamesForRecord(ILXP record);
}
