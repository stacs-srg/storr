package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operations.sharder;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.util.HashMap;
import java.util.Map;

/**
 * Blocker takes a stream and blocks it into buckets based on the assigner
 * Created by al on 29/04/2014.
 */
public abstract class Blocker implements IBlocker {

    private final ILXPInputStream input;
    private final IRepository output_repo;
    private Map<String, IBucket> names = new HashMap<>();

    /**
     * @param input the stream over which to block
     * @param output_repo - the repository into which results are written
     */
    public Blocker(ILXPInputStream input, IRepository output_repo) {
        this.input = input;
        this.output_repo = output_repo;
    }

    public ILXPInputStream getInput() {
            return input;
    }

    /**
     * Apply the method assign to all (non-null) records in the stream
     */
    public void apply() {

       for( ILXP record : input ) {
            if( record != null ) {
                assign(record);
            }

        }
    }

    public void assign(ILXP record)  {
        String[] bucket_names = determineBlockedBucketNamesForRecord(record);
        if( bucket_names != null ) {

            for (String bucket_name : bucket_names) {

                IBucket bucket = names.get(bucket_name);

                if (bucket == null) { // not seen the field before
                    // Need to create a new bucket
                    if (output_repo.bucketExists(bucket_name)) {
                        try {
                            output_repo.getBucket(bucket_name).getOutputStream().add(record);
                        } catch (RepositoryException e) {
                            ErrorHandling.exceptionError(e, "RepositoryException obtaining bucket instance");
                        }
                    } else { // need to create it
                        try {
                            output_repo.makeBucket(bucket_name).getOutputStream().add(record);
                        } catch (RepositoryException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // we have seen this name before and hence have a cached bucket
                    bucket.getOutputStream().add(record);
                }
            }
        }
    }

    public abstract String[] determineBlockedBucketNamesForRecord(ILXP record);


}
