package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.FolderCreationException;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * This class integrates the training of machine learning models and the
 * classification of records using those models. The classification process is
 * as follows: <br>
 * <br>
 * The gold standard training file is read in from the command line and a
 * {@link Bucket} of {@link Record}s are created from this file. A
 * {@link VectorFactory} is then created to manage the creation of vectors for
 * these records. The vectorFactory also manages the mapping of vectors IDs to
 * words, ie the vector dictionary. <br>
 * <br>
 * An {@link AbstractClassifier} is then created from the training bucket and
 * the model(s) are trained and saved to disk. <br>
 * <br>
 * The records to be classified are held in a file with the correct format as
 * specified by NRS. One record per line. This class initiates the reading of
 * these records. These are stored as {@link Record} objects inside a
 * {@link Bucket}. <br>
 * <br>
 * After the records have been created and stored in a bucket, classification
 * can begin. This is carried out by the {@link BucketClassifier} class which in
 * turn implements the {@link MachineLearningClassificationPipeline}. Please see this
 * class for implementation details. <br>
 * <br>
 * Some initial metrics are then printed to the console and classified records
 * are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 */
public final class PIlot {

    private static final Logger LOGGER = LoggerFactory.getLogger(PIlot.class);

    private static String experimentalFolderName;
    private static File training;
    private static File prediction;

    private PIlot() {

        // no public constructor
    }

    /**
     * Entry method for training and classifying a batch of records into
     * multiple codes.
     * 
     * @param args
     *            <file1> training file <file2> file to classify
     * @throws Exception
     *             If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        Timer timer = PipelineUtils.initAndStartTimer();

        setupExperimentalFolders("Experiments");

        parseInput(args);

        GoldStandardBucketGenerator trainingGenerator = new GoldStandardBucketGenerator();
        Bucket trainingBucket = trainingGenerator.generate(training);

        PredictionBucketGenerator predictionBucketGenerator = new PredictionBucketGenerator();
        Bucket predictionBucket = predictionBucketGenerator.createPredictionBucket(prediction);

        PipelineUtils.printStatusUpdate();

        ClassifierTrainer trainer = PipelineUtils.train(trainingBucket, experimentalFolderName);

        ClassificationHolder classifier = PipelineUtils.classify(trainingBucket, predictionBucket, trainer);

        LOGGER.info("Exact Matched Bucket Size: " + classifier.getExactMatched().size());
        LOGGER.info("Machine Learned Bucket Size: " + classifier.getMachineLearned().size());

        PipelineUtils.writeRecords(classifier.getAllClassified(), experimentalFolderName);

        generateAndPrintStatistics(classifier, experimentalFolderName);

        timer.stop();

    }

    private static void parseInput(final String[] args) {

        if (args.length != 2) {
            System.err.println("You must supply 2 arguments");
            System.err.println("usage: $" + PIlot.class.getSimpleName() + "    <trainingFile>    <predictionFile>");
        }
        else {
            training = new File(args[0]);
            prediction = new File(args[1]);
            PipelineUtils.exitIfDoesNotExist(training);
            PipelineUtils.exitIfDoesNotExist(prediction);
        }
    }

    private static void generateAndPrintStatistics(final ClassificationHolder classifier, final String experimentalFolderName) throws IOException {

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(classifier.getAllClassified());

        PipelineUtils.generateAndPrintStats(classifier.getAllClassified(), "All Records", "AllRecords", experimentalFolderName);

        PipelineUtils.generateAndPrintStats(uniqueRecordsOnly, "Unique Only", "UniqueOnly", experimentalFolderName);
    }

    private static void setupExperimentalFolders(final String baseFolder) {

        experimentalFolderName = Utils.getExperimentalFolderName(baseFolder, "Experiment");

        if (!(new File(experimentalFolderName).mkdirs() && new File(experimentalFolderName + "/Reports").mkdirs() && new File(experimentalFolderName + "/Data").mkdirs() && new File(experimentalFolderName + "/Models").mkdirs())) { throw new FolderCreationException(
                        "couldn't create experimental folder"); }
    }

}
