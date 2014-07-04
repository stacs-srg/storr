package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Contains the standard higher order statistics that can be calculated from
 * a confusion matrix. Specifically, precision, recall, specificity, negative
 * predictive value, false positive rate, accuracy, micro precision, micro recall,
 * F1 score and Mathews' correlation coefficient.
 */
public class CodeMetrics {

    /** The false positive. */
    private double[] falsePositive;

    /** The true negative. */
    private double[] trueNegative;

    /** The false negative. */
    private double[] falseNegative;

    /** The true positive. */
    private double[] truePositive;

    /** The precision. */
    private double[] precision;

    /** The recall. */
    private double[] recall;

    /** The specificity. */
    private double[] specificity;

    /** The negative predictive value. */
    private double[] negativePredictiveValue;

    /** The false positive rate. */
    private double[] falsePositiveRate;

    /** The accuracy. */
    private double[] accuracy;

    /** The f1. */
    private double[] f1;

    /** The mcc. */
    private double[] mcc;

    /** The number of output classes. */
    private final int numberOfOutputClasses;

    /** The confusion matrix. */
    private AbstractConfusionMatrix confusionMatrix;

    /** The micro precision. */
    private double microPrecision = 0;

    /** The micro recall. */
    private double microRecall = 0;

    /**
     * Instantiates a new code metrics.
     *
     * @param confusionMatrix the confusion matrix
     */
    public CodeMetrics(final AbstractConfusionMatrix confusionMatrix) {

        this.confusionMatrix = confusionMatrix;
        falsePositive = confusionMatrix.getFalsePositive();
        trueNegative = confusionMatrix.getTrueNegative();
        falseNegative = confusionMatrix.getFalseNegative();
        truePositive = confusionMatrix.getTruePositive();

        numberOfOutputClasses = CodeFactory.getInstance().getNumberOfOutputClasses();
        precision = new double[numberOfOutputClasses];
        recall = new double[numberOfOutputClasses];
        specificity = new double[numberOfOutputClasses];
        negativePredictiveValue = new double[numberOfOutputClasses];
        falsePositiveRate = new double[numberOfOutputClasses];
        accuracy = new double[numberOfOutputClasses];
        f1 = new double[numberOfOutputClasses];
        mcc = new double[numberOfOutputClasses];

        generateHigherOrderStats();
    }

    /**
     * Generate higher order stats.
     */
    private void generateHigherOrderStats() {

        generatePrecision();
        generateRecall();
        generateSpecificity();
        generateNegativePredictiveValue();
        generateFalsePositiveRate();
        generateAccuracy();
        generateF1Score();
        generateMathewsCorrelation();
        generateMicroPrecision();
        generateMicroRecall();
    }

    /**
     * Generate micro precision.
     */
    private void generateMicroPrecision() {

        // tp/tp+fp
        double[] tpfp = add(truePositive, falsePositive);
        double tpsum = sum(truePositive);
        double tpfpsum = sum(tpfp);
        microPrecision = tpsum / tpfpsum;
    }

    /**
     * Generate micro recall.
     */
    private void generateMicroRecall() {

        // tp/tp+fn
        double[] tptn = add(truePositive, falseNegative);
        double tptnSum = sum(tptn);
        double tpSum = sum(truePositive);

        microRecall = tpSum / tptnSum;

    }

    /**
     * Generate recall.
     */
    private void generateRecall() {

        // tp/tp+fn
        double[] tptn = add(truePositive, falseNegative);
        recall = division(truePositive, tptn);

    }

    /**
     * Generate precision.
     */
    private void generatePrecision() {

        // tp/tp+fp
        double[] tpfp = add(truePositive, falsePositive);
        precision = division(truePositive, tpfp);
    }

    /**
     * Generate specificity.
     */
    private void generateSpecificity() {

        // tn/fp+tn
        double[] fptn = add(falsePositive, trueNegative);
        specificity = division(trueNegative, fptn);
    }

    /**
     * Generate negative predictive value.
     */
    private void generateNegativePredictiveValue() {

        // tn/tn+fn
        double[] tnfn = add(trueNegative, falseNegative);
        negativePredictiveValue = division(trueNegative, tnfn);
    }

    /**
     * Generate false postitive rate.
     */
    private void generateFalsePositiveRate() {

        // fp/fp+tn
        double[] fptn = add(falsePositive, trueNegative);
        falsePositiveRate = division(falsePositive, fptn);
    }

    /**
     * Generate accuracy.
     */
    private void generateAccuracy() {

        // tp+tn/total p + total negative
        double[] tptn = add(truePositive, trueNegative);
        double[] fpfn = add(falsePositive, falseNegative);
        double[] denominator = add(tptn, fpfn);
        accuracy = division(tptn, denominator);
    }

    /**
     * Generate f1 score.
     */
    private void generateF1Score() {

        // 2*tp/(2*tp+fp+fn)
        double[] twotp = add(truePositive, truePositive);
        double[] fpfn = add(falsePositive, falseNegative);
        f1 = division(twotp, add(twotp, fpfn));
    }

