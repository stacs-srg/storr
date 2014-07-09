package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.dummies;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;

/**
 * Generates mock buckets for use in testing.
 * Created by fraserdunlop on 19/06/2014 at 10:50.
 */
public class MockBucketFactory {

    public Bucket generateBucketWithDummyRecords(final int numRecords) {

        Bucket bucket = new Bucket();
        for (int i = 0; i < numRecords; i++) {
            bucket.addRecordToBucket(new DummyRecord());
        }
        return bucket;
    }
}
