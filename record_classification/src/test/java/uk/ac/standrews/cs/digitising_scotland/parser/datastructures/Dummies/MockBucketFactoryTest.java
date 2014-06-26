package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Dummies;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import static org.junit.Assert.*;

/**
 * Testing mock bucket factory.
 * Created by fraserdunlop on 19/06/2014 at 10:54.
 */
public class MockBucketFactoryTest {

    private MockBucketFactory mockBucketFactory;

    @Before
    public void setup(){
        mockBucketFactory = new MockBucketFactory();
    }

    @Test
    public void dummyBucket_HasCorrectSizeTest(){
        Bucket dummyBucket = mockBucketFactory.generateBucketWithDummyRecords(10);
        assertEquals(10, dummyBucket.size());
    }
}
