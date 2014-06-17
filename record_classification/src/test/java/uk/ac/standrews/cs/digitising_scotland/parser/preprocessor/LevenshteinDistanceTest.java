package uk.ac.standrews.cs.digitising_scotland.parser.preprocessor;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.preprocessor.LevenshteinDistance;

public class LevenshteinDistanceTest {

    @Test
    public void testDistanceOf4SmallFirst() {

        String testString1 = "This is a test String";
        String testString2 = "This is not a test String";

        int distance = LevenshteinDistance.computeEditDistance(testString1, testString2);
        Assert.assertEquals(4, distance);
    }

    @Test
    public void testDistanceOf4BigFirst() {

        String testString2 = "This is a test String";
        String testString1 = "This is not a test String";

        int distance = LevenshteinDistance.computeEditDistance(testString1, testString2);
        Assert.assertEquals(4, distance);
    }

    @Test
    public void testDistanceEqual() {

        String testString1 = "This is a test String";
        String testString2 = "This is a test String";

        int distance = LevenshteinDistance.computeEditDistance(testString1, testString2);
        Assert.assertEquals(0, distance);
    }

    @Test
    public void testSimilarityEqual() {

        String testString1 = "This is a test String";
        String testString2 = "This is a test String";

        double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(1.00, distance, 0.0001);
    }

    @Test
    public void testSimilarityNotEqual() {

        String testString1 = "1234";
        String testString2 = "123";

        double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(0.75, distance, 0.0001);
    }

}
