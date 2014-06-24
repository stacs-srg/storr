package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.OLR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

@RunWith(Parameterized.class)
public class OLRTest {

    private final Properties properties;
    private ArrayList<NamedVector> vectors;
    private VectorFactory vectorFactory;

    /**
     * Parameterized constructor.
     * @param properties properties for use in configuring OLR
     */
    public OLRTest(final Properties properties) {

        this.properties = properties;
    }

    @Before
    public void setup() {

        vectorFactory = new VectorFactory(); //FIXME
        vectors = createVectors();
        MachineLearningConfiguration.getDefaultProperties().setProperty("numFeatures", "20");
        MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", "20");

    }

    /**
     * Sets up different parameter sets for the test.
     * @return the sets of parameters for the parameterized test.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {

        final List<Object[]> parameters = new ArrayList<Object[]>();

        Properties properties = new Properties(MachineLearningConfiguration.getDefaultProperties());
        properties.setProperty("numCategories", "2");
        //  properties.setProperty("numFeatures", "50");
        properties.setProperty("perTermLearning", "true");
        properties.setProperty("olrRegularisation", "true");
        parameters.add(new Object[]{new Properties(properties)});

        properties.setProperty("perTermLearning", "true");
        properties.setProperty("olrRegularisation", "false");
        parameters.add(new Object[]{new Properties(properties)});

        properties.setProperty("perTermLearning", "false");
        properties.setProperty("olrRegularisation", "true");
        parameters.add(new Object[]{new Properties(properties)});

        properties.setProperty("perTermLearning", "false");
        properties.setProperty("olrRegularisation", "false");
        parameters.add(new Object[]{new Properties(properties)});

        return parameters;
    }

    @Test
    public void testTrainAndClassify() throws Exception {

        OLR olr = new OLR(properties);
        trainOLROnTrainingVectors(olr);
        vectorFactory.updateDictionary("banana dog dragon");
        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("banana")).get(0) > 0.5);
        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("dog")).get(0) < 0.5);
        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("dragon")).get(0) < 0.5);
    }

    @Test
    public void testTrainAndClassifySimpleConstructor() throws Exception {

        MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", "2");
        //MachineLearningConfiguration.getDefaultProperties().setProperty("numFeatures", "50");

        OLR olr = new OLR();
        trainOLROnTrainingVectors(olr);
        vectorFactory.updateDictionary("banana dog dragon");

        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("banana")).get(0) > 0.5);
        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("dog")).get(0) < 0.5);
        Assert.assertTrue(olr.classifyFull(vectorFactory.createVectorFromString("dragon")).get(0) < 0.5);
    }

    @Test
    public void testSerializeAndDeSerialize() throws Exception {

        OLR olr1 = new OLR(properties);

        trainOLROnTrainingVectors(olr1);

        olr1.serializeModel("target/OLRWriteTest.txt");
        OLR olr2 = OLR.deSerializeModel("target/OLRWriteTest.txt");

        trainOLROnTrainingVectors(olr1);
        trainOLROnTrainingVectors(olr2);

        for (Vector vector : vectors) {
            Assert.assertEquals(olr1.classifyFull(vector), olr2.classifyFull(vector));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAlpha() {

        OLR olr = new OLR();
        olr.alpha(0.5);
        olr.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLambda() {

        OLR olr = new OLR();
        olr.lambda(0.5);
        olr.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLearningRate() {

        OLR olr = new OLR();
        olr.learningRate(0.5);
        olr.close();
    }

    private void trainOLROnTrainingVectors(final OLR olr) {

        for (int i = 0; i < 10; i++) {
            for (NamedVector vector : vectors) {
                olr.train(Integer.parseInt(vector.getName()), vector);
            }
        }
    }

    private ArrayList<NamedVector> createVectors() {

        ArrayList<String> fruits = createFruitStrings();
        ArrayList<String> animals = createAnimalStrings();

        for (String fruit : fruits) {
            vectorFactory.updateDictionary(fruit);
        }

        for (String animal : animals) {
            vectorFactory.updateDictionary(animal);
        }

        ArrayList<NamedVector> fruitVectors = makeVectors(fruits, "0");

        ArrayList<NamedVector> animalVectors = makeVectors(animals, "1");

        ArrayList<NamedVector> trainingVectors = new ArrayList<NamedVector>();
        trainingVectors.addAll(fruitVectors);
        trainingVectors.addAll(animalVectors);

        return trainingVectors;
    }

    private ArrayList<NamedVector> makeVectors(final ArrayList<String> strings, final String classification) {

        ArrayList<NamedVector> vectors = new ArrayList<NamedVector>();
        for (String string : strings) {
            NamedVector namedVector = createTrainingVector(classification, string);
            vectors.add(namedVector);
        }
        return vectors;
    }

    private NamedVector createTrainingVector(final String classification, final String string) {

        return vectorFactory.createNamedVectorFromString(string, classification);
    }

    private ArrayList<String> createFruitStrings() {

        ArrayList<String> fruits = new ArrayList<String>();
        addFruits(fruits);
        return fruits;
    }

    private ArrayList<String> createAnimalStrings() {

        ArrayList<String> animals = new ArrayList<String>();
        addAnimals(animals);
        return animals;
    }

    private void addAnimals(final ArrayList<String> animals) {

        animals.add("cat dog");
        animals.add("dragon dog");
        animals.add("rat sloth");
        animals.add("sloth man");
        animals.add("dragon");
        animals.add("hamster rat");
        animals.add("fly lion");
        animals.add("dog dog");
    }

    private void addFruits(final ArrayList<String> fruits) {

        fruits.add("apple banana");
        fruits.add("tangerine banana");
        fruits.add("apple lime");
        fruits.add("lime kiwi");
        fruits.add("kiwi strawberry");
        fruits.add("banana passion fruit lemon");
        fruits.add("persimmon pomegranate");
        fruits.add("dragon fruit");
    }
}
