package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;

public class OLRClassifierTest {

    private Bucket bucketA;
    private OutputStream os;
    private PrintStream originalOut;
    private ClassifierTestingHelper helper = new ClassifierTestingHelper();

    @Before
    public void setup() throws Exception {

        createTrainingBucket();
        divertOutputStream();
    }

    private void divertOutputStream() {

        originalOut = System.out;
        os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
    }

    @After
    public void tearDown() {

        System.setOut(originalOut);
    }

    @Test
    public void testClassifyWithEmptyModel() throws IOException {

        OLRClassifier olrClassifier = new OLRClassifier("machineLearning.properties", new VectorFactory(bucketA));
        olrClassifier.classify(bucketA);
        Assert.assertEquals("Model has not been trained.", os.toString().trim());
    }

    @Test
    public void testClassifyWithDeSerializedModel() throws InterruptedException, IOException {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);

        VectorFactory vectorFactory = new VectorFactory(bucketA);
        OLRClassifier olrClassifier1 = new OLRClassifier(vectorFactory);

        olrClassifier1.train(bucketA);
        olrClassifier1.serializeModel("target/olrClassifierWriteTest");

        OLRClassifier olrClassifier2 = OLRClassifier.deSerializeModel("target/olrClassifierWriteTest");

        for (Record record : bucketA) {
            Assert.assertEquals(olrClassifier1.classify(record), olrClassifier2.classify(record));
        }

    }

    private Bucket createTrainingBucket() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
        File inputFileTraining = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());
        List<Record> listOfRecordsTraining = RecordFactory.makeUnCodedRecordsFromFile(inputFileTraining);
        bucketA = new Bucket(listOfRecordsTraining);
        bucketA = helper.giveBucketTestingOccCodes(bucketA);
        return bucketA;
    }

}
