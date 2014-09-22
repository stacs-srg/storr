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
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 *
 * Created by fraserdunlop on 06/05/2014 at 11:37.
 */
public class OLRCrossFoldTest {

    /** The properties. */
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /** The vector factory. */
    private VectorFactory vectorFactory;

    /** The training vector list. */
    private ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();

    /** The model. */
    private OLRCrossFold model;

    CodeIndexer index;

    CodeDictionary codeDictionary;

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException 
     */
    @Before
    public void setup() throws IOException, CodeNotValidException {

        if (!new File("target/olrModelPath").delete()) {
            System.err.println("Could not clean up all resources.");
        }

        codeDictionary = new CodeDictionary(new File("target/test-classes/CodeFactoryTestFile.txt"));
        vectorFactory = new VectorFactory();

        populateDictionary();

        properties.setProperty("numCategories", "8");
        properties.setProperty("OLRPoolSize", "3");
        properties.setProperty("OLRFolds", "3");
        properties.setProperty("OLRPoolNumSurvivors", "1");
        properties.setProperty("OLRShuffledReps", "1");
        properties.setProperty("perTermLearning", "false");
        properties.setProperty("olrRegularisation", "false");
        properties.setProperty("numDropped", "1");

        trainingVectorList = generateTrainingVectors();
        Bucket bucket = new Bucket();
        index = new CodeIndexer(bucket);
        model = new OLRCrossFold(trainingVectorList, properties);
        model.train();

    }

    @After
    public void tearDown() {

        if (!new File("target/testOLRCrossfoldWrite.txt").delete()) {
            System.err.println("Could not clean up all resources.");
        }
    }

    /**
     * Generate training vectors.
     *
     * @return the array list
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException 
     */
    private ArrayList<NamedVector> generateTrainingVectors() throws IOException, CodeNotValidException {

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
            String[] splitLine = line.split("\t");
            String descriptionFromFile = splitLine[1].trim();
            vectorFactory.updateDictionary(descriptionFromFile);
        }
    }

    /**
     * Creates the training vector.
     *
     * @param line the line
     * @return the named vector
     * @throws IOException 
     * @throws CodeNotValidException 
     */
    private NamedVector createTrainingVector(final String line) throws IOException, CodeNotValidException {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        CodeDictionary codeDictionary = new CodeDictionary(new File(getClass().getResource("/CodeFactoryOLRTestFile.txt").getFile()));

        int id = index.getID(codeDictionary.getCode(codeFromFile));
        return vectorFactory.createNamedVectorFromString(descriptionFromFile, Integer.toString(id));
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

    //    /**
    //     * Test model.
    //     *
    //     * @throws IOException Signals that an I/O exception has occurred.
    //     */
    //    @Test
    //    public void testModel() throws IOException {
    //
    //        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
    //        String line;
    //        while ((line = br.readLine()) != null) {
    //            testClassifyWithCodeAsDescription(model, line);
    //        }
    //
    //    }

    /**
     * Test write.
     *
     * @throws Exception the exception
     */
    //    @Test
    //    public void testWrite() throws Exception {
    //
    //        model.serializeModel("target/testOLRCrossfoldWrite.txt");
    //        OLRCrossFold olrCrossFold = OLRCrossFold.deSerializeModel("target/testOLRCrossfoldWrite.txt");
    //
    //        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
    //        String line;
    //        while ((line = br.readLine()) != null) {
    //            testClassifyWithCodeAsDescription(olrCrossFold, line);
    //        }
    //    }

    /**
     * Test training de serialized model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test(expected = UnsupportedOperationException.class)
    @Ignore("Needs to be updated to new CodeIndex/DictionaryFormat")
    //FIXME
    public void testTrainingDeSerializedModel() throws IOException {

        model.serializeModel("target/testOLRCrossfoldWrite.txt");
        OLRCrossFold olrCrossFold = OLRCrossFold.deSerializeModel("target/testOLRCrossfoldWrite.txt");
        olrCrossFold.train();

    }

    /**
     * Test classify with code as description.
     *
     * @param model the model
     * @param line the line
     */
    private void testClassifyWithCodeAsDescription(final OLRCrossFold model, final String line) {

        String codeFromFile = getCodeFromLine(line);
        Vector testVector = vectorFactory.createVectorFromString(codeFromFile);
        int classification = getClassification(model, testVector);
        Assert.assertEquals(codeFromFile, index.getCode(classification).getCodeAsString());
    }

    /**
     * Gets the classification.
     *
     * @param model the model
     * @param testVector the test vector
     * @return the classification
     */
    private int getClassification(final OLRCrossFold model, final Vector testVector) {

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

}
