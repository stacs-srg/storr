package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * The Class OLRClassifierTest.
 */
public class OLRClassifierTest {

    /** The bucket a. */
    private Bucket bucketA;

    /** The os. */
    private OutputStream os;

    /** The original out. */
    private PrintStream originalOut;

    /** The helper. */
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        createTrainingBucket();
        divertOutputStream();
    }

    /**
     * Divert output stream.
     */
    private void divertOutputStream() {

        originalOut = System.out;
        os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {

        System.setOut(originalOut);
        FileUtils.deleteQuietly(new File("target/olrClassifierWriteTest"));
        FileUtils.deleteQuietly(new File("target/OLRWriteTest.txt"));
        FileUtils.deleteQuietly(new File("target/olrModelPath"));

    }

    /**
     * Test classify with de serialized model.
     *
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testClassifyWithDeSerializedModel() throws InterruptedException, IOException {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);

        VectorFactory vectorFactory = new VectorFactory(bucketA);
        OLRClassifier olrClassifier1 = new OLRClassifier(vectorFactory);

        olrClassifier1.train(bucketA);
        olrClassifier1.serializeModel("target/olrClassifierWriteTest");
        OLRClassifier olrClassifier2 = new OLRClassifier(new VectorFactory());
        olrClassifier2 = olrClassifier2.deSerializeModel("target/olrClassifierWriteTest");

        for (Record record : bucketA) {
            Assert.assertEquals(olrClassifier1.classify(record), olrClassifier2.classify(record));
        }

    }

    /**
     * Creates the training bucket.
     *
     * @return the bucket
     * @throws Exception the exception
     */
    private Bucket createTrainingBucket() throws Exception {

        properties.setProperty("numCategories", "8");
        properties.setProperty("OLRPoolSize", "3");
        properties.setProperty("OLRFolds", "3");
        properties.setProperty("OLRPoolNumSurvivors", "1");
        properties.setProperty("OLRShuffledReps", "1");
        properties.setProperty("perTermLearning", "false");
        properties.setProperty("olrRegularisation", "false");
        properties.setProperty("numDropped", "1");

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketA = new Bucket(listOfRecordsTraining);
        bucketA = helper.giveBucketTestingOccCodes(bucketA);
        return bucketA;
    }

    /**
     * Test stop listener.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStop() throws Exception {

        String data = "stop\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        VectorFactory vectorFactory = new VectorFactory(bucketA);
        OLRClassifier olrClassifier1 = new OLRClassifier(vectorFactory);
        MachineLearningConfiguration.getDefaultProperties().setProperty("reps", "1000");

        olrClassifier1.train(bucketA);

        System.setIn(stdin);
        Assert.assertTrue(new File("target/olrModelPath").exists());

    }

}
