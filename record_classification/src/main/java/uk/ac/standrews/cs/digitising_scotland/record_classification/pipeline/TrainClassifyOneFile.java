package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;

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
public final class TrainClassifyOneFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassifyOneFile.class);

    private static String experimentalFolderName;
    private static File goldStandard;
    private static Bucket trainingBucket;
    private static Bucket predictionBucket;

    private static double trainingRatio = 0.8;

    private TrainClassifyOneFile() {

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

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        parseInput(args);
        File codeDictionaryFile = null; //FIXME
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        GoldStandardBucketGenerator generator = new GoldStandardBucketGenerator(codeDictionary);
        Bucket allRecords = generator.generate(goldStandard);

        randomlyAssignToTrainingAndPrediction(allRecords);

        PipelineUtils.printStatusUpdate();

        CodeIndexer codeIndex = new CodeIndexer(allRecords);
        ClassifierTrainer trainer = PipelineUtils.train(trainingBucket, experimentalFolderName, codeIndex);

        ClassificationHolder classifier = PipelineUtils.classify(allRecords, predictionBucket, trainer);

        LOGGER.info("Exact Matched Bucket Size: " + classifier.getExactMatched().size());
        LOGGER.info("Machine Learned Bucket Size: " + classifier.getMachineLearned().size());

        PipelineUtils.writeRecords(classifier.getAllClassified(), experimentalFolderName);

        generateAndPrintStatistics(classifier, codeIndex);

        timer.stop();

    }

    private static void parseInput(final String[] args) {

        if (args.length > 2) {
            System.err.println("usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);
            if (args.length > 1) {
                double userRatio = Double.valueOf(args[1]);
                if (userRatio > 0 && userRatio < 1) {
                    trainingRatio = userRatio;
                }
                else {
                    System.err.println("trainingRatio must be between 0 and 1. Exiting.");
                    System.exit(1);

                }
            }

        }
    }

    private static void generateAndPrintStatistics(final ClassificationHolder classifier, CodeIndexer codeIndexer) throws IOException {

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(classifier.getAllClassified());

        PipelineUtils.generateAndPrintStats(classifier.getAllClassified(), codeIndexer, "All Records", "AllRecords", experimentalFolderName);

        PipelineUtils.generateAndPrintStats(uniqueRecordsOnly, codeIndexer, "Unique Only", "UniqueOnly", experimentalFolderName);
    }

    private static void randomlyAssignToTrainingAndPrediction(final Bucket bucket) {

        trainingBucket = new Bucket();
        predictionBucket = new Bucket();
        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                trainingBucket.addRecordToBucket(record);
            }
            else {
                predictionBucket.addRecordToBucket(record);
            }
        }
    }

}
