package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import java.util.ArrayList;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * The Class BucketFilter is a utility class that reads a bucket and
 * returns an equivalent bucket that contains only the unique records.
 */
public final class BucketFilter {

    private BucketFilter() {

    }

    /**
     * Returns a bucket containing only the unique records from the original bucket.
     *
     * @param bucket the bucket we want to filter
     * @return the bucket with only the unique records
     */
    public static Bucket uniqueRecordsOnly(final Bucket bucket) {

        List<Record> recordList = new ArrayList<>();

        for (Record record : bucket) {
            if (!recordList.contains(record)) {
                recordList.add(record);
            }

        }

        return new Bucket(recordList);

    }
}
