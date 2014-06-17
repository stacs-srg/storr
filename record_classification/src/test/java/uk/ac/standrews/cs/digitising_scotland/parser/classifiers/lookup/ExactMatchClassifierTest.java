package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.lookup;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;

/**
 * Test class to test {@link ExactMatchClassifier}.
 * @author jkc25
 *
 */
public class ExactMatchClassifierTest {

    private Bucket trainingBucket;
    private Bucket testingBucket;
    private ExactMatchClassifier exactMatchClassifier;

    // private ClassifierTestingHelper helper = new ClassifierTestingHelper(); //FIXME

    @Before
    public void setUp() throws Exception {

        trainingBucket = getTrainingBucket();
        testingBucket = createTestingBucket();
        train();

    }

    @Test
    public void testTrain() throws Exception {

        train();
        Assert.assertNotNull(exactMatchClassifier.toString());

    }

    @Test
    public void testClassifyRecord() throws Exception {

        ExactMatchClassifier exactMatchClassifer = train();
        for (Record record : testingBucket) {
            Record classifiedRecord = exactMatchClassifer.classify(record);
            Set<CodeTriple> codeTriples = classifiedRecord.getCodeTriples();
            if (!codeTriples.isEmpty()) {
                for (CodeTriple codeTriple : codeTriples) {
                    Assert.assertEquals(codeTriple.getConfidence(), 1.0, 0.01);

                }
            }
        }

    }

    @Test
    public void testClassifyBucket() throws Exception {

        ExactMatchClassifier exactMatchClassifer = train();
        Bucket classifiedBucket = exactMatchClassifer.classify(testingBucket);

        for (Record record : classifiedBucket) {
            Set<CodeTriple> codeTriples = record.getCodeTriples();
            if (!codeTriples.isEmpty()) {
                for (CodeTriple codeTriple : codeTriples) {
                    Assert.assertEquals(codeTriple.getConfidence(), 1.0, 0.01);

                }
            }
        }
    }

    /**
     * @throws Exception
     */
    private ExactMatchClassifier train() throws Exception {

        exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    private Bucket createTestingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipeTesting.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);

        return trainingBucket;
    }

    public Bucket getTrainingBucket() throws Exception {

        Bucket trainingBucket;
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        trainingBucket = new Bucket(listOfRecordsTraining);
        // trainingBucket = helper.giveBucketTestingOccCodes(trainingBucket); FIXME

        return trainingBucket;
    }

    @Test
    public void serializationWriteTest() throws Exception {

        exactMatchClassifier = train();
        exactMatchClassifier.writeModel("target/exactmatchlookuptable");
        Assert.assertTrue(new File("target/exactmatchlookuptable.ser").exists());
    }

    @Test
    public void serializationReadTest() throws Exception {

        exactMatchClassifier = train();
        exactMatchClassifier.writeModel("target/exactmatchlookuptable");
        Assert.assertTrue(new File("target/exactmatchlookuptable.ser").exists());
        ExactMatchClassifier newMatcher = new ExactMatchClassifier();
        newMatcher.readModel("target/exactmatchlookuptable");
        Assert.assertEquals(exactMatchClassifier, newMatcher);
    }

}
