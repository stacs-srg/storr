package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

import java.util.Set;

/**
 *
 * Created by fraserdunlop on 02/07/2014 at 10:27.
 */
public class ConfusionMatrix {


    /** The total predictions. */
    private double[] totalPredictions;

    /** The false positive. */
    private double[] falsePositive;

    /** The true negative. */
    private double[] trueNegative;

    /** The false negative. */
    private double[] falseNegative;

    /** The true positive. */
    private double[] truePositive;

    public ConfusionMatrix(final Bucket bucket) {

        /* The number of output classes. */
        int numberOfOutputClasses = CodeFactory.getInstance().getNumberOfOutputClasses();
        totalPredictions = new double[numberOfOutputClasses];
        falsePositive = new double[numberOfOutputClasses];
        trueNegative = new double[numberOfOutputClasses];
        falseNegative = new double[numberOfOutputClasses];
        truePositive = new double[numberOfOutputClasses];
        countStats(bucket);
    }

    /**
     * True pos and false neg.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    private void truePosAndFalseNeg(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple goldStanardCode : goldStandardTriples) {
            final Code code = goldStanardCode.getCode();
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
    private void totalAndFalsePos(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples) {

        for (CodeTriple predictedCode : setCodeTriples) {
            final Code code = predictedCode.getCode();
            totalPredictions[code.getID()]++;
            if (!contains(code, goldStandardTriples)) {
                falsePositive[code.getID()]++;
            }
        }
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
            totalAndFalsePos(setCodeTriples, goldStandardTriples);
            truePosAndFalseNeg(setCodeTriples, goldStandardTriples);
        }
        calculateTrueNeg();
    }

    /**
     * Calculate true neg.
     */
    private void calculateTrueNeg() {

        for (int i = 0; i < trueNegative.length; i++) {
            trueNegative[i] = sum(totalPredictions) - truePositive[i] - falseNegative[i] - falsePositive[i];
        }
    }


    /**
     * Returns true is a code is in the specified set of CodeTriples.
     * @param code code to check for
     * @param setCodeTriples set to check in
     * @return true if present
     */
    public boolean contains(final Code code, final Set<CodeTriple> setCodeTriples) {

        for (CodeTriple codeTriple : setCodeTriples) {
            if (codeTriple.getCode() == code) { return true; }
        }
        return false;
    }

    /**
     * Sum.
     *
     * @param array the array
     * @return the double
     */
    private double sum(final double[] array) {

        double sum = 0;
        for (double d : array) {
            sum += d;
        }
        return sum;
    }

    /**
     * Gets the false positive.
     *
     * @return the false positive
     */
    public double[] getFalsePositive() {

        return falsePositive.clone();
    }


    /**
     * Gets the true positive.
     *
     * @return the true positive
     */
    public double[] getTruePositive() {

        return truePositive.clone();
    }


    /**
     * Gets the false negative.
     *
     * @return the false negative
     */
    public double[] getFalseNegative() {

        return falseNegative.clone();
    }



    /**
     * Gets the true negative.
     *
     * @return the true negative
     */
    public double[] getTrueNegative() {

        return trueNegative.clone();
    }

}
