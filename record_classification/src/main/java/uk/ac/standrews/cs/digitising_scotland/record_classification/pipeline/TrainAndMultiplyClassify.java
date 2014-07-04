package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.FormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.AbstractConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.InvertedSoftConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

/**
 * This class integrates the training of machine learning models and the classification of records using those models.
 * The classification process is as follows:
 * <br><br>
 * The gold standard training file is read in from the command line and a {@link Bucket} of {@link Record}s are created from this file.
 * A {@link VectorFactory} is then created to manage the creation of vectors for these records. The vectorFactory also manages
 * the mapping of vectors IDs to words, ie the vector dictionary.
 * <br><br>
 * An {@link AbstractClassifier} is then created from the training bucket and the model(s) are trained and saved to disk.
 * <br><br>
 * The records to be classified are held in a file with the correct format as specified by NRS. One record per line.
 * This class initiates the reading of these records. These are stored as {@link Record} objects inside a {@link Bucket}.
 * <br><br>
 * After the records have been created and stored in a bucket, classification can begin. This is carried out by the
 * {@link BucketClassifier} class which in turn implements the {@link RecordClassificationPipeline}. Please see this class for
 * implementation details.
 * <br><br>
 * Some initial metrics are then printed to the console and classified records are written to file (target/NRSData.txt).
 *
 * @author jkc25, frjd2
 */
public final class TrainAndMultiplyClassify {

    private static VectorFactory vectorFactory;
    private static Bucket trainingBucket;
    private static Bucket predictionBucket;
    private static String experimentalFolderName;

    private TrainAndMultiplyClassify() {

        //no public constructor
    }

