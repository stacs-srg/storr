package uk.ac.standrews.cs.digitising_scotland.record_classification.legacy.lda;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TopicModelTest {

    @After
    public void tearDown() {

        FileUtils.deleteQuietly(new File("target/LDAExecutionTimes.txt"));
        FileUtils.deleteQuietly(new File("target/ldaTagged.txt"));
        FileUtils.deleteQuietly(new File("target/ldaModel.model"));

    }

    @Test
    public void test() {

        TopicModel tm = new TopicModel();
        File input = new File(getClass().getResource("/ldaTest.txt").getFile());
        tm.process(input);
        Assert.assertTrue(new File("target/ldaTagged.txt").exists());
    }
}
