package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

/**
 * This confusion matrix counts predictions as correct if they are either exactly
 * correct or an ancestor of the correct code (a more general code that is in the
 * same branch of the hierarchy).
 * Created by fraserdunlop on 02/07/2014 at 10:59.
 */
public class SoftConfusionMatrix extends AbstractConfusionMatrix {

    /**
     * Instantiates a new soft confusion matrix.
     *
     * @param bucket the bucket
     */
    public SoftConfusionMatrix(final Bucket bucket) {

        super(bucket);
    }

    /**
     * True positive and false negative.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            final Code code = goldStanardCode.getCode();
            if (containsOrHasAncestors(code, setCodeTriples)) {
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
    protected void totalAndFalsePos(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[code.getID()]++;
            if (!containsOrHasDescendants(code, goldStandardTriples)) {
                falsePositive[code.getID()]++;
            }
        }
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples or is an ancestor
     * of one of the codes in the specified set of CodeTriples.
     * @param code the code
     * @param setCodeTriples the set code triples
     * @return true, if successful
     */
    private boolean containsOrHasDescendants(final Code code, final Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode() == code || codeTriple.getCode().isDescendant(code)) { return true; }
        }
        return false;
    }

    /**
     * Returns true is a code is in the specified set of CodeTriples or is a descendant
     * of one of the codes in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    public boolean containsOrHasAncestors(final Code code, final Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode() == code || codeTriple.getCode().isAncestor(code)) { return true; }
        }
        return false;
    }

}
