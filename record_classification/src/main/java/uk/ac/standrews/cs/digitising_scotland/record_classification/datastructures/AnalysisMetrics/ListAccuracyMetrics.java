package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.AnalysisMetrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

// TODO: Tidying!
/**
 * Class representing the statistics about a bucket of Records.
 * 
 * @author jkc25
 */
public class ListAccuracyMetrics {

	/**
	 * The total aggregated records.
	 */
	private int recordsInBucket;

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

	/** The coded exact match. */
	private int codedExactMatch;

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

	/** The prop gold predicted. */
	private double propGoldPredicted;

	/** The unclassified. */
	private int unclassified;

	/** The single classification. */
	private int singleClassification;

	/** The two classifications. */
	private int twoClassifications;

	/** The more than two classifications. */
	private int moreThanTwoClassifications;

	/** The prop wrongly predicted. */
	private double propWronglyPredicted;

	/** The number of codes not coded. */
	private int[] numberOfCodesNotCoded;

	/** The over under predicion matrix. */
	private int[][] overUnderPredictionMatrix;

	/**
	 * Instantiates a new list accuracy metrics.
	 * 
	 * @param listOfRecrods
	 *            the list of recrods
	 */
	public ListAccuracyMetrics(final List<Record> listOfRecrods) {

		this(new Bucket(listOfRecrods));
	}

	/**
	 * Instantiates a new list accuracy metrics.
	 * 
	 * @param bucket
	 *            the bucket of records
	 */
	public ListAccuracyMetrics(final Bucket bucket) {

		recordsInBucket = bucket.size();
		averageConfidence = calculateAverageConfidence(bucket);
		numConfidenceOfOne = calculateNumConfidenceOfOne(bucket);
		numConfidenceNotOne = calculateNumConfidenceNotOne(bucket);
		propGoldPredicted = calculatePropGoldStandardCorrectlyPredicted(bucket);
		codedExactMatch = calculateExactMatch(bucket);
		numberOfCodesNotCoded = calculateBreakDownOfMatches(bucket);
		countNumClassifications(bucket);
		int maxCodes = calculateMaxCodes(bucket);
		overUnderPredictionMatrix = calculateOverPredictionMatrix(bucket,
				maxCodes);
	}

	/**
	 * Calculate over prediction matrix.
	 * 
	 * @param bucket
	 *            the bucket
	 * @param maxCodes
	 *            the max codes
	 * @return the int[][]
	 */
	private int[][] calculateOverPredictionMatrix(final Bucket bucket,
			final int maxCodes) {

		overUnderPredictionMatrix = new int[maxCodes + 1][maxCodes + 1];
		for (Record record : bucket) {
			int goldStandardSize = record.getGoldStandardClassificationSet()
					.size();
			int actualSize = record.getCodeTriples().size();
			overUnderPredictionMatrix[actualSize][goldStandardSize]++;
		}
		return overUnderPredictionMatrix;
	}

