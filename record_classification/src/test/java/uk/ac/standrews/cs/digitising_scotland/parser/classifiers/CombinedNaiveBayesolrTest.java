package uk.ac.standrews.cs.digitising_scotland.parser.classifiers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.preprocessor.DataCleaning;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Class tests the interaction between the Naive Bayes and the olr Clasifiers when run together in a pipeline.
 * @author jkc25
 *
 */
public class CombinedNaiveBayesolrTest {

    private Bucket bucketA;
    private Bucket bucketB;
    private List<Record> listOfRecords;
    private NaiveBayesClassifier nbc;
    private OLRClassifier olr;
    private ClassificationPipeline pipeline;
    private Properties properties;
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    private static boolean doneOnce = false;

    /**
     * Setup, run before every test.
     * Creates new classifier objects, properties files etc. Also makes sure the directory is clean before testing.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        init();
        File tempFiles = new File("temp/");
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
        }
        MachineLearningConfiguration.getDefaultProperties().setProperty("numFeatures", "6");
        MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", "6");

    }

    /**
     * Initialise classifiers, pipeline and properties file.
     */
    public void init() throws Exception {

        createTrainingBucket();
        VectorFactory vectorFactory = new VectorFactory(bucketB);
        nbc = new NaiveBayesClassifier(vectorFactory);
        olr = new OLRClassifier(vectorFactory);
        pipeline = new ClassificationPipeline();
        properties = MachineLearningConfiguration.getDefaultProperties();
    }

    /**
     * Tear down, cleans up temp files.
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
     * Tests training a Naive Bayes and an olr Classifier.
     * Asserts the the models have been created.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTrain() throws Exception {

        trainNB();
        trainOLR();

        checkFileExsits(properties.getProperty("naiveBayesModelPath"));
        checkFileExsits(properties.getProperty("olrModelPath"));

    }

    /**
     * Simple file exists assertion.
     *
     * @param fileName
     */
    private void checkFileExsits(final String fileName) {

        File fileToCheck = new File(fileName);
        Assert.assertTrue(fileToCheck.exists());
    }

    /**
     * Trains a Naive Bates classifier.
     *
     * @throws Exception the exception
     */
    public void trainNB() throws Exception {

        nbc.train(bucketB);
    }

    /**
     * Trains an olr classifier.
     *
     * @throws Exception the exception
     */
    public void trainOLR() throws Exception {

        olr.train(bucketB);
    }

    private Bucket createTrainingBucket() throws Exception {

        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketB = new Bucket(listOfRecordsTraining);
        bucketB = helper.giveBucketTestingOccCodes(bucketB);
        return bucketB;
    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassify() throws Exception {

        trainNB();
        trainOLR();
        bucketA = new Bucket();

        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        listOfRecords = RecordFactory.makeUnCodedRecordsFromFile(inputFile);
        bucketA.addCollectionOfRecords(listOfRecords);

        DataCleaning.cleanData(bucketA);

        System.out.println(bucketA.toString());

        pipeline.addTrainedClassifier(nbc);
        pipeline.addTrainedClassifier(olr);

        for (Record record : bucketA) {
            pipeline.classifyRecord(record);
        }

        System.out.println(bucketA);
    }
}
