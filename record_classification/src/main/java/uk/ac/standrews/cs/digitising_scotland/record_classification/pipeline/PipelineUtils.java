package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.FolderCreationException;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Utility class containing methods to help with the creation and use of the exact match and machine learning pipelines.
 * @author jkc25
 *
 */
public final class PipelineUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineUtils.class);

    private PipelineUtils() {

    }

    //    public static void generateAndPrintStats(final Bucket classifiedBucket, final CodeIndexer codeIndexer, final String header, final String bucketIdentifier, final String experimentalFolderName, final String identifier) throws IOException {
    //
    //        LOGGER.info(header);
    //        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(classifiedBucket, codeIndexer), codeIndexer);
    //        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket, codeMetrics);
    //        accuracyMetrics.prettyPrint(header);
    //        generateStats(classifiedBucket, codeMetrics, accuracyMetrics, codeIndexer, experimentalFolderName, bucketIdentifier, identifier);
    //    }

    //    public static void generateStats(final Bucket bucket, CodeMetrics codeMetrics, final ListAccuracyMetrics accuracyMetrics, final CodeIndexer codeIndexer, final String experimentalFolderName, final String bucketIdentifier, final String identifier) throws IOException {
    //
    //        final String matrixDataPath = experimentalFolderName + "/Data/" + identifier + "/classificationCountMatrix.csv";
    //        final String matrixImagePath = "classificationMatrix";
    //        final String reportspath = experimentalFolderName + "/Reports/";
    //
    //        final String strictCodeStatsPath = experimentalFolderName + "/Data/" + identifier + "/strictCodeStats" + bucketIdentifier + ".csv";
    //        final String strictCodePath = "strictCodeStats" + bucketIdentifier;
    //        printCodeMetrics(bucket, codeMetrics, accuracyMetrics, codeIndexer, strictCodeStatsPath, strictCodePath, experimentalFolderName, identifier);
    //
    //        final String softCodeStatsPath = experimentalFolderName + "/Data/" + identifier + "/softCodeStats" + bucketIdentifier + ".csv";
    //        final String softCodePath = "softCodeStats" + bucketIdentifier;
    //        printCodeMetrics(bucket, codeMetrics, accuracyMetrics, codeIndexer, softCodeStatsPath, softCodePath, experimentalFolderName, identifier);
    //
    //        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(bucket, codeIndexer);
    //        double totalCorrectlyPredicted = invertedConfusionMatrix.getTotalCorrectlyPredicted();
    //        LOGGER.info("Number of predictions too specific: " + totalCorrectlyPredicted);
    //        LOGGER.info("Proportion of predictions too specific: " + totalCorrectlyPredicted / invertedConfusionMatrix.getTotalPredicted());
    //
    //        runRscript("src/main/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
    //        runRscript("src/main/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
    //        runRscript("src/main/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);
    //
    //    }

    public static String printCodeMetrics(final Bucket bucket, final CodeMetrics codeMetrics, final ListAccuracyMetrics accuracyMetrics, final CodeIndexer codeIndexer, final String strictCodeStatsPath, final String codeStatsPath, final String experimentalFolderName, final String identifier) {

        LOGGER.info(codeMetrics.getMicroStatsAsString());
        codeMetrics.writeStats(strictCodeStatsPath);
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(experimentalFolderName, codeStatsPath);
        return strictCodeStatsPath;
    }

    public static Timer initAndStartTimer() {

        Timer timer = new Timer();
        timer.start();
        return timer;
    }

    public static ClassifierTrainer train(final Bucket trainingBucket, final String experimentalFolderName, final CodeIndexer codeIndex) throws Exception {

        ClassifierTrainer trainer = new ClassifierTrainer(trainingBucket, experimentalFolderName, codeIndex);
        trainer.trainExactMatchClassifier();
        trainer.trainOLRClassifier();
        return trainer;
    }

    public static ClassifierTrainer getExistingModels(final String modelLocations, final Bucket trainingBucket, final String experimentalFolderName) {

        ClassifierTrainer trainer = new ClassifierTrainer(trainingBucket, experimentalFolderName, null);
        trainer.getExistingsModels(modelLocations);

        return trainer;
    }

    public static void printStatusUpdate() {

        LOGGER.info("********** Training Classifiers **********");
        LOGGER.info("Training with a dictionary size of: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numFeatures"));
        LOGGER.info("Training with this number of output classes: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numCategories"));
    }

    public static boolean checkFileType(final File inputFile) throws IOException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
        String line = br.readLine();
        br.close();
        final int expectedLineLength = 38;
        final String[] length = line.split(Utils.getCSVComma());
        if (length.length == expectedLineLength) { return true; }
        return false;
    }

    public static String setupExperimentalFolders(final String baseFolder) {

        final String experimentalFolderName = Utils.getExperimentalFolderName(baseFolder, "Experiment");

        if (!(new File(experimentalFolderName).mkdirs() && new File(experimentalFolderName + "/Reports").mkdirs() && new File(experimentalFolderName + "/Data").mkdirs() && new File(experimentalFolderName + "/Models").mkdirs())) { throw new FolderCreationException(
                        "couldn't create experimental folder"); }

        return experimentalFolderName;
    }

    public static void exitIfDoesNotExist(final File file) {

        if (!file.exists()) {
            LOGGER.error(file.getAbsolutePath() + " does not exist. Exiting");
            throw new RuntimeException();
            // System.exit(2);
        }

    }

}
