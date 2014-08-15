package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.PilotDataFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.AbstractConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.InvertedSoftConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.FolderCreationException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
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
 * turn implements the {@link MachineLearningClassificationPipeline}. Please see this
 * class for implementation details. <br>
 * <br>
 * Some initial metrics are then printed to the console and classified records
 * are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 */
public final class TrainAndMultiplyClassify {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainAndMultiplyClassify.class);

    private static AbstractFormatConverter trainingFormatConverter = new LongFormatConverter();
    private static AbstractFormatConverter classificationFormatConverter = new PilotDataFormatConverter();

    private static double trainingRatio = 0.8;

    private static VectorFactory vectorFactory;
    private static String experimentalFolderName;

    private TrainAndMultiplyClassify() {

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

        // TODO split this up!
        Timer timer = initAndStartTimer();

        //        if (args.length > 1 && args[1] != null) {
        //            double userRatio = Double.valueOf(args[1]);
        //            if (userRatio > 0 && userRatio < 1) {
        //                trainingRatio = userRatio;
        //            }
        //        }

        setupExperimentalFolders("Experiments");

        File training = new File(args[0]);
        File prediction = new File(args[1]);

        LOGGER.info("********** Generating Training Bucket **********");
        Bucket trainingRecords = createBucketTrainingRecords(training);
        Bucket classificationRecords = createPredictionBucket(prediction);

        generateActualCodeMappings(trainingRecords);

        //randomlyAssignToTrainingAndPrediction(bucket);

        vectorFactory = new VectorFactory(trainingRecords);

        printStatusUpdate();

        LOGGER.info("********** Training OLR Classifiers **********");
        AbstractClassifier classifier = trainOLRClassifier(trainingRecords, vectorFactory);

        LOGGER.info("********** Creating Lookup Tables **********");
        ExactMatchClassifier exactMatchClassifier = trainExactMatchClassifier(trainingRecords);

        // Bucket predicitionBucket = createPredictionBucket(prediction);

        LOGGER.info("********** Classifying Bucket **********");
        ExactMatchPipeline exactMatchPipeline = new ExactMatchPipeline(exactMatchClassifier);
        MachineLearningClassificationPipeline machineLearningClassifier = new MachineLearningClassificationPipeline(classifier, classificationRecords);

        Bucket exactMatched = exactMatchPipeline.classify(classificationRecords);
        Bucket notExactMatched = BucketUtils.getComplement(classificationRecords, exactMatched);
        Bucket machineLearned = machineLearningClassifier.classify(notExactMatched);
        Bucket allClassified = BucketUtils.getUnion(machineLearned, exactMatched);

        LOGGER.info("Exact Matched Bucket Size: " + exactMatched.size());
        LOGGER.info("Machine Learned Bucket Size: " + machineLearned.size());

        //  Bucket classifiedBucket = bucketClassifier.classify(predictionBucket);

        writeRecords(allClassified);

        LOGGER.info("********** Output Stats **********");

        LOGGER.info("All Records");
        generateAndPrintStats(allClassified, "All Records");

        LOGGER.info("\nUnique Records");
        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(allClassified);
        generateAndPrintStats(uniqueRecordsOnly, "Unique Only");

        LOGGER.info("Codes that were null and weren't adter chopping: " + CodeFactory.getInstance().getCodeMapNullCounter());

        timer.stop();

        LOGGER.info("Elapsed Time: " + timer.elapsedTime());

    }

    private static Timer initAndStartTimer() {

        Timer timer = new Timer();
        timer.start();
        return timer;
    }

    private static void printStatusUpdate() {

        LOGGER.info("********** Training Classifiers **********");
        LOGGER.info("Training with a dictionary size of: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numFeatures"));
        LOGGER.info("Training with this number of output classes: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numCategories"));
        LOGGER.info("Codes that were null and weren't adter chopping: " + CodeFactory.getInstance().getCodeMapNullCounter());
    }

    private static void generateActualCodeMappings(final Bucket bucket) {

        HashMap<String, Integer> codeMapping = new HashMap<>();
        for (Record record : bucket) {
            for (CodeTriple currentCodeTriple : record.getGoldStandardClassificationSet()) {
                codeMapping.put(currentCodeTriple.getCode().getCodeAsString(), 1);
            }
        }

        StringBuilder sb = new StringBuilder();
        Set<String> keySet = codeMapping.keySet();

        for (String key : keySet) {
            sb.append(key + "\t" + key + "\n");
        }
        //    sb.append("\n");
        File codeFile = new File("target/customCodeMap.txt");
        Utils.writeToFile(sb.toString(), codeFile.getAbsolutePath());
        CodeFactory.getInstance().loadDictionary(codeFile);
    }

    private static void generateAndPrintStats(final Bucket classifiedBucket, String header) throws IOException {

        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket);
        accuracyMetrics.prettyPrint(header);
        generateStats(classifiedBucket, accuracyMetrics);
    }

    private static void generateStats(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics) throws IOException {

        final String matrixDataPath = experimentalFolderName + "/Data/classificationCountMatrix.csv";
        final String matrixImagePath = "classificationMatrix";
        final String reportspath = experimentalFolderName + "/Reports/";

        final String strictCodeStatsPath = experimentalFolderName + "/Data/strictCodeStats.csv";
        final String strictCodePath = "strictCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, strictCodeStatsPath, strictCodePath);

        final String softCodeStatsPath = experimentalFolderName + "/Data/softCodeStats.csv";
        final String softCodePath = "softCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, softCodeStatsPath, softCodePath);

        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(bucket);
        double totalCorrectlyPredicted = invertedConfusionMatrix.getTotalCorrectlyPredicted();
        LOGGER.info("Number of predictions too specific: " + totalCorrectlyPredicted);
        LOGGER.info("Proportion of predictions too specific: " + totalCorrectlyPredicted / invertedConfusionMatrix.getTotalPredicted());

        runRscript("src/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
        runRscript("src/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
        runRscript("src/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);

    }

    private static String printCodeMetrics(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String strictCodeStatsPath, final String codeStatsPath) {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket));
        LOGGER.info(codeMetrics.getMicroStatsAsString());
        codeMetrics.writeStats(strictCodeStatsPath);
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(experimentalFolderName, codeStatsPath);
        return strictCodeStatsPath;
    }

    private static void runRscript(final String pathToRScript, final String dataPath, final String reportsPath, final String imageName) throws IOException {

        // TODO this doesn't look too portable!

        if (!isRinstalled()) { return; }

        String imageOutputPath = reportsPath + imageName + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath + " " + imageOutputPath;
        LOGGER.info(Utils.executeCommand(command));
    }

    private static boolean isRinstalled() {

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

    private static ExactMatchClassifier trainExactMatchClassifier(Bucket trainingRecords) throws Exception {

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingRecords);
        return exactMatchClassifier;
    }

    private static void setupExperimentalFolders(final String baseFolder) {

        experimentalFolderName = getExperimentalFolderName(baseFolder);

        if (!(new File(experimentalFolderName).mkdirs() && new File(experimentalFolderName + "/Reports").mkdirs() && new File(experimentalFolderName + "/Data").mkdirs() && new File(experimentalFolderName + "/Models").mkdirs())) { throw new FolderCreationException(
                        "couldn't create experimental folder"); }
    }

    private static void writeRecords(final Bucket classifiedBucket) throws IOException {

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

    //    Commented out while testing - FIXME
    private static Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        try {
            toClassify = new Bucket(classificationFormatConverter.convert(prediction));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InputFormatException e) {
            e.printStackTrace();
        }

        return toClassify;
    }

    private static AbstractClassifier trainOLRClassifier(final Bucket bucket, final VectorFactory vectorFactory) throws Exception {

        AbstractClassifier olrClassifier = new OLRClassifier(vectorFactory);
        OLRClassifier.setModelPath(experimentalFolderName + "/Models/olrModel");
        olrClassifier.train(bucket);
        return olrClassifier;
    }

    private static Bucket createBucketTrainingRecords(final File training) throws IOException, InputFormatException {

        Bucket bucket = new Bucket();
        Iterable<Record> records;
        boolean longFormat = checkFileType(training);

        if (longFormat) {
            records = trainingFormatConverter.convert(training);
        }
        else {
            records = RecordFactory.makeCodedRecordsFromFile(training);
        }
        bucket.addCollectionOfRecords(records);

        return bucket;
    }

    private static boolean checkFileType(final File inputFile) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
        String line = br.readLine();
        br.close();
        final int expectedLineLength = 38;
        if (line != null && line.split(Utils.getCSVComma()).length == expectedLineLength) { return true; }
        return false;
    }

    protected static String getExperimentalFolderName(final String baseFolder) {

        // all experimental data stored in folder called experimentX, where X is
        // an integer.
        int highestFolderCount = 0;
        File base = new File(baseFolder);

        if (!base.exists() && !base.mkdirs()) {
            LOGGER.error("Could not create all folders in path " + base + ".\n" + base.getAbsolutePath() + " may already exsists");
        }

        File[] allFiles = base.listFiles();
        for (File file : allFiles) {
            if (file.isDirectory() && file.getName().contains("Experiment")) {

                int currentFolder = Integer.parseInt(file.getName().subSequence(10, file.getName().length()).toString());
                if (currentFolder > highestFolderCount) {
                    highestFolderCount = currentFolder;
                }
            }
        }
        highestFolderCount++;
        return baseFolder + "/Experiment" + highestFolderCount;
    }
}
