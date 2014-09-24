package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;

/**
 * Test class to test {@link LookupTableClassifier}.
 * @author jkc25
 * FIXME
 */
public class LookupTableClassifierTest {

    /** The training bucket. */
    private Bucket trainingBucket;

    /** The testing bucket. */
    private Bucket testingBucket;

    /** The lookup classifier. */
    private LookupTableClassifier lookupClassifier;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        trainingBucket = getTrainingBucket();
        testingBucket = createTestingBucket();
        train();

    }

    /**
     * Test train.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTrain() throws Exception {

        train();
        Assert.assertNotNull(lookupClassifier.toString());

    }

    /**
     * Trains a lookup classifier with the training bucket.
     *
     * @return the lookup table classifier
     * @throws Exception the exception
     */
    private LookupTableClassifier train() throws Exception {

        lookupClassifier = new LookupTableClassifier();
        lookupClassifier.train(trainingBucket);
        return lookupClassifier;
    }

    /**
     * Creates the testing bucket.
     *
     * @return the bucket
     * @throws Exception the exception
     */
    private Bucket createTestingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipeTesting.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);
        addVectorsToBucket(trainingBucket);

        return trainingBucket;
    }

    /**
     * Gets the training bucket.
     *
     * @return the training bucket
     * @throws Exception the exception
     */
    public Bucket getTrainingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);
        addVectorsToBucket(trainingBucket);

        return trainingBucket;
    }

    /**
     * Tests the adding of vectors to records in a bucket.
     *
     * @param bucket the bucket to add vectors to
     * @throws Exception if something goes wrong....
     */
    public void addVectorsToBucket(final Bucket bucket) throws Exception {

        for (Record record : bucket) {
            record.getDescription();
        }
    }

}
