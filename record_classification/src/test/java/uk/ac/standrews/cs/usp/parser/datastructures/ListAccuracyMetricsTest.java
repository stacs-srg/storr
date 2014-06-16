package uk.ac.standrews.cs.usp.parser.datastructures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.usp.parser.classifiers.ClassifierTestingHelper;

/**
 * The Class ListAccuracyMetricsTest.
 */
public class ListAccuracyMetricsTest {

    Bucket trainingBucket;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        ClassifierTestingHelper cth = new ClassifierTestingHelper();
        trainingBucket = cth.getTrainingBucket("/accuracyMetricsCoDtest.txt");
    }

    /**
     * Test unqiue record counting.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUnqiueRecords() throws Exception {

        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        int uniqueRecords = lam.getUniqueRecords();
        Assert.assertEquals(4, uniqueRecords);
    }

    /**
     * Test total aggregated records.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTotalAggregatedRecords() throws Exception {

        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        int totalRecords = lam.getTotalAggregatedRecords();
        Assert.assertEquals(6, totalRecords);
    }

}
