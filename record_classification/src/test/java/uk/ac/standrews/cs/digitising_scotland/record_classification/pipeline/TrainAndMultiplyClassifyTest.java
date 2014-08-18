package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

public class TrainAndMultiplyClassifyTest {

    @Test
    public void test() {

        File experiment0 = new File("target/Experiment0");
        File experiment1 = new File("target/Experiment1");
        System.out.println(experiment0.getAbsolutePath());
        if (!experiment0.mkdirs()) {
            System.err.println("Could not create experiment0 Folder");
        }
        if (!experiment1.mkdirs()) {
            System.err.println("Could not create experiment1 Folder");
        }
        final String experimentalFolderName = Utils.getExperimentalFolderName("target", "Experiment");
        Assert.assertEquals("target/Experiment2", experimentalFolderName);
        experiment0.delete();
        experiment1.delete();
    }

}
