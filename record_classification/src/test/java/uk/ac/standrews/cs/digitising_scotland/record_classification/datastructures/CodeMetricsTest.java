package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.ConfusionMatrix;

public class CodeMetricsTest {

    @Test
    public void test() {

        Bucket testingBucket = new Bucket();
        CodeMetrics metrics = new CodeMetrics(new ConfusionMatrix(testingBucket));
        metrics.getHitGoldStandard();
        metrics.getIncorretPredictions();
        metrics.getMissedGoldStandard();

    }

}
