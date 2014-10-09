package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassifierPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassifierTrainer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ExactMatchPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.IPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.MetricsWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

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
 * turn implements the {@link ClassifierPipeline}. Please see this
 * class for implementation details. <br>
 * <br>
 * Some initial metrics are then printed to the console and classified records
 * are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 */
public final class TrainClassifyOneFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassifyOneFile.class);
    private static double DEFAULT_TRAINING_RATIO = 0.8;

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

        TrainClassifyOneFile instance = new TrainClassifyOneFile();
        instance.run(args);
    }

    public Bucket run(final String[] args) throws Exception {

        String experimentalFolderName;
        File goldStandard;
        Bucket trainingBucket;
        Bucket predictionBucket;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("TestExperiments");

        goldStandard = parseGoldStandFile(args);
        double trainingRatio = parseTrainingPct(args);
        boolean multipleClassifications = parseMultipleClassifications(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        BucketGenerator generator = new BucketGenerator(codeDictionary);
        Bucket allInputRecords = generator.generateTrainingBucket(goldStandard);

        Bucket[] trainingPredicition = randomlyAssignToTrainingAndPrediction(allInputRecords, trainingRatio);
        trainingBucket = trainingPredicition[0];
        predictionBucket = trainingPredicition[1];

        LOGGER.info("********** Training Classifiers **********");

        CodeIndexer codeIndex = new CodeIndexer(allInputRecords);
        final ClassifierTrainer trainer = PipelineUtils.train(trainingBucket, experimentalFolderName, codeIndex);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(trainer.getExactMatchClassifier());
        IPipeline machineLearningClassifier = new ClassifierPipeline(trainer.getOlrClassifier(), trainingBucket, multipleClassifications);

        Bucket notExactMatched = exactMatchPipeline.classify(predictionBucket);
        Bucket notMachineLearned = machineLearningClassifier.classify(notExactMatched);
        Bucket successfullyClassifiedExactMatch = exactMatchPipeline.getSuccessfullyClassified();
        Bucket successfullyClassifiedMachineLearning = machineLearningClassifier.getSuccessfullyClassified();

        LOGGER.info("Exact Matched Bucket Size: " + successfullyClassifiedExactMatch.size());
        LOGGER.info("Machine Learned Bucket Size: " + successfullyClassifiedMachineLearning.size());
        LOGGER.info("Not Classifed Bucket Size: " + notMachineLearned.size());

        Bucket allClassifed = BucketUtils.getUnion(successfullyClassifiedExactMatch, successfullyClassifiedMachineLearning);
        Bucket allOutputRecords = BucketUtils.getUnion(allClassifed, notMachineLearned);

        PipelineUtils.writeRecords(allClassifed, experimentalFolderName, "MachineLearning");

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(allClassifed);
        printAllStats(experimentalFolderName, codeIndex, allClassifed, uniqueRecordsOnly);
        printAllStats(experimentalFolderName, codeIndex, successfullyClassifiedMachineLearning, BucketFilter.uniqueRecordsOnly(successfullyClassifiedMachineLearning));
        timer.stop();

        return allOutputRecords;

    }

    private void printAllStats(final String experimentalFolderName, final CodeIndexer codeIndex, final Bucket allClassifed, final Bucket uniqueRecordsOnly) throws IOException {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(allClassifed, codeIndex), codeIndex);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(allClassifed, codeMetrics);
        MetricsWriter metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write("machine learning", "firstBucket");
        accuracyMetrics.prettyPrint("All Records");

        LOGGER.info("Unique Only");
        LOGGER.info("Unique Only  Bucket Size: " + uniqueRecordsOnly.size());

        CodeMetrics codeMetrics1 = new CodeMetrics(new StrictConfusionMatrix(uniqueRecordsOnly, codeIndex), codeIndex);
        accuracyMetrics = new ListAccuracyMetrics(uniqueRecordsOnly, codeMetrics1);
        accuracyMetrics.prettyPrint("Unique Only");
        metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write("machine learning", "unique records");
        accuracyMetrics.prettyPrint("Unique Records");
    }

    private static File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 4) {
            System.err.println("usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);

        }
        return goldStandard;
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExsistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>    <output multiple classificatiosn");
        }
        else {
            if (args[2].equals("1")) { return true; }
        }
        return false;

    }

    private static double parseTrainingPct(final String[] args) {

        double trainingRatio = DEFAULT_TRAINING_RATIO;
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
        return trainingRatio;
    }

    private void generateAndPrintStatistics(final Bucket allClassifed, final CodeIndexer codeIndexer, final String experimentalFolderName) throws IOException {

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(allClassifed);

        PipelineUtils.generateAndPrintStats(allClassifed, codeIndexer, "All Records", "AllRecords", experimentalFolderName, "MachineLearning");

        PipelineUtils.generateAndPrintStats(uniqueRecordsOnly, codeIndexer, "Unique Only", "UniqueOnly", experimentalFolderName, "MachineLearning");
    }

    private Bucket[] randomlyAssignToTrainingAndPrediction(final Bucket bucket, final double trainingRatio) {

        Bucket[] buckets = initBuckets();

        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                buckets[0].addRecordToBucket(record);
            }
            else {
                buckets[1].addRecordToBucket(record);
            }
        }
        return buckets;
    }

    private Bucket[] initBuckets() {

        Bucket[] buckets = new Bucket[2];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

}
