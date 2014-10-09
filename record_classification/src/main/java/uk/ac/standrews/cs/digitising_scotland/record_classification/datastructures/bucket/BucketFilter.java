package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * The Class BucketFilter is a utility class that reads a bucket and
 * returns an equivalent bucket with records removed according to the filer applied.
 */
public final class BucketFilter {

    private BucketFilter() {

    }

    /**
     * Returns a bucket containing only the unique records from the original bucket.
     *
     * @param bucket the bucket we want to filter
     * @return Bucket a new Bucket with only the unique records
     */
    public static Bucket uniqueRecordsOnly(final Bucket bucket) {

        Map<List<String>, Record> map = new HashMap<>();

        for (Record record : bucket) {
            if (!map.containsKey(record.getDescription())) {
                map.put(record.getDescription(), record);
            }
        }

        List<Record> recordList = new ArrayList<>();

        for (Record record : map.values()) {
            recordList.add(record);
        }

        return new Bucket(recordList);

    }
}
