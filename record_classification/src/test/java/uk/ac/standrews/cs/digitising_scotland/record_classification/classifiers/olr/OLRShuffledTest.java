package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * The Class OLRShuffledTest.
 */
public class OLRShuffledTest {

    /** The properties. */
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /** The vector factory. */
    private VectorFactory vectorFactory;

    /** The training vector list. */
    private ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();

    /** The model. */
    private OLRShuffled model;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        String codeDictionary = getClass().getResource("/CodeFactoryOLRTestFile.txt").getFile();
        CodeFactory.getInstance().loadDictionary(new File(codeDictionary));
        vectorFactory = new VectorFactory();
        properties.setProperty("numCategories", "5");
        trainingVectorList = generateTrainingVectors();
        model = new OLRShuffled(properties, trainingVectorList);
        model.run();
    }

    @After
    public void tearDown() {

        if (!new File("target/testOLRShuffledWrite.txt").delete()) {
            System.err.println("Could not clean up all resources.");
        }
    }

    /**
     * Test classify.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClassify() throws Exception {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            testClassifyWithCodeAsDescription(model, line);
        }
    }

    /**
     * Test default properties constructor.
     */
    @Test
    public void testDefaultPropertiesConstructor() {

        OLRShuffled olr = new OLRShuffled(trainingVectorList);
        olr.run();
    }

    /**
     * Test write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWrite() throws Exception {

        model.serializeModel("target/testOLRShuffledWrite.txt");
        OLRShuffled olrShuffled = OLRShuffled.deSerializeModel("target/testOLRShuffledWrite.txt");

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            testClassifyWithCodeAsDescription(olrShuffled, line);
        }
    }

    //TODO test throwing this error when trying to classify
    //    @Test(expected=UnsupportedOperationException.class)
    //    public void testWriteAndTrainFail() throws Exception {
    //        model.serializeModel("target/testOLRShuffledWrite.txt");
    //        OLRShuffled olrShuffled = OLRShuffled.deSerializeModel("target/testOLRShuffledWrite.txt");
    //
    //        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
    //        String line;
    //        while ((line = br.readLine()) != null) {
    //            testClassifyWithCodeAsDescription(olrShuffled, line);
    //        }
    //
    //        olrShuffled.
    //    }

    /**
     * Test classify with code as description.
     *
     * @param model the model
     * @param line the line
     */
    private void testClassifyWithCodeAsDescription(final OLRShuffled model, final String line) {

        String codeFromFile = getCodeFromLine(line);
        Vector testVector = vectorFactory.createVectorFromString(codeFromFile);
        int id = getCodeID(codeFromFile);
        int classification = getClassification(model, testVector);
        Assert.assertEquals(id, classification);
    }

    /**
     * Gets the code id.
     *
     * @param codeFromFile the code from file
     * @return the code id
     */
    private int getCodeID(final String codeFromFile) {

        return CodeFactory.getInstance().getCode(codeFromFile).getID();
    }

    /**
     * Gets the classification.
     *
     * @param model the model
     * @param testVector the test vector
     * @return the classification
     */
    private int getClassification(final OLRShuffled model, final Vector testVector) {

        return model.classifyFull(testVector).maxValueIndex();
    }

    /**
     * Gets the code from line.
     *
     * @param line the line
     * @return the code from line
     */
    private String getCodeFromLine(final String line) {

        String[] splitLine = line.split("\t");
        return splitLine[0].trim();
    }

    /**
     * Gets the buffered reader of code dictionary file.
     *
     * @return the buffered reader of code dictionary file
     * @throws FileNotFoundException the file not found exception
     */
    private BufferedReader getBufferedReaderOfCodeDictionaryFile() throws FileNotFoundException {

        File file = new File(getClass().getResource("/CodeFactoryOLRTestFile.txt").getFile());
        return new BufferedReader(new FileReader(file));
    }

    /**
     * Generate training vectors.
     *
     * @return the array list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private ArrayList<NamedVector> generateTrainingVectors() throws IOException {

        populateDictionary();
        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
        while ((line = br.readLine()) != null) {
            trainingVectorList.add(createTrainingVector(line));
        }
        return trainingVectorList;
    }

    /**
     * Populate dictionary.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void populateDictionary() throws IOException {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            vectorFactory.updateDictionary(line.split("\t")[1]);
        }
    }

    /**
     * Creates the training vector.
     *
     * @param line the line
     * @return the named vector
     */
    private NamedVector createTrainingVector(final String line) {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        int id = CodeFactory.getInstance().getCode(codeFromFile).getID();
        return vectorFactory.createNamedVectorFromString(descriptionFromFile, String.valueOf(id));
    }

}
