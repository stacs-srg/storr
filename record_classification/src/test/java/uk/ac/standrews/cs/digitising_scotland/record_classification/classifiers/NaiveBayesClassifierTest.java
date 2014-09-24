package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;

/**
 * The Class NaiveBayesClassifierTest.
 */
@Ignore("Needs to be updated to new CodeIndex/DictionaryFormat")
//FIXME
public class NaiveBayesClassifierTest {

    /** The bucket a. */
    private Bucket bucketA;

    /** The bucket b. */
    private Bucket bucketB;

    /** The list of records. */
    private List<Record> listOfRecords;

    private CodeIndexer indexer;

    // FIXME  private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    /**
     * Setup. Run before each test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        bucketB = createTrainingBucket();
        File tempFiles = new File("temp/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
        }
        indexer = new CodeIndexer(bucketB);

    }

    /**
     * Tear down.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException {

        File tempFiles = new File("temp/");
        File labelIndex = new File("labelindex.csv");
        File naiveBayesModelPath = new File("naiveBayesModelPath/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
            FileUtils.deleteDirectory(naiveBayesModelPath);
            FileUtils.deleteQuietly(labelIndex);

        }
    }

    /**
     * Test train.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTrain() throws Exception {

        train();
    }

    /**
     * Trains and returns a {@link NaiveBayesClassifier}.
     *
     * @return the naive bayes classifier
     * @throws Exception the exception
     */
    private NaiveBayesClassifier train() throws Exception {

        VectorFactory vectorFactory = new VectorFactory(bucketB, indexer);
        NaiveBayesClassifier nbc = new NaiveBayesClassifier();
        nbc.train(bucketB);
        return nbc;
    }

    /**
     * Creates a training bucket.
     *
     * @return the training bucket
     * @throws Exception the exception
     */
    private Bucket createTrainingBucket() throws Exception {

        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketB = new Bucket(listOfRecordsTraining);
        //FIXME   bucketB = helper.giveBucketTestingOccCodes(bucketB);
        return bucketB;
    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassify() throws Exception {

        NaiveBayesClassifier nbc = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        //        LevenShteinCleaner.cleanData(bucketA);

    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassifyTokenSet() throws Exception {

        NaiveBayesClassifier nbc = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        for (Record r : bucketA) {
            TokenSet tokenSet = new TokenSet(r.getOriginalData().getDescription());
            Pair<Code, Double> result = nbc.classify(new TokenSet(r.getOriginalData().getDescription()));
        }

    }
}
