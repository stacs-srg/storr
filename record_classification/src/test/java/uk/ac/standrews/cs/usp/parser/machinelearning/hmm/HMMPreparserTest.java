package uk.ac.standrews.cs.usp.parser.machinelearning.hmm;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class HMMPreparserTest {

    @Test
    public void testRemoveQuotes() {

        HMMPreparser hmmp = new HMMPreparser();
        String testString = "Remove these \"quotes\"";
        String parsedString = hmmp.removeQuotes(testString);
        Assert.assertEquals("Remove these quotes", parsedString);
    }

    @Test
    public void testSeperatePuncutationComma() {

        String testString = "The quick brown fox, jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick brown fox , jumped", parsedString);
    }

    @Test
    public void testSeperatePuncutationSemiColon() {

        String testString = "The quick brown fox; jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick brown fox ; jumped", parsedString);
    }

    @Test
    public void testSeperatePuncutationCommaSemi() {

        String testString = "The quick, brown fox; jumped";
        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.seperatePuncutation(testString);
        Assert.assertEquals("The quick , brown fox ; jumped", parsedString);
    }

    @Test
    public void testSplitIntoMultipuleLines() {

        ArrayList<String> testString = new ArrayList<String>();
        testString.add("Line one");
        testString.add("line two");

        HMMPreparser hmmp = new HMMPreparser();
        hmmp.splitIntoMultipuleLines(testString);
    }

    @Test
    public void testTokenise() {

        String testString = "The quick, brown fox jumped";

        HMMPreparser hmmp = new HMMPreparser();
        String parsedString = hmmp.tokenise(testString);
        Assert.assertEquals("The\nquick,\nbrown\nfox\njumped\n\n", parsedString);

    }

}
