package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LogLengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassifierPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ExactMatchPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.IPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.MetricsWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

import com.google.common.io.Files;

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
 * An IClassifier is then created from the training bucket and
 * the model(s) are trained and saved to disk. <br>
 * <br>
 * The records to be classified are held in a file with the correct format as
 * specified by NRS. One record per line. This class initiates the reading of
 * these records. These are stored as {@link Record} objects inside a
 * {@link Bucket}. <br>
 * <br>
 * After the records have been created and stored in a bucket, classification
 * can begin. This is carried out by the BucketClassifier class which in
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
    private static final String usageHelp = "usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>  <propertiesFile>  <trainingRatio(optional)>    <output multiple classificatiosn";

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

        printArgs(args);

        String experimentalFolderName;
        File goldStandard;
        Bucket trainingBucket;
        Bucket predictionBucket;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("TestExperiments");

        goldStandard = parseGoldStandFile(args);
        parseProperties(args);
        double trainingRatio = parseTrainingRatio(args);
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

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);

        OLRClassifier.setModelPath(experimentalFolderName + "/Models/olrModel");
        OLRClassifier olrClassifier = new OLRClassifier();
        olrClassifier.train(trainingBucket);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(exactMatchClassifier);
        IPipeline machineLearningClassifier = new ClassifierPipeline(olrClassifier, trainingBucket, new LogLengthWeightedLossFunction(), multipleClassifications, true);

        Bucket notExactMatched = exactMatchPipeline.classify(predictionBucket);
        Bucket notMachineLearned = machineLearningClassifier.classify(notExactMatched);
        Bucket successfullyClassifiedMachineLearning = machineLearningClassifier.getSuccessfullyClassified();
        Bucket successfullyExactMatched = exactMatchPipeline.getSuccessfullyClassified();
        Bucket uniqueRecordsExactMatched = BucketFilter.uniqueRecordsOnly(successfullyExactMatched);
        Bucket uniqueRecordsMachineLearned = BucketFilter.uniqueRecordsOnly(successfullyClassifiedMachineLearning);
        Bucket uniqueRecordsNotMatched = BucketFilter.uniqueRecordsOnly(notMachineLearned);

        LOGGER.info("Exact Matched Bucket Size: " + successfullyExactMatched.size());
        LOGGER.info("Machine Learned Bucket Size: " + successfullyClassifiedMachineLearning.size());
        LOGGER.info("Not Classifed Bucket Size: " + notMachineLearned.size());
        LOGGER.info("Unique Exact Matched Bucket Size: " + uniqueRecordsExactMatched.size());
        LOGGER.info("UniqueMachine Learned Bucket Size: " + uniqueRecordsMachineLearned.size());
        LOGGER.info("Unique Not Classifed Bucket Size: " + uniqueRecordsNotMatched.size());

        Bucket allClassifed = BucketUtils.getUnion(successfullyExactMatched, successfullyClassifiedMachineLearning);
        Bucket allRecords = BucketUtils.getUnion(allClassifed, notMachineLearned);
        Assert.assertTrue(allRecords.size() == predictionBucket.size());

        writeRecords(experimentalFolderName, allRecords);

        writeComparisonFile(experimentalFolderName, allRecords);

        LOGGER.info("********** Output Stats **********");

        printAllStats(experimentalFolderName, codeIndex, allRecords, "allRecords");
        printAllStats(experimentalFolderName, codeIndex, successfullyExactMatched, "exactMatched");
        printAllStats(experimentalFolderName, codeIndex, successfullyClassifiedMachineLearning, "machineLearned");

        timer.stop();

        return allRecords;
    }

    private void printArgs(final String[] args) {

        String argsString = "";
        for (String string : args) {
            argsString += string + " ";
        }
        LOGGER.info("Running with args: " + argsString.trim());
    }

    private void printAllStats(final String experimentalFolderName, final CodeIndexer codeIndex, final Bucket bucket, final String identifier) throws IOException {

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(bucket);

        LOGGER.info("All Records");
        LOGGER.info("All Records Bucket Size: " + bucket.size());
        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket, codeIndex), codeIndex);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(bucket, codeMetrics);
        MetricsWriter metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write(identifier, "nonUniqueRecords");
        accuracyMetrics.prettyPrint("AllRecords");

        LOGGER.info("Unique Only");
        LOGGER.info("Unique Only  Bucket Size: " + uniqueRecordsOnly.size());

        CodeMetrics codeMetrics1 = new CodeMetrics(new StrictConfusionMatrix(uniqueRecordsOnly, codeIndex), codeIndex);
        accuracyMetrics = new ListAccuracyMetrics(uniqueRecordsOnly, codeMetrics1);
        accuracyMetrics.prettyPrint("Unique Only");
        metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write(identifier, "uniqueRecords");
        accuracyMetrics.prettyPrint("UniqueRecords");
    }

    private void writeComparisonFile(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String comparisonReportPath = "/Data/" + "MachineLearning" + "/comaprison.txt";
        final File outputPath2 = new File(experimentalFolderName + comparisonReportPath);
        Files.createParentDirs(outputPath2);

        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(outputPath2, "\t");
        for (final Record record : allClassifed) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    private void writeRecords(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String nrsReportPath = "/Data/" + "MachineLearning" + "/NRSData.txt";
        final File outputPath = new File(experimentalFolderName + nrsReportPath);
        Files.createParentDirs(outputPath);
        final DataClerkingWriter writer = new DataClerkingWriter(outputPath);
        for (final Record record : allClassifed) {
            writer.write(record);
        }
        writer.close();
    }

    private static File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);

        }
        return goldStandard;
    }

    public File parseProperties(String[] args) {

        File properties = null;
        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            properties = new File(args[1]);
            PipelineUtils.exitIfDoesNotExist(properties);
            MachineLearningConfiguration.loadProperties(properties);
        }
        return properties;
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 5) {
            System.err.println(usageHelp);
        }
        else {
            if (args[3].equals("1")) { return true; }
        }
        return false;

    }

    private static double parseTrainingRatio(final String[] args) {

        double trainingRatio = DEFAULT_TRAINING_RATIO;
        if (args.length > 1) {
            double userRatio = Double.valueOf(args[2]);
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
