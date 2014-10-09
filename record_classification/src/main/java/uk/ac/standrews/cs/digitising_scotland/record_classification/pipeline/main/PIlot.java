package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.LengthWeightedLossFunction;
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

        ClassifierTrainer trainer = PipelineUtils.train(trainingBucket, experimentalFolderName, codeIndex);
        boolean multipleClassifications = true;

        IPipeline exactMatchPipeline = new ExactMatchPipeline(trainer.getExactMatchClassifier());
        IPipeline machineLearningClassifier = new ClassifierPipeline(trainer.getOlrClassifier(), trainingBucket,new LengthWeightedLossFunction(), multipleClassifications, true);

        Bucket notExactMatched = exactMatchPipeline.classify(predictionBucket);
        Bucket notMachineLearned = machineLearningClassifier.classify(notExactMatched);
        LOGGER.info("Exact Matched Bucket Size: " + exactMatchPipeline.getSuccessfullyClassified().size());
        LOGGER.info("Machine Learned Bucket Size: " + machineLearningClassifier.getSuccessfullyClassified().size());
        LOGGER.info("Not Classifed Bucket Size: " + notMachineLearned.size());

        Bucket allClassifed = BucketUtils.getUnion(exactMatchPipeline.getSuccessfullyClassified(), machineLearningClassifier.getSuccessfullyClassified());

        PipelineUtils.writeRecords(allClassifed, experimentalFolderName, "MachineLearning");

        PipelineUtils.generateAndPrintStatistics(allClassifed, codeIndex, experimentalFolderName, "MachineLearning");

        timer.stop();

        return allClassifed;
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
