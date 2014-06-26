package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;

public class CodeMetrics {

    private double[] falsePositive;
    private double[] trueNegative;
    private double[] falseNegative;
    private double[] truePositive;
    private double[] totalPredictions;

    public CodeMetrics(final Bucket bucket) {

        final int numberOfOutputClasses = CodeFactory.getInstance().getNumberOfOutputClasses();
        falsePositive = new double[numberOfOutputClasses];
        trueNegative = new double[numberOfOutputClasses];
        falseNegative = new double[numberOfOutputClasses];
        truePositive = new double[numberOfOutputClasses];
        totalPredictions = new double[numberOfOutputClasses];
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
            totalAndFalsePos(setCodeTriples, goldStandardTriples);
            truePosAndFalseNeg(setCodeTriples, goldStandardTriples);
        }
        calculateTrueNeg();
    }

    private void calculateTrueNeg() {

        for (int i = 0; i < trueNegative.length; i++) {
            trueNegative[i] = totalPredictions[i] - truePositive[i] - falseNegative[i] - falsePositive[i];
        }
    }

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

        return sum(falsePositive);
    }

    public double getMissedGoldStandard() {

        return sum(falseNegative);
    }

    public double getHitGoldStandard() {

        return sum(truePositive);
    }

    private double sum(final double[] array) {

        double sum = 0;
        for (double d : array)
            sum += d;
        return sum;
    }
}
