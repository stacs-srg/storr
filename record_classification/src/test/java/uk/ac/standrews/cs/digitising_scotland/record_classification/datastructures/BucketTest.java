package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.RecordFactory;

/**
 * This Class, BucketTest, tests the construction of {@link Bucket} objects.
 */
public class BucketTest {

    /** The bucket a. */
    private Bucket bucketA;

    /** The list of records. */
    private List<Record> listOfRecords;

    /**
     * Set up. Populates listOfRecords.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);

    }

    /**
     * Test empty constructor.
     */
    @Test
    public void testEmptyConstructor() {

        bucketA = new Bucket();
        Assert.assertEquals(0, bucketA.size());
    }

    /**
     * Tests constructor.
     */
    @Test
    public void testConstructor() {

        bucketA = new Bucket(listOfRecords);

        Assert.assertEquals(listOfRecords.size(), bucketA.size());

        Iterator<Record> originalList = listOfRecords.iterator();
        Iterator<Record> bucketList = bucketA.iterator();

        while (bucketList.hasNext()) {
            Assert.assertEquals(originalList.next(), bucketList.next());
        }

    }

    /**
     * Tests adding single record.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testAddSingleRecord() throws InputFormatException {

        bucketA = new Bucket();
        OriginalData originalData = new OriginalData("description", 1995, 1, "testFileName");
        Record recordToInsert = new Record(originalData);

        Assert.assertEquals(0, bucketA.size());

        bucketA.addRecordToBucket(recordToInsert);

        Assert.assertEquals(1, bucketA.size());

        Assert.assertEquals(recordToInsert, bucketA.iterator().next());

    }

    /**
     * Tests adding a list of records.
     */
    @Test
    public void testAddListOfRecords() {

        bucketA = new Bucket();
        bucketA.addCollectionOfRecords(listOfRecords);

        Assert.assertEquals(listOfRecords.size(), bucketA.size());

        Iterator<Record> originalList = listOfRecords.iterator();
        Iterator<Record> bucketList = bucketA.iterator();

        while (bucketList.hasNext()) {
            Assert.assertEquals(originalList.next(), bucketList.next());
        }

    }

    /**
     * Tests getting a  record from it's UID.
     */
    @Test
    public void testGetRecordFromUID() {

        bucketA = new Bucket(listOfRecords);
        for (Record record : listOfRecords) {
            String uid = record.getUid();
            Assert.assertEquals(record, bucketA.getRecord(uid));
        }

    }

}
