package uk.ac.standrews.cs.digitising_scotland.parser.lda;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.lda.TopicModel;

public class TopicModelTest {

    @Test
    public void test() {

        TopicModel tm = new TopicModel();
        File input = new File(getClass().getResource("/ldaTest.txt").getFile());
        tm.process(input);
        Assert.assertTrue(new File("target/ldaTagged.txt").exists());
    }
}
