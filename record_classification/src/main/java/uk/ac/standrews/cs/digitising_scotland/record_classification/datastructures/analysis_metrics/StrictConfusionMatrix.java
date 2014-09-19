package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * This is the 'classic' true confusion matrix. Predictions are only correct if they
 * exactly match the gold standard code.
 * Created by fraserdunlop on 02/07/2014 at 10:50.
 */
public class StrictConfusionMatrix extends AbstractConfusionMatrix {

    /**
     * Instantiates a new strict confusion matrix.
     *
     * @param bucket the bucket
     */
    public StrictConfusionMatrix(final Bucket bucket) {

        super(bucket);
    }

    /**
     * True pos and false neg.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

        for (Classification goldStandardCode : goldStandardTriples) {
            final Code code = goldStandardCode.getCode();
            if (contains(code, setCodeTriples)) {
                truePositive[code.getID()]++;
            }
            else {
                falseNegative[code.getID()]++;
            }
        }

    }

    /**
     * Total and false pos.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

        for (Classification predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[code.getID()]++;
            if (!contains(code, goldStandardTriples)) {
                falsePositive[code.getID()]++;
            }
        }
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    public boolean contains(final Code code, final Set<Classification> setCodeTriples) {

        return Utils.contains(code, setCodeTriples);
    }

}
