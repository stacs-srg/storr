package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.ClassifierTestingHelper;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;

/**
 * The Class ListAccuracyMetricsTest.
 */
public class ListAccuracyMetricsTest {

    private Bucket trainingBucket;
    private ClassifierTestingHelper cth;

    /**
     * Sets the up.
     * 
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {

        cth = new ClassifierTestingHelper();
        trainingBucket = cth.getTrainingBucket("/accuracyMetricsCoDtest.txt");
    }

    /**
     * Test unqiue record counting.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testUnqiueRecords() throws Exception {

        ListAccuracyMetrics lam = new ListAccuracyMetrics(BucketFilter.uniqueRecordsOnly(trainingBucket));
        int uniqueRecords = lam.getTotalRecordsInBucket();
        Assert.assertEquals(6, uniqueRecords);
    }

    /**
     * Test total aggregated records.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testTotalAggregatedRecords() throws Exception {

        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        int totalRecords = lam.getTotalRecordsInBucket();
        Assert.assertEquals(6, totalRecords);
    }

    @Test
    @Ignore("Needs to be updated to new CodeIndex/DictionaryFormat")
    //FIXME
    public void testAverageConfidence() throws URISyntaxException {

        cth.giveBucketTestingCODCodes(trainingBucket);
        ListAccuracyMetrics lam = new ListAccuracyMetrics(trainingBucket);
        double averageConfidence = lam.getAverageConfidence();
        Assert.assertEquals(1.0, averageConfidence, 0.00001);
    }
}
