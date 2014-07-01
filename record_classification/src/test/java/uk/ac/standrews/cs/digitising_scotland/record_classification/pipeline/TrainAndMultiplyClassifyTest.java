package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class TrainAndMultiplyClassifyTest {

    @Test
    public void test() {

        File experiment0 = new File("Experiment0");
        File experiment1 = new File("Experiment1");
        experiment0.mkdirs();
        experiment1.mkdirs();

        Assert.assertEquals("Experiment2", TrainAndMultiplyClassify.getExperimentalFolderName());
    }

}
