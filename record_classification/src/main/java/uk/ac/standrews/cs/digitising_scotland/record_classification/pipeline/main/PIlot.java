package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
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
public final class PIlot {

    private static final Logger LOGGER = LoggerFactory.getLogger(PIlot.class);

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

        PIlot instance = new PIlot();
        instance.run(args);
    }

    public Bucket run(final String[] args) throws Exception {

        String experimentalFolderName;
        File training;
        File prediction;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        File[] inputFiles = parseInput(args);
        training = inputFiles[0];
        prediction = inputFiles[1];

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        BucketGenerator generator = new BucketGenerator(codeDictionary);
        Bucket trainingBucket = generator.generateTrainingBucket(training);

        CodeIndexer codeIndex = new CodeIndexer(trainingBucket);

        Bucket predictionBucket = generator.createPredictionBucket(prediction);

        LOGGER.info("Prediction bucket contains " + predictionBucket.size() + " records");

        PipelineUtils.printStatusUpdate();
        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        OLRClassifier olrClassifier = new OLRClassifier();
        OLRClassifier.setModelPath(experimentalFolderName + "/Models/olrModel");
        olrClassifier.train(trainingBucket);

        boolean multipleClassifications = true;

        IPipeline exactMatchPipeline = new ExactMatchPipeline(exactMatchClassifier);
        IPipeline machineLearningClassifier = new ClassifierPipeline(olrClassifier, trainingBucket, new LengthWeightedLossFunction(), multipleClassifications, true);

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

    private void writeComparisonFile(final String experimentalFolderName, final Bucket allClassifed) throws IOException, FileNotFoundException, UnsupportedEncodingException {

        final String comparisonReportPath = "/Data/" + "Output" + "/comaprison.txt";
        final File outputPath2 = new File(experimentalFolderName + comparisonReportPath);
        Files.createParentDirs(outputPath2);

        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(outputPath2, "\t");
        for (final Record record : allClassifed) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    private void writeRecords(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String nrsReportPath = "/Data/" + "Output" + "/NRSData.txt";
        final File outputPath = new File(experimentalFolderName + nrsReportPath);
        Files.createParentDirs(outputPath);
        final DataClerkingWriter writer = new DataClerkingWriter(outputPath);
        for (final Record record : allClassifed) {
            writer.write(record);
        }
        writer.close();
    }

    private File[] parseInput(final String[] args) {

        //Training file in [0], prediction file in [1]
        File[] trainingPrediction = new File[2];

        if (args.length != 2) {
            System.err.println("You must supply 2 arguments");
            System.err.println("usage: $" + PIlot.class.getSimpleName() + "    <trainingFile>    <predictionFile>");
        }
        else {
            trainingPrediction[0] = new File(args[0]);
            trainingPrediction[1] = new File(args[1]);
            PipelineUtils.exitIfDoesNotExist(trainingPrediction[0]);
            PipelineUtils.exitIfDoesNotExist(trainingPrediction[1]);
        }

        return trainingPrediction;
    }

}
