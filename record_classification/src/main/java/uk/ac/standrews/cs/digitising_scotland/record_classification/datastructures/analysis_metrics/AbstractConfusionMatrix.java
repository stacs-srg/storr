package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * Contains a count of total predictions, false positives,
 * false negatives, true positives and true negatives for a bucket.
 * Created by fraserdunlop on 02/07/2014 at 10:27.
 */
public abstract class AbstractConfusionMatrix {

    /** The total predictions. */
    protected double[] totalPredictions;

    /** The false positive. */
    protected double[] falsePositive;

    /** The true negative. */
    protected double[] trueNegative;

    /** The false negative. */
    protected double[] falseNegative;

    /** The true positive. */
    protected double[] truePositive;

    protected CodeIndexer index;

    /**
     * Instantiates a new abstract confusion matrix and populated the variables.
     *
     * @param bucket the bucket of classified records.
     * @param codeIndex the {@link CodeIndexer} that contains the mapping of codes to ID's for these classifications.

     */
    public AbstractConfusionMatrix(final Bucket bucket, final CodeIndexer codeIndex) {

        this.index = codeIndex;
        int numberOfOutputClasses = index.getNumberOfOutputClasses();
        totalPredictions = new double[numberOfOutputClasses];
        falsePositive = new double[numberOfOutputClasses];
        trueNegative = new double[numberOfOutputClasses];
        falseNegative = new double[numberOfOutputClasses];
        truePositive = new double[numberOfOutputClasses];
        countStats(bucket);

    }

    /**
     * Calculates the true positive and false negative count.
     *
     * @param setCodeTriples the set of code triples
     * @param goldStandardTriples the set of gold standard triples
     */
    protected abstract void truePosAndFalseNeg(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples);

    /**
     * Calculates the total number of predictions and false positive count.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected abstract void totalAndFalsePos(final Set<Classification> setCodeTriples, final Set<Classification> goldStandardTriples);

    /**
     * Calculates the true/false positive/negative counts for the bucket.
     *
     * @param bucket the bucket
     * @return the double
     */
    private void countStats(final Bucket bucket) {

        for (Record record : bucket) {
            Set<Classification> setCodeTriples = record.getClassifications();
            Set<Classification> goldStandardTriples = record.getGoldStandardClassificationSet();
            totalAndFalsePos(setCodeTriples, goldStandardTriples);
            truePosAndFalseNeg(setCodeTriples, goldStandardTriples);
        }
        calculateTrueNeg();
    }

    /**
     * Calculates true negative.
     */
    private void calculateTrueNeg() {

        for (int i = 0; i < trueNegative.length; i++) {
            trueNegative[i] = sum(totalPredictions) - truePositive[i] - falseNegative[i] - falsePositive[i];
        }
    }

    /**
     * Sums all the values in a array.
     *
     * @param array the array to sum
     * @return the double total of all values
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

    /**
     * Gets the total correctly predicted.
     *
     * @return the total correctly predicted
     */
    public double getTotalPredicted() {

        return sum(totalPredictions);
    }

    /**
     * Gets the total correctly predicted.
     *
     * @return the total correctly predicted
     */
    public double getTotalCorrectlyPredicted() {

        return sum(getTruePositive());
    }

}
