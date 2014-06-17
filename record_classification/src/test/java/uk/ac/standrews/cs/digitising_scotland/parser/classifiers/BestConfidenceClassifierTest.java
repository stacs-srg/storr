package uk.ac.standrews.cs.digitising_scotland.parser.classifiers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.BestConfidenceClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.NaiveBayesClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.preprocessor.DataCleaning;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.Pair;

public class BestConfidenceClassifierTest {

    private Bucket bucketA;
    private Bucket bucketB;
    private List<Record> listOfRecords;
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

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

    }

    /**
     * Tear down.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException {

        File tempFiles = new File("temp/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
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
    private BestConfidenceClassifier train() throws Exception {

        VectorFactory vectorFactory = new VectorFactory(bucketB);
        BestConfidenceClassifier bestConfClassifier = new BestConfidenceClassifier(vectorFactory);
        bestConfClassifier.train(bucketB);
        return bestConfClassifier;
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
        bucketB = helper.giveBucketTestingOccCodes(bucketB);
        addVectorsToBucket(bucketB);
        return bucketB;
    }

    /**
     * Tests the adding of vectors to records in a bucket.
     * @throws Exception if something goes wrong....
     */
    public void addVectorsToBucket(final Bucket bucket) throws Exception {

        DataCleaning.cleanData(bucket);

        System.out.println(bucket.toString());

    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassify() throws Exception {

        BestConfidenceClassifier bestConfClassifier = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        DataCleaning.cleanData(bucketA);

        System.out.println(bucketA.toString());
        bestConfClassifier.classify(bucketA);
        System.out.println(bucketA);
    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassifyTokenSet() throws Exception {

        BestConfidenceClassifier bestConfClassifier = train();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        DataCleaning.cleanData(bucketA);

        System.out.println(bucketA.toString());
        for (Record r : bucketA) {
            TokenSet tokenSet = new TokenSet(r.getOriginalData().getDescription());
            Pair<Code, Double> result = bestConfClassifier.classify(tokenSet);
            r.addCodeTriples(new CodeTriple(result.getLeft(), tokenSet, result.getRight()));
        }
        System.out.println(bucketA);
    }

}
