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
    private double[] precision;
    private double[] recall;
    private double[] specificity;
    private double[] negativePredictiveValue;
    private double[] falsePositiveRate;
    private double[] accuracy;
    private double[] f1;
    private double[] mcc;
    private final int numberOfOutputClasses;

    public CodeMetrics(final Bucket bucket) {

        numberOfOutputClasses = CodeFactory.getInstance().getNumberOfOutputClasses();
        falsePositive = new double[numberOfOutputClasses];
        trueNegative = new double[numberOfOutputClasses];
        falseNegative = new double[numberOfOutputClasses];
        truePositive = new double[numberOfOutputClasses];
        totalPredictions = new double[numberOfOutputClasses];
        precision = new double[numberOfOutputClasses];
        recall = new double[numberOfOutputClasses];
        specificity = new double[numberOfOutputClasses];
        negativePredictiveValue = new double[numberOfOutputClasses];
        falsePositiveRate = new double[numberOfOutputClasses];
        accuracy = new double[numberOfOutputClasses];
        f1 = new double[numberOfOutputClasses];
        mcc = new double[numberOfOutputClasses];

        countStats(bucket);
        geneateHigherOrderStats();
    }

    private void geneateHigherOrderStats() {

        generatePrecision();
        generateRecall();
        generateSpecificity();
        generateNegativePredictiveValue();
        generateFalsePostitiveRate();
        generateAccuracy();
        generateF1Score();
        generateMathewsCorrelation();
    }

    private void generateRecall() {

        // tp/tp+fn
        double[] tptn = add(truePositive, falseNegative);
        recall = division(truePositive, tptn);

    }

    private void generatePrecision() {

        // tp/tp+fp
        double[] tpfp = add(truePositive, falsePositive);
        precision = division(truePositive, tpfp);
    }

    private void generateSpecificity() {

        // tn/fp+tn
        double[] fptn = add(falsePositive, trueNegative);
        specificity = division(trueNegative, fptn);
    }

    private void generateNegativePredictiveValue() {

        // tn/tn+fn
        double[] tnfn = add(trueNegative, falseNegative);
        negativePredictiveValue = division(trueNegative, tnfn);
    }

    private void generateFalsePostitiveRate() {

        // fp/fp+tn
        double[] fptn = add(falsePositive, trueNegative);
        falsePositiveRate = division(falsePositive, fptn);
    }

    private void generateAccuracy() {

        // tp+tn/total p + total negative
        double[] tptn = add(truePositive, trueNegative);
        accuracy = division(tptn, totalPredictions);
    }

    private void generateF1Score() {

        // 2*tp/(2*tp+fp+fn)
        double[] twotp = add(truePositive, truePositive);
        double[] fpfn = add(falsePositive, falseNegative);
        f1 = division(twotp, add(twotp, fpfn));
    }

    private void generateMathewsCorrelation() {

        double[] tptn = multiply(truePositive, trueNegative);
        double[] fpfn = multiply(falsePositive, falseNegative);
        double[] numerator = subtract(tptn, fpfn);
        double[] tpfp = add(truePositive, falsePositive);
        double[] tpfn = add(truePositive, falseNegative);
        double[] tnfp = add(trueNegative, falsePositive);
        double[] tnfn = add(trueNegative, falseNegative);
        double[] tpfptpfn = multiply(tpfp, tpfn);
        double[] tnfptnfn = multiply(tnfp, tnfn);
        double[] squaredDenom = multiply(tpfptpfn, tnfptnfn);
        double[] denominator = pow(squaredDenom, 0.5);

        mcc = division(numerator, denominator);

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

    private double sum(final double[] array) {

        double sum = 0;
        for (double d : array) {
            sum += d;
        }
        return sum;
    }

    /**
     * Performs element-wise addition.
     * @param array1
     * @param array2
     * @return
     */
    private double[] add(final double[] array1, final double[] array2) {

        if (array1.length != array2.length) { throw new RuntimeException("Cannot add arrays of different length, array1 length: " + array1.length + " array2 length: " + array2.length); }
        double[] sum = new double[array1.length];

        for (int i = 0; i < sum.length; i++) {
            sum[i] = array1[i] + array2[i];
        }
        return sum;
    }

    /**
     * Performs element-wise subtraction.
     * @param array1
     * @param array2
     * @return
     */
    private double[] subtract(final double[] array1, final double[] array2) {

        if (array1.length != array2.length) { throw new RuntimeException("Cannot add arrays of different length, array1 length: " + array1.length + " array2 length: " + array2.length); }
        double[] sum = new double[array1.length];

        for (int i = 0; i < sum.length; i++) {
            sum[i] = array1[i] - array2[i];
        }
        return sum;
    }

    /**
     * Performs element-wise division.
     * @param numerator
     * @param denominator
     * @return
     */
    private double[] division(final double[] numerator, final double[] denominator) {

        if (numerator.length != denominator.length) { throw new RuntimeException("Cannot add arrays of different length, array1 length: " + numerator.length + " array2 length: " + denominator.length); }
        double[] divisionResult = new double[numerator.length];

        for (int i = 0; i < divisionResult.length; i++) {
            divisionResult[i] = numerator[i] / denominator[i];
        }
        return divisionResult;
    }

    /**
     * Performs element-wise multiplication of arrays.
     * @param array1
     * @param array2
     * @return
     */
    private double[] multiply(final double[] array1, final double[] array2) {

        if (array1.length != array2.length) { throw new RuntimeException("Cannot add arrays of different length, array1 length: " + array1.length + " array2 length: " + array2.length); }
        double[] result = new double[array1.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = array1[i] * array2[i];
        }
        return result;
    }

    /**
     * Performs element-wise multiplication of arrays.
     * @param array1
     * @param array2
     * @return
     */
    private double[] pow(final double[] array1, final double exponent) {

        double[] result = new double[array1.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Math.pow(array1[i], exponent);
        }
        return result;
    }

    public String getStatsPerCode(Code code) {

        return getStatsPerCode(code.getID());
    }

    public String getStatsPerCode(int id) {

        StringBuilder sb = new StringBuilder();
        sb.append(CodeFactory.getInstance().getCode(id).getCodeAsString() + ", ");
        sb.append(precision[id] + ", ");
        sb.append(recall[id] + ", ");
        sb.append(specificity[id] + ", ");
        sb.append(negativePredictiveValue[id] + ", ");
        sb.append(falsePositiveRate[id] + ", ");
        sb.append(accuracy[id] + ", ");
        sb.append(f1[id] + ", ");
        sb.append(mcc[id]);

        return sb.toString();
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

    public double[] getPrecision() {

        return precision;
    }

    public double[] getRecall() {

        return recall;
    }

    public double[] getSpecificity() {

        return specificity;
    }

    public double[] getNegativePredictiveValue() {

        return negativePredictiveValue;
    }

    public double[] getFalsePositiveRate() {

        return falsePositiveRate;
    }

    public double[] getAccuracy() {

        return accuracy;
    }

    public double[] getF1() {

        return f1;
    }

    public double[] getMcc() {

        return mcc;
    }

    public int numberOfCodes() {

        return numberOfOutputClasses;
    }

}
