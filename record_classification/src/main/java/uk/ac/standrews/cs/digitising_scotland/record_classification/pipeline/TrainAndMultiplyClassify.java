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
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.FormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

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
 *<br><br>
 * After the records have been created and stored in a bucket, classification can begin. This is carried out by the
 * {@link BucketClassifier} class which in turn implements the {@link RecordClassificationPipeline}. Please see this class for
 * implementation details.
 * <br><br>
 * Some initial metrics are then printed to the console and classified records are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 *
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

        setupExperimentalFolders();

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

        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket);

        System.out.println("********** **********");
        System.out.println(classifiedBucket);
        accuracyMetrics.prettyPrint();
        final String dataPath = experimentalFolderName + "/Data/codeStats.csv";
        accuracyMetrics.writeStats(bucket, dataPath);

        runRscript(dataPath);

    }

    private static void runRscript(final String dataPath) throws IOException {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String imageOutputPath = experimentalFolderName + "/Reports/graph.png";
        String command = "Rscript src/R/CodeStatsPlotter.R " + dataPath + " " + imageOutputPath;
        System.out.println("Running: " + command);
        Process proc = Runtime.getRuntime().exec(command);
        try {
            int exitVal = proc.waitFor();
            System.out.println("RScript exitValue: " + exitVal);
        }
        catch (InterruptedException e) {
            System.out.println(proc.getErrorStream());
            e.printStackTrace();
        }
    }

    private static ExactMatchClassifier trainExactMatchClassifier() throws Exception {

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        return exactMatchClassifier;
    }

    private static void setupExperimentalFolders() {

        experimentalFolderName = getExperimentalFolderName();

        String[] paths = {"/Reports", "/Data", "/Models"};
        for (String string : paths) {
            final String pathname = experimentalFolderName + string;

            if (!new File(pathname).mkdirs()) {
                System.err.println("Problem creating output folder: " + pathname);

            }
        }

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
            if (Math.random() < 0.8) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InputFormatException e) {
            // TODO Auto-generated catch block
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

    protected static String getExperimentalFolderName() {

        //all experimental data stored in folder called experimentX, where X is an integer.
        int highestFolderCount = 0;
        File[] allFiles = new File(".").listFiles();
        for (File file : allFiles) {
            if (file.isDirectory() && file.getName().contains("Experiment")) {

                int currentFolder = Integer.parseInt(file.getName().subSequence(10, file.getName().length()).toString());
                if (currentFolder > highestFolderCount) {
                    highestFolderCount = currentFolder;
                }
            }
        }
        highestFolderCount++;
        return "Experiment" + highestFolderCount;
    }
}
