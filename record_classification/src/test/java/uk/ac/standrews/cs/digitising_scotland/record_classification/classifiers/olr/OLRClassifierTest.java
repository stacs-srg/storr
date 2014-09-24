package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * The Class OLRClassifierTest.
 */
//FIXME
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

    private CodeIndexer index;

    private CodeDictionary codeDictionary;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        codeDictionary = new CodeDictionary(new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile()));
        createTrainingBucket();
        index = new CodeIndexer(bucketA);
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

        VectorFactory vectorFactory = new VectorFactory(bucketA, index);
        OLRClassifier olrClassifier1 = new OLRClassifier(vectorFactory);

        olrClassifier1.train(bucketA);
        olrClassifier1.serializeModel("target/olrClassifierWriteTest");
        OLRClassifier olrClassifier2 = new OLRClassifier(new VectorFactory());
        olrClassifier2 = olrClassifier2.deSerializeModel("target/olrClassifierWriteTest");

        for (Record record : bucketA) {
            Code c = olrClassifier2.classify(new TokenSet(record.getDescription())).getLeft();
            Assert.assertTrue("Record does not have code " + c + " in gold standard set. Set contains "
                    + record.getGoldStandardClassificationSet()+ ".",recordHasCodeInGoldStandardSet(record,c));
        }

    }

    /**
     * if the record's gold standard set contains the code then return true else return false.
     */
    private boolean recordHasCodeInGoldStandardSet(Record record, Code c) {
        Set<Classification> gs = record.getGoldStandardClassificationSet();
        Set<Code> gsCodes =  new HashSet<>();
        for(Classification classification : gs){
            gsCodes.add(classification.getCode());
        }
        return gsCodes.contains(c);
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

        VectorFactory vectorFactory = new VectorFactory(bucketA, index);
        OLRClassifier olrClassifier1 = new OLRClassifier(vectorFactory);
        MachineLearningConfiguration.getDefaultProperties().setProperty("reps", "1000");

        olrClassifier1.train(bucketA);

        System.setIn(stdin);
        Assert.assertTrue(new File("target/olrModelPath").exists());

    }

}
