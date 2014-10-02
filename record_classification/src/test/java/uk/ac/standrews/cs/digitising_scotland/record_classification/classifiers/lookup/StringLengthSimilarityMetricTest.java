package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.runners.Parameterized.Parameters;

/**
 * Testing the basic string similarity metric.
 * Created by fraserdunlop on 02/10/2014 at 10:09.
 */
@RunWith(Parameterized.class)
public class StringLengthSimilarityMetricTest {


    private final String o1;
    private final String o2;
    private StringLengthSimilarityMetric stringLengthSimilarity;

    public StringLengthSimilarityMetricTest(String o1, String o2){
        stringLengthSimilarity = new StringLengthSimilarityMetric();
        this.o1 = o1;
        this.o2 = o2;
    }

    @Parameters
    public static Collection<String[]> strings() {
        return Arrays.asList(new String[][]{
                {"boom", "bang"},
                {"shake", "sheek"},
                {"so long and", "thanks for all the fish"},
                {"Einstein", "Schrodinger"},
                {"cat in a ", "box"},
                {"off she ", "hops"},
                {"she", "jumps from the box"}
        });
    }

    @Test
    public void runSuite(){
        testSuite_AllMethods(o1,o2);
    }

    private void testSuite_AllMethods(String o1,String o2){
        testSuite_getLengthDiff(o1,o2);
        testSuite_getSimilarity(o1,o2);
    }

    /**Tests for StringSimilarityMetric.getSimilarity(String o1, String o2) */
    private void testSuite_getSimilarity(String o1, String o2){
        assertCommutativityOf_LengthMetric_getSimilarity(o1,o2);
        assertCorrectnessOf_getSimilarity(o1,o2);
    }

    private void assertCommutativityOf_LengthMetric_getSimilarity(String o1, String o2) {
        Assert.assertTrue(stringLengthSimilarity.getSimilarity(o1, o2) == stringLengthSimilarity.getSimilarity(o2, o1));
    }

    private void assertCorrectnessOf_getSimilarity(String o1, String o2){
        assert_SameLengthRtnOne_DiffLengthNotRtnOne(o1, o2, stringLengthSimilarity);
        assert_RtnValAlwaysInZeroOneInterval(o1, o2, stringLengthSimilarity);
    }

    private void assert_RtnValAlwaysInZeroOneInterval(String o1, String o2, StringLengthSimilarityMetric stringLengthSimilarity) {
        Assert.assertEquals(0.5, stringLengthSimilarity.getSimilarity(o1, o2), 0.50001);
    }

    private void assert_SameLengthRtnOne_DiffLengthNotRtnOne(String o1, String o2, StringLengthSimilarityMetric stringLengthSimilarity) {
        if(o1.length() == o2.length())
            Assert.assertEquals(1, stringLengthSimilarity.getSimilarity(o1, o2), 0.0001);
        else Assert.assertNotEquals(1,stringLengthSimilarity.getSimilarity(o1,o2),0.0001);
    }

    /**Tests for StringSimilarityMetric.getLengthDiff(String o1, String o2) */
    private void testSuite_getLengthDiff(String o1, String o2){
        assertCommutativityOfLengthMetric_getLengthDiff(o1,o2);
        assertCorrectnessOf_LengthMetric_getLengthDiff(o1,o2);
    }

    private void assertCorrectnessOf_LengthMetric_getLengthDiff(String o1, String o2) {
        Assert.assertEquals(Math.abs(o1.length() - o2.length()), stringLengthSimilarity.getLengthDiff(o1, o2));
    }

    private void assertCommutativityOfLengthMetric_getLengthDiff(String o1, String o2) {
        Assert.assertTrue(stringLengthSimilarity.getLengthDiff(o1, o2) == stringLengthSimilarity.getLengthDiff(o2, o1));
    }


}
