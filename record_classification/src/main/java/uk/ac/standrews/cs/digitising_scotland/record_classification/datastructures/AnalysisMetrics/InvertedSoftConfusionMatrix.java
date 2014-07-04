package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

// TODO: Auto-generated Javadoc
/**
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

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            final Code code = goldStanardCode.getCode();
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

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.AbstractConfusionMatrix#getFalsePositive()
     */
    @Override
    public double[] getFalsePositive() {

        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.AbstractConfusionMatrix#getFalseNegative()
     */
    @Override
    public double[] getFalseNegative() {

        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics.AbstractConfusionMatrix#getTrueNegative()
     */
    @Override
    public double[] getTrueNegative() {

        throw new UnsupportedOperationException();
    }

}
