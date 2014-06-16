package uk.ac.standrews.cs.usp.parser.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;

// TODO: Auto-generated Javadoc

/**
 * Class representing the statistics about a bucket of Records.
 *
 * @author jkc25
 */
public class ListAccuracyMetrics {

    /**
     * The unique records.
     */
    private int uniqueRecords;

    /**
     * The total aggregated records.
     */
    private int totalAggregatedRecords;

    /**
     * The average confidence.
     */
    private double averageConfidence;

    /**
     * The coded from lookup.
     */
    private int numConfidenceOfOne;

    /**
     * The coded by machine learning.
     */
    private int numConfidenceNotOne;

    private int codedBySubStringMatch;

    /**
     * The micro precision.
     */
    private double microPrecision;

    /**
     * The micro recall.
     */
    private double microRecall;

    /**
     * The macro precision.
     */
    private double macroPrecision;

    /**
     * The macro recall.
     */
    private double macroRecall;

    private double propGoldPredicted;

    private int unclassified;

    private int singleClassification;

    private int twoClassifications;

    private int moreThanTwoClassifications;

    /**
     * Instantiates a new list accuracy metrics.
     *
     * @param listOfRecrods the list of recrods
     */
    public ListAccuracyMetrics(final ArrayList<Record> listOfRecrods) {

        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new list accuracy metrics.
     *
     * @param bucket the bucket of records
     */
    public ListAccuracyMetrics(final Bucket bucket) {

        uniqueRecords = calculateUniqueRecords(bucket);
        totalAggregatedRecords = bucket.size();
        averageConfidence = calculateAverageConfidence(bucket);
        numConfidenceOfOne = calculateNumConfidenceOfOne(bucket);
        numConfidenceNotOne = calculateNumConfidenceNotOne(bucket);
        propGoldPredicted = calculatePropGoldStandardCorrectlyPredicted(bucket);
        countNumClassifications(bucket);

    }

    /**
     * Prints the statistics generated with pretty formatting.
     */
    public void prettyPrint() {

        System.out.println("Unique records: " + uniqueRecords);
        System.out.println("Total aggregated: " + totalAggregatedRecords);
        System.out.println("Average confidence: " + averageConfidence);
        System.out.println("Total number of classifications: " + (numConfidenceNotOne + numConfidenceOfOne));
        System.out.println("Number of classifications with confidence of 1: " + numConfidenceOfOne);
        System.out.println("Number of classifications with confidence < 1: " + numConfidenceNotOne);
        System.out.println("Proportion of gold standard codes predicted: " + propGoldPredicted);
        System.out.println("Number unclassified: " + unclassified);
        System.out.println("Singly classified: " + singleClassification);
        System.out.println("Doubly classified: " + twoClassifications);
        System.out.println("Multiply classified: " + moreThanTwoClassifications);

    }

    private void countNumClassifications(final Bucket bucket) {

        unclassified = 0;
        singleClassification = 0;
        twoClassifications = 0;
        moreThanTwoClassifications = 0;
        for (Record record : bucket) {
            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            int size = setCodeTriples.size();
            if (size < 1) {
                unclassified++;
            }
            else if (size == 1) {
                singleClassification++;
            }
            else if (size == 2) {
                twoClassifications++;
            }
            else if (size > 2) {
                moreThanTwoClassifications++;
            }
        }
    }

    private double calculatePropGoldStandardCorrectlyPredicted(final Bucket bucket) {

        double propGoldPredicted = 0.;

        for (Record record : bucket) {
            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            Set<CodeTriple> goldStandardTriples = record.getGoldStandardClassificationSet();
            if (goldStandardTriples.size() < 1) {
                break;
            }
            int count = 0;
            for (CodeTriple goldTriple : goldStandardTriples) {
                for (CodeTriple classification : setCodeTriples) {
                    if (goldTriple.getCode() == classification.getCode()) {
                        count++;
                    }
                }
            }

            propGoldPredicted += (count / goldStandardTriples.size());
        }
        return propGoldPredicted / bucket.size();
    }

    private int calculateNumConfidenceOfOne(final Bucket bucket) {

        int totallookup = 0;

        for (Record record : bucket) {
            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            for (CodeTriple classification : setCodeTriples) {
                if (classification.getConfidence() == 1) {
                    totallookup++;
                }

            }
        }

        return totallookup;
    }

    private int calculateNumConfidenceNotOne(final Bucket bucket) {

        int totalMi = 0;

        for (Record record : bucket) {
            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            for (CodeTriple classification : setCodeTriples) {
                if (classification.getConfidence() != 1) {
                    totalMi++;
                }

            }
        }

        return totalMi;
    }

    private double calculateAverageConfidence(final Bucket bucket) {

        double totalConfidence = 0;
        double totalMeasurments = 0;

        for (Record record : bucket) {
            Set<CodeTriple> setCodeTriples = record.getCodeTriples();
            for (CodeTriple codeTriple : setCodeTriples) {
                totalConfidence += codeTriple.getConfidence();
                totalMeasurments++;

            }
        }

        return totalConfidence / totalMeasurments;
    }

    private int calculateUniqueRecords(final Bucket bucket) {

        HashMap<Record, Integer> tempMap = new HashMap<>();
        for (Record record : bucket) {
            tempMap.put(record, 0);
        }

        return tempMap.size();
    }

    /**
     * Instantiates a new list accuracy metrics.
     */
    public ListAccuracyMetrics() {

        // TODO Auto-generated constructor stub
    }

    /**
     * Gets the unique records.
     *
     * @return the unique records
     */
    public int getUniqueRecords() {

        return uniqueRecords;
    }

    /**
     * Sets the unique records.
     *
     * @param uniqueRecords the new unique records
     */
    public void setUniqueRecords(final int uniqueRecords) {

        this.uniqueRecords = uniqueRecords;
    }

    /**
     * Gets the total aggregated records.
     *
     * @return the total aggregated records
     */
    public int getTotalAggregatedRecords() {

        return totalAggregatedRecords;
    }

    /**
     * Sets the total aggregated records.
     *
     * @param totalAggregatedRecords the new total aggregated records
     */
    public void setTotalAggregatedRecords(final int totalAggregatedRecords) {

        this.totalAggregatedRecords = totalAggregatedRecords;
    }

    /**
     * Gets the average confidence.
     *
     * @return the average confidence
     */
    public double getAverageConfidence() {

        return averageConfidence;
    }

    /**
     * Sets the average confidence.
     *
     * @param averageConfidence the new average confidence
     */
    public void setAverageConfidence(final int averageConfidence) {

        this.averageConfidence = averageConfidence;
    }

    /**
     * Gets the coded from lookup.
     *
     * @return the coded from lookup
     */
    public int getNumConfidenceOfOne() {

        return numConfidenceOfOne;
    }

    /**
     * Sets the coded from lookup.
     *
     * @param numConfidenceOfOne the new coded from lookup
     */
    public void setNumConfidenceOfOne(final int numConfidenceOfOne) {

        this.numConfidenceOfOne = numConfidenceOfOne;
    }

    /**
     * Gets the coded by machine learning.
     *
     * @return the coded by machine learning
     */
    public int getNumConfidenceNotOne() {

        return numConfidenceNotOne;
    }

    /**
     * Sets the coded by machine learning.
     *
     * @param numConfidenceNotOne the new coded by machine learning
     */
    public void setNumConfidenceNotOne(final int numConfidenceNotOne) {

        this.numConfidenceNotOne = numConfidenceNotOne;
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
     * Sets the micro precision.
     *
     * @param microPrecision the new micro precision
     */
    public void setMicroPrecision(final double microPrecision) {

        this.microPrecision = microPrecision;
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
     * Sets the micro recall.
     *
     * @param microRecall the new micro recall
     */
    public void setMicroRecall(final double microRecall) {

        this.microRecall = microRecall;
    }

    /**
     * Gets the macro precision.
     *
     * @return the macro precision
     */
    public double getMacroPrecision() {

        return macroPrecision;
    }

    /**
     * Sets the macro precision.
     *
     * @param macroPrecision the new macro precision
     */
    public void setMacroPrecision(final double macroPrecision) {

        this.macroPrecision = macroPrecision;
    }

    /**
     * Gets the macro recall.
     *
     * @return the macro recall
     */
    public double getMacroRecall() {

        return macroRecall;
    }

    /**
     * Sets the macro recall.
     *
     * @param macroRecall the new macro recall
     */
    public void setMacroRecall(final double macroRecall) {

        this.macroRecall = macroRecall;
    }

    public int getCodedBySubStringMatch() {

        return codedBySubStringMatch;
    }

    public void setCodedBySubStringMatch(final int codedBySubStringMatch) {

        this.codedBySubStringMatch = codedBySubStringMatch;
    }

}
