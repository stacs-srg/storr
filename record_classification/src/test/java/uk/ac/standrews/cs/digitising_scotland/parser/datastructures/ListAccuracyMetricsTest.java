package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.ClassifierTestingHelper;
import java.net.URISyntaxException;

/**
 * The Class ListAccuracyMetricsTest.
 */
public class ListAccuracyMetricsTest {

    private Bucket trainingBucket;
    private ClassifierTestingHelper cth;

    @Before
    public void setUp() throws Exception {
        cth = new ClassifierTestingHelper();
        trainingBucket = cth.getTrainingBucket("/accuracyMetricsCoDtest.txt");

    }

    @Test
    public void testUnqiueRecords() throws Exception {
        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        int uniqueRecords = lam.getUniqueRecords();
        Assert.assertEquals(4, uniqueRecords);
    }

    @Test
    public void testTotalAggregatedRecords() throws Exception {
        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        int totalRecords = lam.getTotalAggregatedRecords();
        Assert.assertEquals(6, totalRecords);
    }

    @Test
    public void testAverageConfidence() throws URISyntaxException {
        cth.giveBucketTestingCODCodes(trainingBucket);
        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        double averageConfidence = lam.getAverageConfidence();
        Assert.assertEquals(1.0, averageConfidence,0.00001);
    }
}
