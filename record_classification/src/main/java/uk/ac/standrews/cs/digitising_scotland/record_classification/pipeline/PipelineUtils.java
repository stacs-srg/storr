package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.AbstractConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.InvertedSoftConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.FolderCreationException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
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

    /**
     * Generates a file containing all the codes that were in the given bucket.
     * @param bucket Bucket to generate mapping file from
     */
    protected static void generateActualCodeMappings(final Bucket bucket) {

        Map<String, Integer> codeMapping = new HashMap<>();
        for (Record record : bucket) {
            addAllCodesToMap(codeMapping, record);
        }

        StringBuilder sb = buildOutputString(codeMapping);
        File codeFile = new File("target/customCodeMap.txt");
        Utils.writeToFile(sb.toString(), codeFile.getAbsolutePath());
        CodeFactory.getInstance().loadDictionary(codeFile);
    }

    private static StringBuilder buildOutputString(final Map<String, Integer> codeMapping) {

        StringBuilder sb = new StringBuilder();
        Set<String> keySet = codeMapping.keySet();

        for (String key : keySet) {
            sb.append(key + "\t" + key + "\n");
        }

        return sb;
    }

    private static void addAllCodesToMap(final Map<String, Integer> codeMapping, final Record record) {

        for (CodeTriple currentCodeTriple : record.getGoldStandardClassificationSet()) {
            codeMapping.put(currentCodeTriple.getCode().getCodeAsString(), 1);
        }
    }

    protected static void generateAndPrintStats(final Bucket classifiedBucket, final String header, final String bucketIdentifier, final String experimentalFolderName) throws IOException {

        LOGGER.info(header);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket);
        accuracyMetrics.prettyPrint(header);
        generateStats(classifiedBucket, accuracyMetrics, experimentalFolderName, bucketIdentifier);
    }

    protected static void generateStats(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String experimentalFolderName, final String bucketIdentifier) throws IOException {

        final String matrixDataPath = experimentalFolderName + "/Data/classificationCountMatrix.csv";
        final String matrixImagePath = "classificationMatrix";
        final String reportspath = experimentalFolderName + "/Reports/";

        final String strictCodeStatsPath = experimentalFolderName + "/Data/strictCodeStats" + bucketIdentifier + ".csv";
        final String strictCodePath = "strictCodeStats" + bucketIdentifier;
        printCodeMetrics(bucket, accuracyMetrics, strictCodeStatsPath, strictCodePath, experimentalFolderName);

        final String softCodeStatsPath = experimentalFolderName + "/Data/softCodeStats" + bucketIdentifier + ".csv";
        final String softCodePath = "softCodeStats" + bucketIdentifier;
        printCodeMetrics(bucket, accuracyMetrics, softCodeStatsPath, softCodePath, experimentalFolderName);

        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(bucket);
        double totalCorrectlyPredicted = invertedConfusionMatrix.getTotalCorrectlyPredicted();
        LOGGER.info("Number of predictions too specific: " + totalCorrectlyPredicted);
        LOGGER.info("Proportion of predictions too specific: " + totalCorrectlyPredicted / invertedConfusionMatrix.getTotalPredicted());

        runRscript("src/main/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
        runRscript("src/main/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
        runRscript("src/main/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);

    }

    protected static String printCodeMetrics(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String strictCodeStatsPath, final String codeStatsPath, final String experimentalFolderName) {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket));
        LOGGER.info(codeMetrics.getMicroStatsAsString());
        codeMetrics.writeStats(strictCodeStatsPath);
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(experimentalFolderName, codeStatsPath);
        return strictCodeStatsPath;
    }

    protected static void runRscript(final String pathToRScript, final String dataPath, final String reportsPath, final String imageName) throws IOException {

        if (!isRinstalled()) { return; }

        String imageOutputPath = reportsPath + imageName + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath + " " + imageOutputPath;
        LOGGER.info(Utils.executeCommand(command));
    }

    protected static boolean isRinstalled() {

        final String pathToScript = Utils.class.getResource("/scripts/checkScript.sh").getFile();
        String checkSystemForR = "sh " + pathToScript + " RScript";
        final String executeCommand = Utils.executeCommand(checkSystemForR);
        LOGGER.info(executeCommand);

        if (executeCommand.equals("RScript required but it's not installed.  Aborting.\n")) {
            LOGGER.error("Stats not generated. R or RScript is not installed.");
            System.exit(2);
            return false;
        }
        return true;
    }

    protected static Timer initAndStartTimer() {

        Timer timer = new Timer();
        timer.start();
        return timer;
    }

    protected static ClassifierTrainer train(final Bucket trainingBucket, final String experimentalFolderName) throws Exception {

        ClassifierTrainer trainer = new ClassifierTrainer(trainingBucket, experimentalFolderName);
        trainer.trainExactMatchClassifier();
        trainer.trainOLRClassifier();
        return trainer;
    }

    protected static ClassifierTrainer getExistingModels(final String modelLocations, final Bucket trainingBucket, final String experimentalFolderName) {

        ClassifierTrainer trainer = new ClassifierTrainer(trainingBucket, experimentalFolderName);
        trainer.getExistingsModels(modelLocations);

        return trainer;
    }

    protected static ClassificationHolder classify(final Bucket trainingBucket, final Bucket predictionBucket, final ClassifierTrainer trainer) throws IOException {

        ExactMatchPipeline exactMatchPipeline = new ExactMatchPipeline(trainer.getExactMatchClassifier());
        MachineLearningClassificationPipeline machineLearningClassifier = new MachineLearningClassificationPipeline(trainer.getOlrClassifier(), trainingBucket);

        ClassificationHolder classifier = new ClassificationHolder(exactMatchPipeline, machineLearningClassifier);
        classifier.classify(predictionBucket);
        return classifier;
    }

    protected static void printStatusUpdate() {

        LOGGER.info("********** Training Classifiers **********");
        LOGGER.info("Training with a dictionary size of: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numFeatures"));
        LOGGER.info("Training with this number of output classes: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numCategories"));
        LOGGER.info("Codes that were null and weren't adter chopping: " + CodeFactory.getInstance().getCodeMapNullCounter());
    }

    protected static boolean checkFileType(final File inputFile) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
        String line = br.readLine();
        br.close();
        final int expectedLineLength = 38;
        if (line != null && line.split(Utils.getCSVComma()).length == expectedLineLength) { return true; }
        return false;
    }

    protected static void writeRecords(final Bucket classifiedBucket, final String experimentalFolderName) throws IOException {

        final String nrsReportPath = "/Data/NRSData.txt";
        final DataClerkingWriter writer = new DataClerkingWriter(new File(experimentalFolderName + nrsReportPath));
        for (final Record record : classifiedBucket) {
            writer.write(record);
        }
        writer.close();

        final String comparisonReportPath = "/Data/comaprison.txt";
        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(new File(experimentalFolderName + comparisonReportPath), "\t");
        for (final Record record : classifiedBucket) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    protected static void generateAndPrintStatistics(final ClassificationHolder classifier, final String experimentalFolderName) throws IOException {

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(classifier.getAllClassified());

        PipelineUtils.generateAndPrintStats(classifier.getAllClassified(), "All Records", "AllRecords", experimentalFolderName);

        PipelineUtils.generateAndPrintStats(uniqueRecordsOnly, "Unique Only", "UniqueOnly", experimentalFolderName);
    }

    protected static String setupExperimentalFolders(final String baseFolder) {

        final String experimentalFolderName = Utils.getExperimentalFolderName(baseFolder, "Experiment");

        if (!(new File(experimentalFolderName).mkdirs() && new File(experimentalFolderName + "/Reports").mkdirs() && new File(experimentalFolderName + "/Data").mkdirs() && new File(experimentalFolderName + "/Models").mkdirs())) { throw new FolderCreationException(
                        "couldn't create experimental folder"); }

        return experimentalFolderName;
    }

    protected static void exitIfDoesNotExist(final File file) {

        if (!file.exists()) {
            System.err.println(file.getAbsolutePath() + " does not exsist. Exiting");
            System.exit(1);
        }

    }

}
