package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class TrainAndMultiplyClassifyTest {

    @Test
    public void test() {

        File experiment0 = new File("Target/Experiment0");
        File experiment1 = new File("Target/Experiment1");
        if (!experiment0.mkdirs()) {
            System.err.println("Could not create experiment0 Folder");
        }
        if (!experiment1.mkdirs()) {
            System.err.println("Could not create experiment1 Folder");
        }
        Assert.assertEquals("Experiment2", TrainAndMultiplyClassify.getExperimentalFolderName("Target/Experiments"));
    }

}
