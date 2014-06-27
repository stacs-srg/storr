package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.hmm;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.hmm.HMMPreparser;

/**
 * The Class HMMPreparserTests tests the basic functionality of the {@link HMMPreparser} class.
 */
public class HMMPreparserTest {

    /**
     * Test remove quotes.
     */
    @Test
    public void testRemoveQuotes() {

        HMMPreparser hmmp = new HMMPreparser();
        String testString = "Remove these \"quotes\"";
        String parsedString = hmmp.removeQuotes(testString);
        Assert.assertEquals("Remove these quotes", parsedString);
    }

    /**
     * Tests separate punctuation comma.
     */
    @Test
    public void testSeperatePuncutationComma() {

        String testString = "The quick brown fox, jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick brown fox , jumped", parsedString);
    }

    /**
     * Test separate punctuation  semi colon.
     */
    @Test
    public void testSeperatePuncutationSemiColon() {

        String testString = "The quick brown fox; jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick brown fox ; jumped", parsedString);
    }

    /**
     * Test separate punctuation c comma semi.
     */
    @Test
    public void testSeperatePuncutationCommaSemi() {

        String testString = "The quick, brown fox; jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick , brown fox ; jumped", parsedString);
    }

    /**
     * Test split into multiple lines.
     */
    @Test
    public void testSplitIntoMultipuleLines() {

        ArrayList<String> testString = new ArrayList<String>();
        testString.add("Line one");
        testString.add("line two");

        HMMPreparser hmmp = new HMMPreparser();
        hmmp.splitIntoMultipuleLines(testString);
    }

    /**
     * Test tokenise.
     */
    @Test
    public void testTokenise() {

        String testString = "The quick, brown fox jumped";

        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.tokenise(testString);
        Assert.assertEquals("The\nquick,\nbrown\nfox\njumped\n\n", parsedString);

    }

}
