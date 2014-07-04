package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassificationPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.RecordFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor.DataCleaning;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class is the entry class to train all the classifiers.
 * All the machine learning methods will be trained on the file specified in the program
 * arguments. These models will then be written to disk for use in classification.
 *
 * @author jkc25
 */
public class TrainAndClassify {

    /**
     * The training file.
     */
    private String trainingFile;

    /**
     * The properties.
     */
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD")
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /**
     * The unclassified file.
     */
    private String unclassifiedFile;

    /**
     * Constructs a {@link TrainAndClassify} with the 'fileToTrainWith' specifying the training file.
     *
     * @param fileToTrainWith file containing training examples
     * @param fileToClassify  file contaning unclassifed records
     */
    public TrainAndClassify(final String fileToTrainWith, final String fileToClassify) {

        populateTrainingFile(fileToTrainWith);
        populateFileToClassify(fileToClassify);
    }

    /**
     * Constructs a {@link TrainAndClassify} with the 'fileToTrainWith' specifying the training file.
     *
     * @param fileToTrainWith      file containing training examples
     * @param fileToClassify       the file to classify
     * @param customPropertiesFile custom .properties file with custom parameters
     */
    public TrainAndClassify(final String fileToTrainWith, final String fileToClassify, final String customPropertiesFile) {

        this(fileToTrainWith, fileToClassify);
        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
        properties = mlc.extendDefaultProperties(customPropertiesFile);
    }

    /**
     * Trains all classifiers.
     * Classifiers are added to a List and the list iterated through to train each one.
     *
     * @return the array list
     * @throws Exception the exception
     */
    private ArrayList<AbstractClassifier> trainClassifiers() throws Exception {

        TrainClassifiers trainer = new TrainClassifiers();
        Bucket bucket = trainer.createCleanedBucketFromFile(trainingFile);
        VectorFactory vectorFactory = new VectorFactory(bucket);
        return trainer.trainClassifiers(bucket, vectorFactory);
    }

    /**
     * Classify bucket.
     *
     * @param inputFile   the input file
     * @param classifiers the classifiers
     * @return the bucket
     * @throws IOException          Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    private Bucket classifyBucket(final String inputFile, final ArrayList<AbstractClassifier> classifiers) throws IOException, InputFormatException {

        ClassificationPipeline pipeLine = new ClassificationPipeline(classifiers);

        Bucket toClassify = new Bucket(RecordFactory.makeUnCodedRecordsFromFile(new File(inputFile)));
        DataCleaning.cleanData(toClassify);
        //        toClassify.generateVectors(toClassify);

        pipeLine.classifyBucket(toClassify);

        Utils.writeBucketToFileNrsFormat(toClassify);

        return toClassify;
    }

    /**
     * Populate training file.
     *
     * @param fileToTrainWith the file to train with
     */
    private void populateTrainingFile(final String fileToTrainWith) {

        File trainingFile = new File(fileToTrainWith);
        if (trainingFile.exists()) {
            this.trainingFile = trainingFile.getPath();
        }
        else {
            System.err.println(trainingFile.getAbsolutePath() + " does not exsist...\n Exiting the program");
            throw new RuntimeException(trainingFile.getAbsolutePath() + " does not exsist...\n Exiting the program");
        }
    }

    /**
     * Populate file to classify.
     *
     * @param fileToClassify the file to classify
     */
    private void populateFileToClassify(final String fileToClassify) {

        File unclassifiedFile = new File(fileToClassify);

        if (unclassifiedFile.exists()) {
            this.unclassifiedFile = unclassifiedFile.getPath();
        }
        else {
            System.err.println(unclassifiedFile + " does not exsist...\n Exiting the program");
            throw new RuntimeException(unclassifiedFile + " does not exsist...\n Exiting the program");
        }
    }

    /**
     * Train classifiers and classify records.
     *
     * @return Classified Bucket
     */
    public Bucket trainAndClassify() {

        try {

            ArrayList<AbstractClassifier> classifiers = trainClassifiers();
            return classifyBucket(unclassifiedFile, classifiers);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Program entry point. Trains all models and writes to disk.
     * Models are written to default locations which are specified in machineLearning.default.properties.
     * Custom locations can be given by creating a custom properties file.
     *
     * @param args training file
     */
    public static void main(final String[] args) {

        int i = 0;
        String arg;
        String customPropertiesFile = "";
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            if (arg.equals("-properties")) {
                if (i < args.length) {
                    customPropertiesFile = args[i++];
                }
                else {
                    System.err.println("-output requires a filename");
                }
            }

        }

        if (i == args.length) {
            System.err.println("Usage: TrainClassifiers [-properties customProperties] trainingFile unclassifiedFile");
        }
        else {
            System.out.println("Training Classifiers...");
            String trainingFile = args[args.length - 2];
            String unclassifiedFile = args[args.length - 1];

            TrainAndClassify training = new TrainAndClassify(trainingFile, unclassifiedFile);

            if (customPropertiesFile.length() != 0) {
                training = new TrainAndClassify(trainingFile, unclassifiedFile, customPropertiesFile);
            }

            training.trainAndClassify();

        }
    }
}
