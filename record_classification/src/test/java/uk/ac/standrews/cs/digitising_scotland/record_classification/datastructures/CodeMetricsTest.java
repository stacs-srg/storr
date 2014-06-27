package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CodeMetrics;

public class CodeMetricsTest {

    @Test
    public void test() {

        Bucket testingBucket = new Bucket();
        CodeMetrics metrics = new CodeMetrics(testingBucket);
        metrics.getHitGoldStandard();
        metrics.getIncorretPredictions();
        metrics.getMissedGoldStandard();

    }

}