    /**
     * Generate mathews correlation.
     */
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
     * Performs element-wise addition.
     *
     * @param array1 the array1
     * @param array2 the array2
     * @return the double[]
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
     *
     * @param array1 the array1
     * @param array2 the array2
     * @return the double[]
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
     *
     * @param numerator the numerator
     * @param denominator the denominator
     * @return the double[]
     */
    private double[] division(final double[] numerator, final double[] denominator) {

        if (numerator.length != denominator.length) { throw new RuntimeException("Cannot add arrays of different length, array1 length: " + numerator.length + " array2 length: " + denominator.length); }

        double[] divisionResult = new double[numerator.length];
        for (int i = 0; i < divisionResult.length; i++) {
            final double divResult = numerator[i] / denominator[i];

            if (Double.isNaN(divResult)) {
                divisionResult[i] = 0;
            }
            else {
                divisionResult[i] = divResult;
            }
        }
        return divisionResult;
    }

    /**
     * Performs element-wise multiplication of arrays.
     *
     * @param array1 the array1
     * @param array2 the array2
     * @return the double[]
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
     *
     * @param array1 the array1
     * @param exponent the exponent
     * @return the double[]
     */
    private double[] pow(final double[] array1, final double exponent) {

        double[] result = new double[array1.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Math.pow(array1[i], exponent);
        }
        return result;
    }

    /**
     * Gets the stats per code.
     *
     * @param code the code
     * @return the stats per code
     */
    public String getStatsPerCode(final Code code) {

        return getStatsPerCode(code.getID());
    }

    /**
     * Gets the stats per code.
     *
     * @param id the id
     * @return the stats per code
     */
    public String getStatsPerCode(final int id) {

        StringBuilder sb = new StringBuilder();
        sb.append(CodeFactory.getInstance().getCode(id).getCodeAsString()).append(", ");
        sb.append(truePositive[id]).append(", ");
        sb.append(trueNegative[id]).append(", ");
        sb.append(falsePositive[id]).append(", ");
        sb.append(falseNegative[id]).append(", ");
        sb.append(precision[id]).append(", ");
        sb.append(recall[id]).append(", ");
        sb.append(specificity[id]).append(", ");
        sb.append(negativePredictiveValue[id]).append(", ");
        sb.append(falsePositiveRate[id]).append(", ");
        sb.append(accuracy[id]).append(", ");
        sb.append(f1[id]).append(", ");
        sb.append(mcc[id]);

        return sb.toString();
    }

    /**
     * Gets the incorret predictions.
     *
     * @return the incorret predictions
     */
    public double getIncorretPredictions() {

        return sum(falsePositive);
    }

    /**
     * Gets the missed gold standard.
     *
     * @return the missed gold standard
     */
    public double getMissedGoldStandard() {

        return sum(falseNegative);
    }

    /**
     * Gets the hit gold standard.
     *
     * @return the hit gold standard
     */
    public double getHitGoldStandard() {

        return sum(truePositive);
    }

    /**
     * Gets the precision.
     *
     * @return the precision
     */
    public double[] getPrecision() {

        return precision.clone();
    }

    /**
     * Gets the recall.
     *
     * @return the recall
     */
    public double[] getRecall() {

        return recall.clone();
    }

    /**
     * Gets the specificity.
     *
     * @return the specificity
     */
    public double[] getSpecificity() {

        return specificity.clone();
    }

    /**
     * Gets the negative predictive value.
     *
     * @return the negative predictive value
     */
    public double[] getNegativePredictiveValue() {

        return negativePredictiveValue.clone();
    }

    /**
     * Gets the false positive rate.
     *
     * @return the false positive rate
     */
    public double[] getFalsePositiveRate() {

        return falsePositiveRate.clone();
    }

    /**
     * Gets the accuracy.
     *
     * @return the accuracy
     */
    public double[] getAccuracy() {

        return accuracy.clone();
    }

    /**
     * Gets the f1.
     *
     * @return the f1
     */
    public double[] getF1() {

        return f1.clone();
    }

    /**
     * Gets the mcc.
     *
     * @return the mcc
     */
    public double[] getMcc() {

        return mcc.clone();
    }

    /**
     * Number of codes.
     *
     * @return the int
     */
    public int numberOfCodes() {

        return numberOfOutputClasses;
    }

    /**
     * Gets the micro precision.
     *
     * @return the micro precision
     */
    public double getMicroPrecision() {

        return microPrecision;
    }

    /**
     * Gets the micro recall.
     *
     * @return the micro recall
     */
    public double getMicroRecall() {

        return microRecall;
    }

    /**
     * Creates and writes all the accumulated statistics to the specified file.
     * @param fileName The path to where we want to write the file
     */
    public void writeStats(final String fileName) {

        StringBuilder sb = new StringBuilder();
        sb.append("Code, True Positive, True Negative, False Positive, False Negative, Precision, Recall, Specificity, Negative Predictive Value, False Positive Rate, Accuracy, F1, MCC\n");

        for (int i = 0; i < numberOfCodes(); i++) {
            sb.append(getStatsPerCode(i)).append("\n");
        }

        Utils.writeToFile(sb.toString(), fileName);
    }

    /**
     * Gets the total correctly predicted.
     *
     * @return the total correctly predicted
     */
    public double getTotalCorrectlyPredicted() {

        return confusionMatrix.getTotalCorrectlyPredicted();
    }

}
