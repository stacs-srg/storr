package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;

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
    public InvertedSoftConfusionMatrix(final Bucket bucket, final CodeIndexer codeIndex) {

        super(bucket, codeIndex);
    }

    /**
     * True posotive and false negative.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void truePosAndFalseNeg(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

        for (Classification goldStandardCode : goldStandardTriples) {
            final Code code = goldStandardCode.getCode();
            if (hasDescendants(code, setCodeTriples)) {
                truePositive[index.getID(code)]++;
            }
        }
    }

    /**
     * Total and false positive.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected void totalAndFalsePos(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples) {

        for (Classification predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[index.getID(code)]++;
        }
    }

    /**
     * Checks for descendants.
     *
     * @param code the code
     * @param setCodeTriples the set code triples
     * @return true, if successful
     */
    private boolean hasDescendants(final Code code, final Set<Classification> setCodeTriples) {

        for (Classification codeTriple : setCodeTriples) {
            if (codeTriple.getCode().isDescendant(code)) { return true; }
        }
        return false;
    }

    /**
     * Unsupported operation! Throws exception!.
     *
     * @return the false positive
     */
    @Override
    public double[] getFalsePositive() {

        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation! Throws exception!.
     *
     * @return the false negative
     */
    @Override
    public double[] getFalseNegative() {

        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation! Throws exception!.
     *
     * @return the true negative
     */
    @Override
    public double[] getTrueNegative() {

        throw new UnsupportedOperationException();
    }
}
