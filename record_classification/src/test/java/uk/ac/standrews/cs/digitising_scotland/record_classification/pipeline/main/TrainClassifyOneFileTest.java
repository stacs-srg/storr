package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

public class TrainClassifyOneFileTest {

    private TrainClassifyOneFile trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        String codeDictionary = getClass().getResource("/HiscoTitles.txt").getFile();
        MachineLearningConfiguration.getDefaultProperties().setProperty("codeDictionaryFile", codeDictionary);
        trainer = new TrainClassifyOneFile();

    }

    @Test
    public void testPipeline() throws Exception {

        String training = getClass().getResource("/occupationTrainingTest.txt").getFile();
        String[] args = {training, "0.8", "false"};
        Bucket classified = trainer.run(args);
        Assert.assertEquals(20, classified.size(), 2);

    }

    @Test
    public void testRunNoGoldStandard() throws Exception {

        trainer = new TrainClassifyOneFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception {

        trainer = new TrainClassifyOneFile();
        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
