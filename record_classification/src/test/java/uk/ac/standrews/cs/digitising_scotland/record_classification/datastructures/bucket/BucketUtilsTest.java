package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class BucketUtilsTest {

    Bucket bucketA;
    Bucket bucketB;
    Bucket bucketC;
    Bucket emptyBucket;

    @Before
    public void setUp() throws IOException, InputFormatException {

        emptyBucket = new Bucket();
        bucketC = new Bucket();

        File inputFileA = new File(getClass().getResource("/BucketUtilsTestFile1.txt").getFile());
        File inputFileB = new File(getClass().getResource("/BucketUtilsTestFile2.txt").getFile());

        bucketA = new Bucket(RecordFactory.makeUnCodedRecordsFromFile(inputFileA));
        bucketB = new Bucket(RecordFactory.makeUnCodedRecordsFromFile(inputFileB));

        bucketC.addCollectionOfRecords(bucketA);
        bucketC.remove(bucketA.iterator().next());
    }

    @Test
    public void testCompliment() {

        Bucket compliment = BucketUtils.getComplement(bucketA, bucketC);
        int expectedSize = 1;
        Assert.assertEquals(expectedSize, compliment.size());
    }

    @Test
    public void testIntersection() {

        Bucket intersection = BucketUtils.getIntersection(bucketA, bucketC);
        int expectedSize = bucketA.size() - 1;
        Assert.assertEquals(expectedSize, intersection.size());
    }

    @Test
    public void testUnion() {

        Bucket union = BucketUtils.getUnion(bucketA, bucketB);
        int expectedSize = bucketA.size() + bucketB.size();
        Assert.assertEquals(expectedSize, union.size());
    }

}