    /**
     * Entry method for training and classifying a batch of records into multiple codes.
     *
     * @param args <file1> training file <file2> file to classify
     * @throws Exception If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        // TODO split this up!

        setupExperimentalFolders("Experiments");

        File training = new File(args[0]);
        // File prediction = new File(args[1]);

        System.out.println("********** Generating Training Bucket **********");

        Bucket bucket = createTrainingBucket(training);

        randomlyAssignToTrainingAndPrediction(bucket);

        vectorFactory = new VectorFactory(trainingBucket);

        System.out.println("********** Training Classifier **********");
        System.out.println("Training with a dictionary size of: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numFeatures"));
        System.out.println("Training with this number of output classes: " + MachineLearningConfiguration.getDefaultProperties().getProperty("numCategories"));

        AbstractClassifier classifier = trainOLRClassifier(trainingBucket, vectorFactory);

        ExactMatchClassifier exactMatchClassifier = trainExactMatchClassifier();

        //Bucket predicitionBucket = createPredictionBucket(prediction);

        RecordClassificationPipeline recordClassifier = new RecordClassificationPipeline(classifier, exactMatchClassifier);

        BucketClassifier bucketClassifier = new BucketClassifier(recordClassifier);

        System.out.println("********** Classifying Bucket **********");

        Bucket classifiedBucket = bucketClassifier.classify(predictionBucket);

        writeRecords(classifiedBucket);

        ListAccuracyMetrics accuracyMetricsAllRecords = new ListAccuracyMetrics(classifiedBucket);
        ListAccuracyMetrics accuracyMetricsUniqueRecords = new ListAccuracyMetrics(BucketFilter.uniqueRecordsOnly(classifiedBucket));

        System.out.println("********** **********");

        System.out.println("All Records");
        accuracyMetricsAllRecords.prettyPrint();

        generateStats(classifiedBucket, accuracyMetricsAllRecords);

        System.out.println("\nUnique Records");
        accuracyMetricsUniqueRecords.prettyPrint();
        generateStats(classifiedBucket, accuracyMetricsUniqueRecords);

    }

    private static void generateStats(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics) throws IOException {

        final String matrixDataPath = experimentalFolderName + "/Data/classificationCountMatrix.csv";
        final String matrixImagePath = "classificationMatrix";

        final String strictCodeStatsPath = experimentalFolderName + "/Data/strictCodeStats.csv";
        final String strictCodePath = "strictCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, strictCodeStatsPath, strictCodePath);

        final String softCodeStatsPath = experimentalFolderName + "/Data/softCodeStats.csv";
        final String softCodePath = "softCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, softCodeStatsPath, softCodePath);

        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(bucket);
        double totalCorrectlyPredicted = invertedConfusionMatrix.getTotalCorrectlyPredicted();
        System.out.println("Number of predictions too specific: " + totalCorrectlyPredicted);
        System.out.println("Proportion of predictions too specific: " + totalCorrectlyPredicted / invertedConfusionMatrix.getTotalPredicted());

        runRscript("src/R/CodeStatsPlotter.R", strictCodeStatsPath, strictCodePath);
        runRscript("src/R/CodeStatsPlotter.R", softCodeStatsPath, softCodePath);
        runRscript("src/R/HeatMapPlotter.R", matrixDataPath, matrixImagePath);

    }

    private static String printCodeMetrics(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String strictCodeStatsPath, final String codeStatsPath) {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket));
        codeMetrics.printMicroStats();
        codeMetrics.writeStats(strictCodeStatsPath);
        System.out.println(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(experimentalFolderName, codeStatsPath);
        return strictCodeStatsPath;
    }

    private static void runRscript(final String pathToRScript, final String dataPath, final String imageName) throws IOException {

        // TODO this doesn't look too portable!

        String imageOutputPath = experimentalFolderName + "/Reports/" + imageName + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath + " " + imageOutputPath;
        System.out.println(executeCommand(command));
    }

    private static String executeCommand(final String command) {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            int exitVal = p.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), FileManipulation.FILE_CHARSET))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
            }

            if (exitVal != 0) {
                System.out.println("ExitValue: " + exitVal);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private static ExactMatchClassifier trainExactMatchClassifier() throws Exception {

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    private static void setupExperimentalFolders(final String baseFolder) {

        experimentalFolderName = getExperimentalFolderName(baseFolder);

        if (!(new File(experimentalFolderName).mkdirs() && new File(experimentalFolderName + "/Reports").mkdirs() && new File(experimentalFolderName + "/Data").mkdirs() && new File(experimentalFolderName + "/Models").mkdirs())) { throw new RuntimeException("couldn't create experimental folder"); }
    }

    private static void writeRecords(final Bucket classifiedBucket) throws IOException {

        DataClerkingWriter writer = new DataClerkingWriter(new File(experimentalFolderName + "/Data/NRSData.txt"));
        for (Record record : classifiedBucket) {
            writer.write(record);
        }
        writer.close();
    }

    private static void randomlyAssignToTrainingAndPrediction(final Bucket bucket) {

        trainingBucket = new Bucket();
        predictionBucket = new Bucket();
        for (Record record : bucket) {
            if (Math.random() < 0.8) { // TODO Magic number
                trainingBucket.addRecordToBucket(record);
            }
            else {
                predictionBucket.addRecordToBucket(record);
            }
        }
    }

    private static Bucket createPredictionBucket(final File prediction) {

        Bucket toClassify = null;
        try {
            toClassify = new Bucket(RecordFactory.makeCodedRecordsFromFile(prediction));
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
        ((OLRClassifier) olrClassifier).setModelPath(experimentalFolderName + "/Models/olrModel");
        olrClassifier.train(bucket);
        return olrClassifier;
    }

    private static Bucket createTrainingBucket(final File training) throws IOException, InputFormatException {

        Bucket bucket = new Bucket();
        Iterable<Record> records;
        boolean longFormat = checkFileType(training);

        if (longFormat) {
            records = FormatConverter.convert(training);
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
        if (line != null) {
            if (line.split(Utils.getCSVComma()).length == 38) { return true; }
        }
        return false;
    }

    protected static String getExperimentalFolderName(final String baseFolder) {

        //all experimental data stored in folder called experimentX, where X is an integer.
        int highestFolderCount = 0;
        File[] allFiles = new File(".").listFiles();
        for (File file : allFiles) {
            if (file.isDirectory() && file.getName().contains(baseFolder + "/Experiment")) {

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
