package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.NaiveBayesClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenShteinCleaner;

/**
 * The Class TrainClassifiers trains a set of classifiers.
 */
public class TrainClassifiers {

    /**
     * Instantiates a new train classifiers.
     */
    public TrainClassifiers() {

    }

    /**
     * Trains all classifiers.
     * Classifiers are added to a List and the list iterated through to train each one.
     *
     * @param trainingBucket the training bucket
     * @param vectorFactory vector factory
     * @return the array list
     * @throws Exception the exception
     */
    public ArrayList<AbstractClassifier> trainClassifiers(final Bucket trainingBucket, final VectorFactory vectorFactory) throws Exception {

        ArrayList<AbstractClassifier> classifierList = new ArrayList<AbstractClassifier>();
        classifierList.add(new ExactMatchClassifier());
        classifierList.add(new NGramClassifier());
        classifierList.add(new NaiveBayesClassifier(vectorFactory));
        classifierList.add(new OLRClassifier(vectorFactory));

        for (AbstractClassifier abstractClassifier : classifierList) {
            abstractClassifier.train(trainingBucket);
        }

        return classifierList;
    }

    /**
     * Creates the cleaned bucket.
     *
     * @param listOfRecords the list of records
     * @return the bucket
     */
    private Bucket createCleanedBucket(final List<Record> listOfRecords) {

        Bucket trainingBucket = new Bucket(listOfRecords);
//        LevenShteinCleaner.cleanData(trainingBucket);
        return trainingBucket;
    }

    /**
     * Creates the cleaned bucket from file.
     *
     * @param trainingFile the training file
     * @return the bucket
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public Bucket createCleanedBucketFromFile(final String trainingFile) throws IOException, InputFormatException {

        List<Record> listOfRecords = RecordFactory.makeCodedRecordsFromFile(new File(trainingFile));

        Bucket trainingBucket = createCleanedBucket(listOfRecords);
        return trainingBucket;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Must supply a training file. Usage: TrainClassifiers <trainingFile.txt>");
        }
        String trainingFile = args[args.length - 1];
        TrainClassifiers trainingClass = new TrainClassifiers();

        Bucket trainingBucket = trainingClass.createCleanedBucketFromFile(trainingFile);

        VectorFactory vectorFactory = new VectorFactory(trainingBucket); //FIXME this is empty

        trainingClass.trainClassifiers(trainingBucket, vectorFactory);
        System.out.println("Classifiers trained and models written to default locations");
    }

}
