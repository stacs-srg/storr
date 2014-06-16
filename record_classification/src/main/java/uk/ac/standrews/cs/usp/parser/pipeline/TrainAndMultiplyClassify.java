package uk.ac.standrews.cs.usp.parser.pipeline;

import java.io.File;
import java.io.IOException;

import uk.ac.standrews.cs.usp.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.usp.parser.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.InputFormatException;
import uk.ac.standrews.cs.usp.parser.datastructures.ListAccuracyMetrics;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.RecordFactory;
import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;

/**
 * This class integrates the training of machine learning models and the classification of records using those models.
 * The classification process is as follows:
 * 
 * The gold standard training file is read in from the command line and a {@link Bucket} of {@link Record}s are created from this file.
 * A {@link VectorFactory} is then created to manage the creation of vectors for these records. The vectorFactory also manages
 * the mapping of vectors IDs to words, ie the vector dictionary.
 * 
 * An {@link AbstractClassifier} is then created from the training bucket and the model(s) are trained and saved to disk.
 * 
 * The records to be classified are held in a file with the correct format as specified by NRS. One record per line.
 * This class initiates the reading of these records. These are stored as {@link Record} objects inside a {@link Bucket}.
 *
 * 
 * 
 * 
 * @author jkc25
 *
 */
public class TrainAndMultiplyClassify {

    private static VectorFactory vectorFactory;

    /**
     * Entry method for training and classifying a batch of records into multiple codes.
     * 
     * @param args <file1> training file <file2> file to classify
     * @throws Exception If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        File training = new File(args[0]);
        File prediction = new File(args[1]);

        System.out.println("********** Training Bucket **********");

        Bucket bucket = createTrainingBucket(training);

        vectorFactory = new VectorFactory(bucket);

        AbstractClassifier classifier = trainClassifier(bucket, vectorFactory);

        Bucket predicitionBucket = createPredictionBucket(prediction);

        RecordClassificationPipeline recordClassifier = new RecordClassificationPipeline(classifier);

        BucketClassifier bucketClassifier = new BucketClassifier(recordClassifier);

        System.out.println("********** Classifying Bucket **********");

        Bucket classifiedBucket = bucketClassifier.classify(predicitionBucket);

        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket);

        System.out.println("********** **********");
        System.out.println(classifiedBucket);
        accuracyMetrics.prettyPrint();
    }

    private static Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        try {
            toClassify = new Bucket(RecordFactory.makeCodedRecordsFromFile(prediction));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InputFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return toClassify;
    }

    private static AbstractClassifier trainClassifier(final Bucket bucket, final VectorFactory vectorFactory) throws Exception {

        AbstractClassifier olrClassifier = new OLRClassifier(vectorFactory);
        olrClassifier.train(bucket);
        return olrClassifier;
    }

    private static Bucket createTrainingBucket(final File training) throws IOException, InputFormatException {

        Bucket bucket = new Bucket();
        Iterable<Record> records = RecordFactory.makeCodedRecordsFromFile(training);
        bucket.addCollectionOfRecords(records);
        return bucket;
    }

}
