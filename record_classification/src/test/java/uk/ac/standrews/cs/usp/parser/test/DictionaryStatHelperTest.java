package uk.ac.standrews.cs.usp.parser.test;

import java.io.File;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.usp.tools.analysis.DictionaryStatHelper;

/**
 * The Class DictionaryStatHelperTest.
 */
public class DictionaryStatHelperTest {

    /** The dsh. */
    private DictionaryStatHelper dsh;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        File dictionary1 = new File(getClass().getResource("/testDict1.txt").getFile());
        File dictionary2 = new File(getClass().getResource("/testDict2.txt").getFile());

        dsh = new DictionaryStatHelper(dictionary1, dictionary2);

    }

    /**
     * Test get total words.
     */
    @Test
    public void testGetTotalWords() {

        Assert.assertEquals(31, dsh.getTotalWords());
    }

    /**
     * Test get unique words original.
     */
    @Test
    public void testGetUniqueWordsOriginal() {

        Assert.assertEquals(21, dsh.getUniqueWordsOriginal());
    }

    /**
     * Test get unique words corrected.
     */
    @Test
    public void testGetUniqueWordsCorrected() {

        Assert.assertEquals(14, dsh.getUniqueWordsCorrected());
    }

    /**
     * Test get number of corrections.
     */
    @Test
    public void testGetNumberOfCorrections() {

        Assert.assertEquals(8, dsh.getNumberOfCorrections());

    }

    /**
     * Test get number of lines corrected.
     */
    @Test
    public void testGetNumberOfLinesCorrected() {

        Assert.assertEquals(5, dsh.getNumberOfLinesCorrected());
    }

    /**
     * Test get percentage of words corrected.
     */
    @Test
    public void testGetPercentageOfWordsCorrected() {

        Assert.assertEquals(33.33, dsh.getPercentageOfWordsCorrected(), 0.01);
    }

    /**
     * Test get map of most common words.
     */
    @Test
    public void testGetMapOfMostCommonWords() {

        String[] stringInput = {"three", "three", "three", "two", "two", "one"};
        HashMap<String, Integer> mostCommonWords = dsh.getMapOfMostCommonWords(stringInput);
        int three = mostCommonWords.get("three");
        int two = mostCommonWords.get("two");
        int one = mostCommonWords.get("one");

        Assert.assertEquals(three, 3);
        Assert.assertEquals(two, 2);
        Assert.assertEquals(one, 1);
        Assert.assertEquals(mostCommonWords.values().toArray()[0], 1);
        Assert.assertEquals(mostCommonWords.values().toArray()[1], 2);
        Assert.assertEquals(mostCommonWords.values().toArray()[2], 3);

    }
}
