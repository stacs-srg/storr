package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * The Class OLRClassifierTest.
 */
//FIXME
public class OLRClassifierTest {

    /** The os. */
    private OutputStream os;

    /** The original out. */
    private PrintStream originalOut;

    /** The helper. */
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    // private CodeIndexer index;

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

    @Test
    public void trainExistingModelWithNewDataTest() throws Exception {

        Bucket bucketA = createTrainingBucket();
        OLRClassifier olrClassifier1 = new OLRClassifier();
        olrClassifier1.train(bucketA);
        olrClassifier1.serializeModel("target/olrClassifierWriteTest2");
        OLRClassifier olrClassifier2 = new OLRClassifier();
        olrClassifier2 = olrClassifier2.deSerializeModel("target/olrClassifierWriteTest2");
        Iterable<Record> records = createNewRecords();
        bucketA.addCollectionOfRecords(records);
        olrClassifier2.train(bucketA);

    }

    private Iterable<Record> createNewRecords() throws InputFormatException, CodeNotValidException {

        String[] description = {"Occupation 1", "Another Job", "Foo", "Bar", "FooManChu"};
        String[] tokenSetArry = {"The Red", "Cat Or", "Should it be fox", "jumped over something", "that", "should maybe be a ", "big cat or something", "idk", "jamie", "fraser"};

        List<Record> records = new ArrayList<Record>();

        for (int i = 0; i < 100; i++) {
            ArrayList<String> d = new ArrayList<>();
            d.add(description[i % 5]);
            final OriginalData originalData = new OriginalData(d, 2010 + i, 1, "fileName.txt");
            Set<Classification> goldStandardClassification = new HashSet<>();
            Code code = getRandomCode();
            goldStandardClassification.add(new Classification(code, new TokenSet(tokenSetArry[i % 10]), 1.0));
            originalData.setGoldStandardClassification(goldStandardClassification);
            Record r = new Record(i, originalData);
            records.add(r);
        }

        return records;
    }

    private Code getRandomCode() throws CodeNotValidException {

        double rnd = Math.random();
        if (rnd < 0.33) {
            return codeDictionary.getCode("95240");
        }
        else if (rnd > 0.3 && rnd < 0.66) { return codeDictionary.getCode("9500"); }
        return codeDictionary.getCode("952");
    }

    /**
     * Test classify with de serialized model.
     * @throws Exception 
     */
    @Test
    public void testClassifyWithDeSerializedModel() throws Exception {

        Bucket bucketA = createTrainingBucket();

        OLRClassifier olrClassifier1 = new OLRClassifier();

        olrClassifier1.train(bucketA);
        olrClassifier1.serializeModel("target/olrClassifierWriteTest");
        OLRClassifier olrClassifier2 = new OLRClassifier();
        olrClassifier2 = olrClassifier2.deSerializeModel("target/olrClassifierWriteTest");

        for (Record record : bucketA) {
            for (String s : record.getDescription()) {
                Pair<Code, Double> c1 = olrClassifier1.classify(new TokenSet(s));
                Pair<Code, Double> c2 = olrClassifier2.classify(new TokenSet(s));
                Assert.assertEquals(c1, c2);

            }
        }

    }

    /**
     * if the record's gold standard set contains the code then return true else return false.
     */
    private boolean recordHasCodeInGoldStandardSet(Record record, Code c) {

        Set<Classification> gs = record.getGoldStandardClassificationSet();
        Set<Code> gsCodes = new HashSet<>();
        for (Classification classification : gs) {
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
        Bucket bucketA = new Bucket(listOfRecordsTraining);
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

        Bucket bucketA = createTrainingBucket();

        String data = "stop\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        OLRClassifier olrClassifier1 = new OLRClassifier();
        MachineLearningConfiguration.getDefaultProperties().setProperty("reps", "1000");

        olrClassifier1.train(bucketA);

        System.setIn(stdin);
        Assert.assertTrue(new File("target/olrModelPath").exists());

    }

}
