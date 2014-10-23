package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class TrainClassifyOneFileTest {

    private TrainClassifyOneFile trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testPipelineHisco() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/HiscoTitles.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new TrainClassifyOneFile();
        String training = getClass().getResource("/occupationTrainingTest.txt").getFile();
        String[] args = {training, "0.8", "false"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 10);

    }

    @Test
    public void testPipelineCod() throws Exception, CodeNotValidException {

        String codeDictionary = getClass().getResource("/pilotTestCodeDictionary.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new TrainClassifyOneFile();
        String training = getClass().getResource("/OneFileCodTestTrainingData.txt").getFile();
        String[] args = {training, "0.8", "true"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 10);

    }

    @Test
    public void testRunNoGoldStandard() throws Exception, CodeNotValidException {

        trainer = new TrainClassifyOneFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception, CodeNotValidException {

        trainer = new TrainClassifyOneFile();
        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
