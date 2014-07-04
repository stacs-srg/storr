package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

// TODO: Auto-generated Javadoc
/**
 *
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

    /**
     * Instantiates a new abstract confusion matrix.
     *
     * @param bucket the bucket
     */
    public AbstractConfusionMatrix(final Bucket bucket) {

        int numberOfOutputClasses = CodeFactory.getInstance().getNumberOfOutputClasses();
        totalPredictions = new double[numberOfOutputClasses];
        falsePositive = new double[numberOfOutputClasses];
        trueNegative = new double[numberOfOutputClasses];
        falseNegative = new double[numberOfOutputClasses];
        truePositive = new double[numberOfOutputClasses];
        countStats(bucket);

    }

    /**
     * True positive and false negative.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected abstract void truePosAndFalseNeg(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples);

    /**
     * Total and false pos.
     *
     * @param setCodeTriples the set code triples
     * @param goldStandardTriples the gold standard triples
     */
    protected abstract void totalAndFalsePos(final Set<CodeTriple> setCodeTriples, final Set<CodeTriple> goldStandardTriples);

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
     * Calculate true negative.
     */
    private void calculateTrueNeg() {

        for (int i = 0; i < trueNegative.length; i++) {
            trueNegative[i] = sum(totalPredictions) - truePositive[i] - falseNegative[i] - falsePositive[i];
        }
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
