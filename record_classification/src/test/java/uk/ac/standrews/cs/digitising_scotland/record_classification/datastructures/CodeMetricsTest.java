package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.StrictConfusionMatrix;

/**
 * The Class CodeMetricsTest.
 */
public class CodeMetricsTest {

    /**
     * Test.
     */
    @Test
    public void test() {

        Bucket testingBucket = new Bucket();
        CodeMetrics metrics = new CodeMetrics(new StrictConfusionMatrix(testingBucket));
        metrics.getHitGoldStandard();
        metrics.getIncorretPredictions();
        metrics.getMissedGoldStandard();
    }
}