	/**
	 * Calculate max codes.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
	private int calculateMaxCodes(final Bucket bucket) {

		int maxCodes = 0;
		for (Record record : bucket) {

			if (record.getGoldStandardClassificationSet().size() > maxCodes) {
				maxCodes = record.getGoldStandardClassificationSet().size();
			}
			if (record.getCodeTriples().size() > maxCodes) {
				maxCodes = record.getCodeTriples().size();
			}
		}

		return maxCodes;
	}

	/**
	 * Calculate exact match.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
	private int[] calculateBreakDownOfMatches(final Bucket bucket) {

		int[] missedCodesCounter = new int[16];
		for (int i = 0; i < missedCodesCounter.length; i++) {
			missedCodesCounter[i] = 0;
		}

		for (Record record : bucket) {

			Set<CodeTriple> goldStandrdSet = record
					.getGoldStandardClassificationSet();
			final Set<CodeTriple> codedTriples = record.getCodeTriples();
			int missedCodeCount = 0;
			for (CodeTriple goldTriple : goldStandrdSet) {

				int matchCount = 0;

				for (CodeTriple classification : codedTriples) {
					if (goldTriple.getCode() == classification.getCode()) {
						matchCount++;
					}
				}
				if (matchCount == 0) {
					missedCodeCount++;
				}
			}

			missedCodesCounter[missedCodeCount]++;
		}

		return missedCodesCounter;
	}

	/**
	 * Calculate exact match.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
	private int calculateExactMatch(final Bucket bucket) {

		int exactMatch = 0;

		for (Record record : bucket) {
			final Iterator<CodeTriple> iterator = record.getCodeTriples()
					.iterator();
			double totalConfidence = 0;
			while (iterator.hasNext()) {
				CodeTriple codeTriple = iterator.next();
				totalConfidence += codeTriple.getConfidence();
			}

			if ((totalConfidence / (double) record.getCodeTriples().size()) % 2 == 0) {
				exactMatch++;
			}
		}

		return exactMatch;
	}

	/**
	 * Prints the statistics generated with pretty formatting.
	 */
	public void prettyPrint() {

		System.out.println("Total records in bucket: " + recordsInBucket);
		System.out.println("Average confidence: " + averageConfidence);
		System.out.println("Total number of classifications: "
				+ (numConfidenceNotOne + numConfidenceOfOne));
		System.out.println("Number of classifications with confidence of 1: "
				+ numConfidenceOfOne);
		System.out.println("Number of classifications with confidence < 1: "
				+ numConfidenceNotOne);
		System.out.println("Proportion of gold standard codes predicted: "
				+ propGoldPredicted);
		System.out
				.println("Proportion of incorrect gold standard codes predicted: "
						+ propWronglyPredicted);
		System.out.println("Number unclassified: " + unclassified);
		System.out.println("Number coded by exact match: " + codedExactMatch);
		System.out.println("Singly classified: " + singleClassification);
		System.out.println("Doubly classified: " + twoClassifications);
		System.out
				.println("Multiply classified: " + moreThanTwoClassifications);
		printMatrix("Over/Under Prediction Matrix", overUnderPredictionMatrix);
	}

