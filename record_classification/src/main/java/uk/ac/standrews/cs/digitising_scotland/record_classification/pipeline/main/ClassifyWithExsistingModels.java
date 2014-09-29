package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassificationHolder;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassifierTrainer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.GoldStandardBucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.MachineLearningClassificationPipeline;
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
 * turn implements the {@link MachineLearningClassificationPipeline}. Please see this
 * class for implementation details. <br>
 * <br>
 * Some initial metrics are then printed to the console and classified records
 * are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 */
public final class ClassifyWithExsistingModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyWithExsistingModels.class);

    private ClassifyWithExsistingModels() {

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

        String experimentalFolderName;
        File goldStandard;
        String modelLocation;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        goldStandard = parseGoldStandard(args);
        modelLocation = parseModelLocation(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
        GoldStandardBucketGenerator generator = new GoldStandardBucketGenerator(codeDictionary);
        Bucket allRecords = generator.generate(goldStandard);

        PipelineUtils.printStatusUpdate();

        ClassifierTrainer trainer = PipelineUtils.getExistingModels(modelLocation, allRecords, experimentalFolderName);

        ClassificationHolder classifier = PipelineUtils.classify(allRecords, allRecords, trainer);

        LOGGER.info("Exact Matched Bucket Size: " + classifier.getExactMatched().size());
        LOGGER.info("Machine Learned Bucket Size: " + classifier.getMachineLearned().size());

        PipelineUtils.writeRecords(classifier.getAllClassified(), experimentalFolderName);

        PipelineUtils.generateAndPrintStatistics(classifier, trainer.getVectorFactory().getCodeIndexer(), experimentalFolderName);

        timer.stop();

    }

    private static File parseGoldStandard(final String[] args) {

        File goldStandard = null;
        if (args.length > 2) {
            System.err.println("usage: $" + ClassifyWithExsistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);
        }
        return goldStandard;

    }

    private static String parseModelLocation(final String[] args) {

        String modelLocation = null;
        if (args.length > 2) {
            System.err.println("usage: $" + ClassifyWithExsistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            modelLocation = args[1];
            PipelineUtils.exitIfDoesNotExist(new File(modelLocation));
        }
        return modelLocation;
    }
}
