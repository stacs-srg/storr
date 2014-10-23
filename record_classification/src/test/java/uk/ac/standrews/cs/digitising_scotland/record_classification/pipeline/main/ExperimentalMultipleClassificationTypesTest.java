package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class ExperimentalMultipleClassificationTypesTest {

    ExperimentalMultipleClassificationTypes trainer;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testPipelineHisco() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/HiscoTitles.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new ExperimentalMultipleClassificationTypes();
        String training = getClass().getResource("/occupationTrainingTest.txt").getFile();
        String[] args = {training, "0.8", "false"};
        trainer.run(args);

    }

    @Test
    public void testPipelineCod() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new ExperimentalMultipleClassificationTypes();
        String training = getClass().getResource("/OneFileCodTestTrainingData.txt").getFile();
        String[] args = {training, "0.8", "true"};
        trainer.run(args);

    }
}
