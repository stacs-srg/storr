package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests cleaning of strings using {@link TokenizerCleaner} which internally uses Lucene {@link StandardTokenizer}.
 * @author jkc25
 * TODO write more tests
 */
public class TokenizerCleanerTest {

    /**
     * Test comma removal.
     */
    @Test
    public void testCommaRemoval() {

        String testString = "String, To, Test";
        String expected = "String To Test";
        String cleanString = TokenizerCleaner.clean(testString);
        Assert.assertEquals(expected, cleanString);
    }

    /**
     * Test tokenization and cleaning with hyphen.
     */
    @Test
    public void testHyphen() {

        String testString = "String - To Test";
        String expected = "String To Test";
        String cleanString = TokenizerCleaner.clean(testString);
        Assert.assertEquals(expected, cleanString);
    }

    /**
     * Test new line.
     */
    @Test
    public void testNewLine() {

        String testString = "String\n To Test";
        String expected = "String To Test";
        String cleanString = TokenizerCleaner.clean(testString);
        Assert.assertEquals(expected, cleanString);
    }
}
