package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.closestmatchmap;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class SimilaratorComapatorTest {

    Similaritor<String> s;

    @Before
    public void setUp() throws Exception {

        SimilarityMetricFromSimmetricFactory factory = new SimilarityMetricFromSimmetricFactory();
        SimilarityMetric<String> metric = factory.create(new Levenshtein());
        s = new Similaritor<>(metric);

    }

    @Test
    public void testShortStrings() {

        String string = "foo";
        String o1 = "foo";
        String o2 = "bar";

        Comparator<String> c = s.getComparator(string);

        testAllVariationsShort(o1, o2, c);

    }

    @Test
    public void testLongStrings() {

        String string = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        String o1 = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        String o2 = "Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        Comparator<String> c = s.getComparator(string);
        testAllVariationsLong(o1, o2, c);

    }

    private void testAllVariationsShort(final String o1, final String o2, Comparator<String> c) {

        Assert.assertEquals(-1, c.compare(o1, o2));
        Assert.assertEquals(1, c.compare(o2, o1));
        Assert.assertEquals(0, c.compare(o1, o1));
        Assert.assertEquals(0, c.compare(o2, o2));
        Assert.assertEquals(0, c.compare("", o2));
        Assert.assertEquals(1, c.compare("", o1));
        Assert.assertEquals(-1, c.compare(o1, ""));
        Assert.assertEquals(0, c.compare(o2, ""));
        Assert.assertEquals(0, c.compare("", ""));
    }

    private void testAllVariationsLong(final String o1, final String o2, Comparator<String> c) {

        Assert.assertEquals(-1, c.compare(o1, o2));
        Assert.assertEquals(1, c.compare(o2, o1));
        Assert.assertEquals(0, c.compare(o1, o1));
        Assert.assertEquals(0, c.compare(o2, o2));
        Assert.assertEquals(1, c.compare("", o2));
        Assert.assertEquals(1, c.compare("", o1));
        Assert.assertEquals(-1, c.compare(o1, ""));
        Assert.assertEquals(-1, c.compare(o2, ""));
        Assert.assertEquals(0, c.compare("", ""));
    }

}
