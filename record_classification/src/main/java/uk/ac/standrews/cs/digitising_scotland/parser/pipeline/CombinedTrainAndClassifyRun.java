package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.File;
import java.util.Iterator;

import org.junit.Assert;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class CombinedTrainAndClassifyRun has only a run and a main method that trains a set of classifiers (using {@link TrainClassifiers})
 * and then classifies the using {@link Classifier} and compares the results to those gotten using {@link TrainAndClassify}.
 */
public class CombinedTrainAndClassifyRun {

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {

        String trainingFile = "kilmarnockBasedCoDTrainingPipe.txt";
        String testingFile = "kilmarnockBasedCoDToBeClassified.txt";
        CombinedTrainAndClassifyRun c = new CombinedTrainAndClassifyRun();
        c.run(trainingFile, testingFile);
    }

    /**
     * Runs the classifier comparison.
     * @param trainingFile File to train on.
     * @param testingFile  File to test on.
     *
     * @throws Exception the exception
     */
    public void run(final String trainingFile, final String testingFile) throws Exception {

        TrainClassifiers trainer = new TrainClassifiers();

        Bucket trainingBucket = trainer.createCleanedBucketFromFile(trainingFile);
        VectorFactory vectorFactory = new VectorFactory(trainingBucket);

        trainer.trainClassifiers(trainingBucket, vectorFactory);

        Classifier classifier = new Classifier(new File(testingFile), vectorFactory);
        Bucket seperates = classifier.classifyBucket();
        Utils.writeBucketToFileNrsFormat(seperates);

        cleanUp();

        TrainAndClassify trainAndClassifiy = new TrainAndClassify(trainingFile, testingFile);
        Bucket combined = trainAndClassifiy.trainAndClassify();

        Iterator<Record> seperateIterator = seperates.iterator();
        Iterator<Record> combinedIterator = combined.iterator();
        Assert.assertEquals(seperates.size(), combined.size());
        while (seperateIterator.hasNext() && combinedIterator.hasNext()) {
            Record record1 = seperateIterator.next();
            Record record2 = combinedIterator.next();
            Assert.assertEquals(record1, record2);
        }

        cleanUp();
    }

    /**
     * Clean up.
     */
    public void cleanUp() {

        Assert.assertTrue(new File("target/OLRModel").delete());
        Assert.assertTrue(new File("target/lookupTable.ser").delete());
        Assert.assertTrue(new File("target/naiveBayesModelPath/naiveBayesModel.bin").delete());
        Assert.assertTrue(new File("target/nGramModel.ser").delete());
    }

}
