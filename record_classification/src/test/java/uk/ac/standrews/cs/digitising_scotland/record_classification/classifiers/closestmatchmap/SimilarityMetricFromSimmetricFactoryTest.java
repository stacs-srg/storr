package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

public class SimilarityMetricFromSimmetricFactoryTest {

    SimilarityMetricFromSimmetricFactory factory;

    @Before
    public void setUp() throws Exception {

        factory = new SimilarityMetricFromSimmetricFactory();
    }

    @Test
    public void testTheSameString() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(1.0, metric.getSimilarity("Foo", "Foo"), 0.0001);

    }

    @Test
    public void testOverLap() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(0.5, metric.getSimilarity("Foo Bar", "Foo"), 0.0001);

    }

    @Test
    public void testDifferntString() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(0.0, metric.getSimilarity("Foo", "Bar"), 0.0001);

    }

}
