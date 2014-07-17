package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenshteinDistance;

/**
 * The Class LevenshteinDistanceTest tests the calculation of Levenshtein distances with various strings.
 */
public class LevenshteinDistanceTest {

    /**
     * Tests calculation of distance betweem two strings with distance of 4. Shorest string first.
     */
    @Test
    public void testDistanceOf4SmallFirst() {

        final String shortString = "This is a test String";
        final String longString = "This is not a test String";
        final int distance = LevenshteinDistance.computeEditDistance(shortString, longString);
        Assert.assertEquals(4, distance);
    }

    /**
     * Tests calculation of distance between two strings with distance of 4, longest string first.
     */
    @Test
    public void testDistanceOf4BigFirst() {

        final String shortString = "This is a test String";
        final String longString = "This is not a test String";
        final int distance = LevenshteinDistance.computeEditDistance(longString, shortString);
        Assert.assertEquals(4, distance);
    }

    /**
     * Tests calculation of distance between two strings with equall length string.
     */
    @Test
    public void testDistanceEqual() {

        final String testString1 = "This is a test String";
        final String testString2 = "This is a test String";
        final int distance = LevenshteinDistance.computeEditDistance(testString1, testString2);
        Assert.assertEquals(0, distance);
    }

    /**
     * Tests when similarity is equal.
     */
    @Test
    public void testSimilarityEqual() {

        final String testString1 = "This is a test String";
        final String testString2 = "This is a test String";
        final double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(1.00, distance, 0.0001);
    }

    /**
     * Tests when similarity is not equal.
     */
    @Test
    public void testSimilarityNotEqual() {

        final String testString1 = "1234";
        final String testString2 = "123";
        final double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(0.75, distance, 0.0001);
    }

}
