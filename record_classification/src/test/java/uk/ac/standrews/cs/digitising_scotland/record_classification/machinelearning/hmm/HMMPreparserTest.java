/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.hmm;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

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
