package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.File;
import java.util.Iterator;

import org.junit.Assert;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

public class CombinedTrainAndClassifyRun {

    public static void main(final String[] args) throws Exception {

        CombinedTrainAndClassifyRun c = new CombinedTrainAndClassifyRun();
        c.run();
    }

    public void run() throws Exception {

        String trainingFile = "kilmarnockBasedCoDTrainingPipe.txt";
        String testingFile = "kilmarnockBasedCoDToBeClassified.txt";
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

    public void cleanUp() {

        Assert.assertTrue(new File("target/OLRModel").delete());
        Assert.assertTrue(new File("target/lookupTable.ser").delete());
        Assert.assertTrue(new File("target/naiveBayesModelPath/naiveBayesModel.bin").delete());
        Assert.assertTrue(new File("target/nGramModel.ser").delete());
    }

}
