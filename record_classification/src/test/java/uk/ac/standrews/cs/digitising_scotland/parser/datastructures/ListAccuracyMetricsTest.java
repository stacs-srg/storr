package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.ListAccuracyMetrics;

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