	/**
	 * Prints a matrix.
	 * 
	 * @param message
	 *            the message to add to the top of the matrix
	 * @param matrix
	 *            the matrix to print
	 * @return the string
	 */
	private String printMatrix(final String message, final int[][] matrix) {

		StringBuilder sb = new StringBuilder();

		sb.append(message).append("\n");
		sb.append("   ");
		sb.append("\n").append(getMatrixAsString(matrix, "\t", true));
		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * Prints a matrix with values separated by the chosen delimiter. The
	 * row/column ID can be printed or not as required by using printLabels.
	 * 
	 * @param matrix
	 *            the matrix to print
	 * @param delimiter
	 *            the delimiter
	 * @param printLabels
	 *            the print labels
	 * @return the matrix as string
	 */
	public String getMatrixAsString(final int[][] matrix,
			final String delimiter, final boolean printLabels) {

		StringBuilder sb = new StringBuilder();

		if (printLabels) {
			sb.append("\t");
			for (int i = 0; i < matrix.length; i++) {
				sb.append(i).append(delimiter);
			}
			sb.append("\n");
		}

		for (int i = 0; i < matrix.length; i++) {
			if (printLabels) {
				sb.append(i).append(delimiter);
			}

			for (int j = 0; j < matrix.length; j++) {
				sb.append(matrix[i][j]);

				if (j < matrix.length - 1) {
					sb.append(delimiter);
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Prints the statistics generated with pretty formatting.
	 * 
	 * @param pathToExperiemntFolder
	 *            the path to experiemnt folder
	 * @param pathToGraph
	 *            the path to graph
	 */
	public void generateMarkDownSummary(final String pathToExperiemntFolder,
			final String pathToGraph) {

		StringBuilder sb = new StringBuilder();
		sb.append("#Classification Report    \n" + "##Summary    \n");
		sb.append("Total records in bucket: ").append(recordsInBucket)
				.append("   \n");
		sb.append("Average confidence: ").append(averageConfidence)
				.append("    \n");
		sb.append("Total number of classifications: ")
				.append(numConfidenceNotOne + numConfidenceOfOne)
				.append("   \n");
		sb.append("Number of classifications with confidence of 1: ")
				.append(numConfidenceOfOne).append("    \n");
		sb.append("Number of classifications with confidence < 1: ")
				.append(numConfidenceNotOne).append("   \n");
		sb.append("Proportion of gold standard codes predicted: ")
				.append(propGoldPredicted).append("    \n");
		sb.append("Proportion of incorrect gold standard codes predicted: ")
				.append(propWronglyPredicted).append("    \n");
		sb.append("Number unclassified: ").append(unclassified)
				.append("    \n");
		sb.append("Number coded by exact match: ").append(codedExactMatch)
				.append("    \n");
		sb.append("Singly classified: ").append(singleClassification)
				.append("    \n");
		sb.append("Doubly classified: ").append(twoClassifications)
				.append("   \n");
		sb.append("Multiply classified: ").append(moreThanTwoClassifications)
				.append("    \n");
		sb.append("Over/Under Matrix    \n");
		sb.append(getMatrixAsString(overUnderPredictionMatrix, "\t", true));
		sb.append("    \n\n");
		sb.append("##Graphs    \n");
		sb.append("![Graph Matrix][").append(pathToGraph).append("]     \n");
		sb.append("   \n\n");
		sb.append("[").append(pathToGraph).append("]: ").append(pathToGraph)
				.append(".png \"Graph Matrix\"    \n");
		// sb.append("![Graph](graph.png)");
		Utils.writeToFile(sb.toString(), pathToExperiemntFolder
				+ "/Reports/summary.md", true);
		Utils.writeToFile(
				getMatrixAsString(overUnderPredictionMatrix, ",", false),
				pathToExperiemntFolder + "/Data/classificationCountMatrix.csv");

	}

	/**
	 * Count num classifications.
	 * 
	 * @param bucket
	 *            the bucket
	 */
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
			} else if (size == 1) {
				singleClassification++;
			} else if (size == 2) {
				twoClassifications++;
			} else if (size > 2) {
				moreThanTwoClassifications++;
			}
		}
	}

	/**
	 * Calculate prop gold standard correctly predicted.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the double
	 */
	private double calculatePropGoldStandardCorrectlyPredicted(
			final Bucket bucket) {

		double propGoldPredicted = 0.;

		for (Record record : bucket) {
			Set<CodeTriple> setCodeTriples = record.getCodeTriples();
			Set<CodeTriple> goldStandardTriples = record
					.getGoldStandardClassificationSet();
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

			propGoldPredicted += count / (double) goldStandardTriples.size();
		}
		return propGoldPredicted / bucket.size();
	}

	/**
	 * Calculate num confidence of one.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
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

	/**
	 * Calculate num confidence not one.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
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

	/**
	 * Calculate average confidence.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the double
	 */
	private double calculateAverageConfidence(final Bucket bucket) {

		double totalConfidence = 0;
		double totalMeasurements = 0;

		for (Record record : bucket) {
			Set<CodeTriple> setCodeTriples = record.getCodeTriples();
			for (CodeTriple codeTriple : setCodeTriples) {
				totalConfidence += codeTriple.getConfidence();
				totalMeasurements++;

			}
		}

		return totalConfidence / totalMeasurements;
	}

	/**
	 * Calculate unique records.
	 * 
	 * @param bucket
	 *            the bucket
	 * @return the int
	 */
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
	 * Gets the total records in bucket.
	 * 
	 * @return the total aggregated records
	 */
	public int getTotalRecordsInBucket() {

		return recordsInBucket;
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
	 * @param averageConfidence
	 *            the new average confidence
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
	 * @param numConfidenceOfOne
	 *            the new coded from lookup
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
	 * @param numConfidenceNotOne
	 *            the new coded by machine learning
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
	 * @param microPrecision
	 *            the new micro precision
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
	 * @param microRecall
	 *            the new micro recall
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
	 * @param macroPrecision
	 *            the new macro precision
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
	 * @param macroRecall
	 *            the new macro recall
	 */
	public void setMacroRecall(final double macroRecall) {

		this.macroRecall = macroRecall;
	}

	/**
	 * Gets the coded by sub string match.
	 * 
	 * @return the coded by sub string match
	 */
	public int getCodedBySubStringMatch() {

		return codedExactMatch;
	}

	/**
	 * Sets the coded by sub string match.
	 * 
	 * @param codedBySubStringMatch
	 *            the new coded by sub string match
	 */
	public void setCodedBySubStringMatch(final int codedBySubStringMatch) {

		this.codedExactMatch = codedBySubStringMatch;
	}
}
