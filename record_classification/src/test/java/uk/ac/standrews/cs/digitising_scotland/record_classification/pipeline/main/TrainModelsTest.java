package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TrainModelsTest {

    private TrainModels trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        trainer = new TrainModels();
    }

    @Test
    public void testRunNoGoldStandard() throws Exception {

        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception {

        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
