package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.runners.Parameterized.*;

/**
 *
 * Created by fraserdunlop on 02/10/2014 at 12:07.
 */
@RunWith(Parameterized.class)
public class SimilaritorTest {

    private String string;
    private Similaritor<String> similaritor;
    private List<String> strings = Arrays.asList(
            "boom", "bang",
            "shake", "sheek",
            "so long and", "thanks for all the fish",
            "Einstein", "Schrodinger",
            "cat in a ", "box",
            "off she ", "hops",
            "she", "jumps from the box"
    );
    private StringLengthSimilarityMetric metric = new StringLengthSimilarityMetric();

    public SimilaritorTest(String string){
        this.string = string;
    }

    @Parameters
    public static Collection<String[]> strings() {
        return Arrays.asList(new String[][]{
                {"one"},
                {"two"},
                {"three"},
                {"four"},
                {"five"},
                {"six"},
                {"seven"}
        });
    }

    @Before
    public void setup(){
        similaritor = new Similaritor<>(new StringLengthSimilarityMetric());
        Collections.shuffle(strings);
    }

    @Test
    public void runSuite(){
        testSuite(string);
    }

    private void testSuite(String string) {
        Collections.sort(strings,similaritor.getComparator(string));
        testListSortedCorrectly(string);
    }

    private void testListSortedCorrectly(String string) {
        for(int i = 0 ; i < strings.size() ; i++)
            assertSubsequentEntriesAreLessOrEquallySimilarToString(i, string);
    }

    private void assertSubsequentEntriesAreLessOrEquallySimilarToString(final int i, String string) {
        for(int j = i + 1 ; j < strings.size() ; j++ ) {
            Assert.assertTrue(metric.getSimilarity(strings.get(i), string) >= metric.getSimilarity(strings.get(j), string));
            Assert.assertTrue(Math.abs(strings.get(i).length()-string.length()) <= Math.abs(strings.get(j).length()-string.length()));
        }
    }
}
