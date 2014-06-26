package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import org.junit.Test;

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
