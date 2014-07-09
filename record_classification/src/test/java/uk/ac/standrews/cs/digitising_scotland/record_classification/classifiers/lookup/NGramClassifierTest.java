package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor.DataCleaning;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

/**
 * Test class to test {@link NGramClassifier}.
 * @author jkc25
 *
 */
public class NGramClassifierTest {

    private Bucket trainingBucket;
    private Bucket testingBucket;
    private NGramClassifier nGramClassifier;
    private String modelPath = "target/nGramClassifierLookuptable";

    // private ClassifierTestingHelper helper = new ClassifierTestingHelper(); FIXME

    @Before
    public void setUp() throws Exception {

        trainingBucket = getTrainingBucket();
        testingBucket = createTestingBucket();
        train();

    }

    @Test
    public void testTrain() throws Exception {

        train();
        Assert.assertNotNull(nGramClassifier.toString());

    }

    @Test
    public void testClassify() throws Exception {

        DataCleaning.cleanData(testingBucket);
        NGramClassifier nGramClassifer = train();
        for (Record record : testingBucket) {
            Record classifiedRecord = nGramClassifer.classify(record);

            Set<CodeTriple> codeTriples = classifiedRecord.getCodeTriples();
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
    public NGramClassifier train() throws Exception {

        nGramClassifier = new NGramClassifier();
        nGramClassifier.train(trainingBucket);
        return nGramClassifier;
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
        //  trainingBucket = helper.giveBucketTestingOccCodes(trainingBucket); //FIXME

        return trainingBucket;
    }

    @Test
    public void serializationWriteTest() throws Exception {

        writeTable();
        Assert.assertTrue(new File(modelPath + ".ser").exists());
        Assert.assertTrue(new File(modelPath + ".ser").delete());

    }

    private void writeTable() throws Exception {

        nGramClassifier = train();
        nGramClassifier.writeModel(modelPath);
    }

    @Test
    public void serializationReadTest() throws Exception {

        writeTable();

        Assert.assertTrue(new File(modelPath + ".ser").exists());
        NGramClassifier newMatcher = new NGramClassifier();
        newMatcher.readModel(modelPath);

        Assert.assertEquals(nGramClassifier, newMatcher);
        Assert.assertTrue(new File(modelPath + ".ser").delete());
    }
}
