package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

public class RecordMetricCounter {

    private double incorretPredictions;
    private double correctPredictions;
    private double missedGoldStandard;
    private double hitGoldStandard;

    public RecordMetricCounter(final Bucket bucket) {

        countStats(bucket);
    }

    /**
     * Calculate proportion wrongly predicted.
     *
     * @param bucket the bucket
     * @return the double
     */
    private void countStats(final Bucket bucket) {

        for (Record record : bucket) {

            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            Set<CodeTriple> goldStandardTriples = record.getGoldStandardClassificationSet();
            incorretPredictions += countNumIncorrectPredictions(setCodeTriples, goldStandardTriples);
            correctPredictions += countCorrectPredictions(setCodeTriples, goldStandardTriples);
            missedGoldStandard += countNMissedGoldStandard(setCodeTriples, goldStandardTriples);
            hitGoldStandard += countHitGoldStandard(setCodeTriples, goldStandardTriples);

        }
    }

    private double countHitGoldStandard(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double correct = 0;

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            if (contains(goldStanardCode.getCode(), setCodeTriples)) {
                correct++;
            }
        }

        return correct;
    }

    private double countNMissedGoldStandard(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double missed = 0;

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            if (!contains(goldStanardCode.getCode(), setCodeTriples)) {
                missed++;
            }
        }

        return missed;
    }

    private double countCorrectPredictions(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double correct = 0;

        for (CodeTriple predictedCode : setCodeTriples) {
            if (contains(predictedCode.getCode(), goldStandardTriples)) {
                correct++;
            }
        }

        return correct;
    }

    private double countNumIncorrectPredictions(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double numIncorrectPredictions = 0;

        for (CodeTriple predictedCode : setCodeTriples) {
            if (!contains(predictedCode.getCode(), goldStandardTriples)) {
                numIncorrectPredictions++;
            }
        }
        return numIncorrectPredictions;
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    private boolean contains(final Code code, final Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode() == code) { return true; }
        }

        return false;
    }

    public double getIncorretPredictions() {

        return incorretPredictions;
    }

    public double getCorrectPredictions() {

        return correctPredictions;
    }

    public double getMissedGoldStandard() {

        return missedGoldStandard;
    }

    public double getHitGoldStandard() {

        return hitGoldStandard;
    }

}
