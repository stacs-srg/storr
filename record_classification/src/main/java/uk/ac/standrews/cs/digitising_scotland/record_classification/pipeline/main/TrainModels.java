package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.GoldStandardBucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Trains a model and a lookup table from the supplied gold standard data file.
 * The models are then written to disk.
 * @author jkc25
 *
 */
public class TrainModels {

    /**
     * Entry method for training a model on a batch of records.
     * 
     * @param args
     *            <file1> training file
     * @throws Exception
     *             If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        TrainModels instance = new TrainModels();
        instance.run(args);

    }

    public void run(final String[] args) throws Exception {

        Timer timer = PipelineUtils.initAndStartTimer();

        String experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        File goldStandard = parseGoldStandFile(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        GoldStandardBucketGenerator generator = new GoldStandardBucketGenerator(codeDictionary);
        Bucket allRecords = generator.generate(goldStandard);

        PipelineUtils.printStatusUpdate();

        CodeIndexer codeIndex = new CodeIndexer(allRecords);
        PipelineUtils.train(allRecords, experimentalFolderName, codeIndex);

        timer.stop();
    }

    private File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 2) {
            System.err.println("usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>    <modelLocation(optional)>");
        }
        if (args.length < 2) {
            System.err.println("usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>    <modelLocation(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);
            File modelLocation = new File(args[1]);
            PipelineUtils.exitIfDoesNotExist(modelLocation);

        }
        return goldStandard;
    }

}
