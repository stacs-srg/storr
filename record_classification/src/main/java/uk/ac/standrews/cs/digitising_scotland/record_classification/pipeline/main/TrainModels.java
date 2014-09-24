package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.GoldStandardBucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;

/**
 * Trains a model and a lookup table from the supplied gold standard data file.
 * The models are then written to disk.
 * @author jkc25
 *
 */
public class TrainModels {

    private TrainModels() {

    }

    /**
     * Entry method for training a model on a batch of records.
     * 
     * @param args
     *            <file1> training file
     * @throws Exception
     *             If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        String experimentalFolderName;
        File goldStandard;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        goldStandard = parseGoldStandFile(args);

        File codeDictionaryFile = null; //FIXME
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        GoldStandardBucketGenerator generator = new GoldStandardBucketGenerator(codeDictionary);
        Bucket allRecords = generator.generate(goldStandard);

        PipelineUtils.printStatusUpdate();

        CodeIndexer codeIndex = new CodeIndexer(allRecords);
        PipelineUtils.train(allRecords, experimentalFolderName, codeIndex);

        timer.stop();

    }

    private static File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 2) {
            System.err.println("usage: $" + TrainClassifyOneFile.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);

        }
        return goldStandard;
    }

}
