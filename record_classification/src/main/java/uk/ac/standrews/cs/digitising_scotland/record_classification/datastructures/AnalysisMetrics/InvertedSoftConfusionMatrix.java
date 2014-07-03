package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

import java.util.Set;

/**
 * Created by fraserdunlop on 03/07/2014 at 10:09.
 */
public class InvertedSoftConfusionMatrix extends AbstractConfusionMatrix {


    public InvertedSoftConfusionMatrix(final Bucket bucket){
        super(bucket);
    }


    /**
     * True pos and false neg.
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
     * Total and false pos.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {
        for (CodeTriple predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[code.getID()]++;
        }}

    private boolean hasDescendants(Code code, Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode().isDescendant(code)) { return true; }
        }
        return false;
    }

    @Override
    public double[] getFalsePositive(){
        throw new UnsupportedOperationException();
    }

    @Override
    public double[] getFalseNegative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double[] getTrueNegative() {
        throw new UnsupportedOperationException();
    }

}
