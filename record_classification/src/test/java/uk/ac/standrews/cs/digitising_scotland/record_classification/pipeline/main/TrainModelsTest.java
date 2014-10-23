package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;

public class TrainModelsTest {

    private TrainClassifyOneFile trainer;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        trainer = new TrainClassifyOneFile();
    }

    @Test
    public void testRunNoGoldStandard() throws Exception, CodeNotValidException {

        expectedEx.expect(RuntimeException.class);
        String[] args = {"testFile", "modelLoc"};
        trainer.run(args);

    }

    @Test
    public void testRunNoModelLocation() throws Exception, CodeNotValidException {

        String goldStandardFile = getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile();
        expectedEx.expect(RuntimeException.class);
        String[] args = {goldStandardFile, "nonExistantModelLocation"};
        trainer.run(args);

    }

}
