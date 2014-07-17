package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.dummies;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;

/**
 * Testing mock bucket factory.
 * Created by fraserdunlop on 19/06/2014 at 10:54.
 */
public class MockBucketFactoryTest {

    private MockBucketFactory mockBucketFactory;

    @Before
    public void setup() {

        mockBucketFactory = new MockBucketFactory();
    }

    @Test
    public void dummyBucketHasCorrectSizeTest() {

        Bucket dummyBucket = mockBucketFactory.generateBucketWithDummyRecords(10);
        assertEquals(10, dummyBucket.size());
    }
}
