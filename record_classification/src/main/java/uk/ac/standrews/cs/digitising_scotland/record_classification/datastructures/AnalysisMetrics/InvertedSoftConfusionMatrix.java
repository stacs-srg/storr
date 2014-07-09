package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

/**
 * Exists to count the number of predicted codes that are too specific. i.e. A1234
 * predicted when the true gold standard code was A123. False positive, false negative
 * and true negative getters all throw unsupported operation exceptions because they
 * are meaningless given this correctness function.
 * Created by fraserdunlop on 03/07/2014 at 10:09.
 */
public class InvertedSoftConfusionMatrix extends AbstractConfusionMatrix {

    /**
     * Instantiates a new inverted soft confusion matrix.
     *
     * @param bucket the bucket
     */
    public InvertedSoftConfusionMatrix(final Bucket bucket) {

        super(bucket);
    }

    /**
     * True posotive and false negative.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple goldStandardCode : goldStandardTriples) {
            final Code code = goldStandardCode.getCode();
            if (hasDescendants(code, setCodeTriples)) {
                truePositive[code.getID()]++;
            }
        }
    }

    /**
     * Total and false positive.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[code.getID()]++;
        }
    }

    /**
     * Checks for descendants.
     *
     * @param code the code
     * @param setCodeTriples the set code triples
     * @return true, if successful
     */
    private boolean hasDescendants(Code code, Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode().isDescendant(code)) { return true; }
        }
        return false;
    }

    /**
     * Unsupported operation! Throws exception!
     */
    @Override
    public double[] getFalsePositive() {

        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation! Throws exception!
     */
    @Override
    public double[] getFalseNegative() {

        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation! Throws exception!
     */
    @Override
    public double[] getTrueNegative() {
        throw new UnsupportedOperationException();
    }
}
