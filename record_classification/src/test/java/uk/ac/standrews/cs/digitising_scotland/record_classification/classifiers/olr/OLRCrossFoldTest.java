package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.mahout.math.NamedVector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    private CodeIndexer index;

    private CodeDictionary codeDictionary;

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
        index = new CodeIndexer(codeDictionary);
        vectorFactory = new VectorFactory();

        populateDictionary();

        setProperties();

        trainingVectorList = generateTrainingVectors();

        model = new OLRCrossFold(trainingVectorList, properties);
        model.train();

    }

    private void setProperties() {

        properties.setProperty("numCategories", "8");
        properties.setProperty("OLRPoolSize", "3");
        properties.setProperty("OLRFolds", "3");
        properties.setProperty("OLRPoolNumSurvivors", "1");
        properties.setProperty("OLRShuffledReps", "1");
        properties.setProperty("perTermLearning", "false");
        properties.setProperty("olrRegularisation", "false");
        properties.setProperty("numDropped", "1");
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

    /**
     * Test training de serialized model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testTrainingDeSerializedModel() throws IOException {

        model.serializeModel("target/testOLRCrossfoldWrite.txt");
        OLRCrossFold olrCrossFold = OLRCrossFold.deSerializeModel("target/testOLRCrossfoldWrite.txt");
        olrCrossFold.train();
    }

}
