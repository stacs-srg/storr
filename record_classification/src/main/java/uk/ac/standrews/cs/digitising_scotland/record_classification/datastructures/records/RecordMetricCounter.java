package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

// TODO: Auto-generated Javadoc
/**
 * The Class RecordMetricCounter.
 */
public class RecordMetricCounter {

    /** The incorret predictions. */
    private double incorretPredictions;

    /** The correct predictions. */
    private double correctPredictions;

    /** The missed gold standard. */
    private double missedGoldStandard;

    /** The hit gold standard. */
    private double hitGoldStandard;

    /**
     * Instantiates a new record metric counter.
     *
     * @param bucket the bucket
     */
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

    /**
     * Count hit gold standard.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     * @return the double
     */
    private double countHitGoldStandard(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double correct = 0;

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            if (contains(goldStanardCode.getCode(), setCodeTriples)) {
                correct++;
            }
        }

        return correct;
    }

    /**
     * Count n missed gold standard.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     * @return the double
     */
    private double countNMissedGoldStandard(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double missed = 0;

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            if (!contains(goldStanardCode.getCode(), setCodeTriples)) {
                missed++;
            }
        }

        return missed;
    }

    /**
     * Count correct predictions.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     * @return the double
     */
    private double countCorrectPredictions(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        double correct = 0;

        for (CodeTriple predictedCode : setCodeTriples) {
            if (contains(predictedCode.getCode(), goldStandardTriples)) {
                correct++;
            }
        }

        return correct;
    }

    /**
     * Count num incorrect predictions.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     * @return the double
     */
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

    /**
     * Gets the incorret predictions.
     *
     * @return the incorret predictions
     */
    public double getIncorretPredictions() {

        return incorretPredictions;
    }

    /**
     * Gets the correct predictions.
     *
     * @return the correct predictions
     */
    public double getCorrectPredictions() {

        return correctPredictions;
    }

    /**
     * Gets the missed gold standard.
     *
     * @return the missed gold standard
     */
    public double getMissedGoldStandard() {

        return missedGoldStandard;
    }

    /**
     * Gets the hit gold standard.
     *
     * @return the hit gold standard
     */
    public double getHitGoldStandard() {

        return hitGoldStandard;
    }

}
