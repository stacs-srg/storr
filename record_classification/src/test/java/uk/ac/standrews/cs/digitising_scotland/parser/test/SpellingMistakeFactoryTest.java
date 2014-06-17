package uk.ac.standrews.cs.digitising_scotland.parser.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.SpellingMistakeFactory;

/**
 * Runs tests on the SpellingMistakeFactory class.
 * @author jkc25
 *
 */
public class SpellingMistakeFactoryTest {

    /**
     * Tests to make sure that the input and output strings are different,
     * ie, that they contain a newly introduced mistake.
     */
    @Test
    public void testDifferent() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        for (int i = 0; i < 10; i++) {
            assertTrue(!spf.addMistake("spelling mistakes are easy to make").equalsIgnoreCase("spelling mistakes are easy to make"));
        }
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testTwoLetter() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        assertTrue(spf.addMistake("to").equalsIgnoreCase("ot"));
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testSpellingMistakeComplex() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        spf.addMistakeTypo("input");

        for (int i = 0; i < 10; i++) {
            assertTrue(!spf.addMistakeTypo("spelling mistakes are easy to make").equalsIgnoreCase("spelling mistakes are easy to make"));
        }
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testSpellingMistakeComplexOneLetter() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        assertTrue(!spf.addMistakeTypo("a").equalsIgnoreCase("a"));
    }
}
