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

import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.usp.tools.configuration.MachineLearningConfiguration;

public class OLRShuffledTest {

    private Properties properties = MachineLearningConfiguration.getDefaultProperties();
    private VectorFactory vectorFactory;
    private ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
    private OLRShuffled model;

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

    @Test
    public void testClassify() throws Exception {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        while ((line = br.readLine()) != null) {
            testClassifyWithCodeAsDescription(model, line);
        }
    }

    @Test
    public void testDefaultPropertiesConstructor() {

        OLRShuffled olr = new OLRShuffled(trainingVectorList);
        olr.run();
    }

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

    private void testClassifyWithCodeAsDescription(final OLRShuffled model, final String line) {

        String codeFromFile = getCodeFromLine(line);
        Vector testVector = vectorFactory.createVectorFromString(codeFromFile);
        int id = getCodeID(codeFromFile);
        int classification = getClassification(model, testVector);
        Assert.assertEquals(id, classification);
    }

    private int getCodeID(final String codeFromFile) {

        return CodeFactory.getInstance().getCode(codeFromFile).getID();
    }

    private int getClassification(final OLRShuffled model, final Vector testVector) {

        return model.classifyFull(testVector).maxValueIndex();
    }

    private String getCodeFromLine(final String line) {

        String[] splitLine = line.split("\t");
        return splitLine[0].trim();
    }

    private BufferedReader getBufferedReaderOfCodeDictionaryFile() throws FileNotFoundException {

        File file = new File(getClass().getResource("/CodeFactoryOLRTestFile.txt").getFile());
        return new BufferedReader(new FileReader(file));
    }

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

    private void populateDictionary() throws IOException {

        BufferedReader br = getBufferedReaderOfCodeDictionaryFile();
        String line;
        ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();
        while ((line = br.readLine()) != null) {
            vectorFactory.updateDictionary(line.split("\t")[1]);
        }
    }

    private NamedVector createTrainingVector(final String line) {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        int id = CodeFactory.getInstance().getCode(codeFromFile).getID();
        return vectorFactory.createNamedVectorFromString(descriptionFromFile, String.valueOf(id));
    }

}
