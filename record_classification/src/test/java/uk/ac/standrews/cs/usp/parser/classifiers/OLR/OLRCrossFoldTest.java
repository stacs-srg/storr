package uk.ac.standrews.cs.usp.parser.classifiers.OLR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.usp.tools.configuration.MachineLearningConfiguration;

/**
 *
 * Created by fraserdunlop on 06/05/2014 at 11:37.
 */
public class OLRCrossFoldTest {

    private Properties properties = MachineLearningConfiguration.getDefaultProperties();
    private VectorFactory vectorFactory;
    private ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
    private OLRCrossFold model;

    @Before
    public void setup() throws IOException {

        vectorFactory = new VectorFactory();
        populateDictionary();
        //        properties.setProperty("numFeatures", "50");
        properties.setProperty("numCategories", "8");
        trainingVectorList = generateTrainingVectors();
        model = new OLRCrossFold(trainingVectorList, properties);
        model.train();

    }

    private ArrayList<NamedVector> generateTrainingVectors() throws IOException {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
        while ((line = br.readLine()) != null) {
            trainingVectorList.add(createTrainingVector(line));
        }
        return trainingVectorList;
    }

    private void populateDictionary() throws IOException {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitLine = line.split("\t");
            String descriptionFromFile = splitLine[1].trim();
            vectorFactory.updateDictionary(descriptionFromFile);
        }
    }

    private NamedVector createTrainingVector(final String line) {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        int id = CodeFactory.getInstance().getCode(codeFromFile).getID();
        return vectorFactory.createNamedVectorFromString(descriptionFromFile, Integer.toString(id));
    }

    private BufferedReader getBufferedReaderOfCodeDictionaryFile() throws FileNotFoundException {

        File file = new File(getClass().getResource("/CodeFactoryOLRTestFile.txt").getFile());
        return new BufferedReader(new FileReader(file));
    }

    @Test
    public void testModel() throws IOException {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            testClassifyWithCodeAsDescription(model, line);
        }

    }

    @Test
    public void testWrite() throws Exception {

        model.serializeModel("target/testOLRCrossfoldWrite.txt");
        OLRCrossFold olrCrossFold = OLRCrossFold.deSerializeModel("target/testOLRCrossfoldWrite.txt");

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            testClassifyWithCodeAsDescription(olrCrossFold, line);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTrainingDeSerializedModel() throws IOException {

        model.serializeModel("target/testOLRCrossfoldWrite.txt");
        OLRCrossFold olrCrossFold = OLRCrossFold.deSerializeModel("target/testOLRCrossfoldWrite.txt");
        olrCrossFold.train();

    }

    private void testClassifyWithCodeAsDescription(final OLRCrossFold model, final String line) {

        String codeFromFile = getCodeFromLine(line);
        Vector testVector = vectorFactory.createVectorFromString(codeFromFile);
        int id = getCodeID(codeFromFile);
        int classification = getClassification(model, testVector);
        Assert.assertEquals(id, classification);
    }

    private int getCodeID(final String codeFromFile) {

        return CodeFactory.getInstance().getCode(codeFromFile).getID();
    }

    private int getClassification(final OLRCrossFold model, final Vector testVector) {

        return model.classifyFull(testVector).maxValueIndex();
    }

    private String getCodeFromLine(final String line) {

        String[] splitLine = line.split("\t");
        return splitLine[0].trim();
    }

}
