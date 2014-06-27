package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

/**
 * Contains the classifiers (that extend {@link AbstractClassifier}) that we
 * desire to use plus a classify method that allows us to classify the record
 * using each. Created by fraserdunlop on 25/04/2014 at 12:37.
 */
public class ClassificationPipeline implements Iterable<AbstractClassifier> {

	/** The classifier set. */
	private List<AbstractClassifier> classifierSet;

	/**
	 * Constructs an empty {@link ClassificationPipeline}.
	 */
	public ClassificationPipeline() {

		classifierSet = new ArrayList<AbstractClassifier>();
	}

	/**
	 * Constructs a {@link ClassificationPipeline} with the classifiers in the
	 * classifierList added to the pipeline.
	 * 
	 * @param classifierList
	 *            List of trained {@link AbstractClassifier}
	 */
	public ClassificationPipeline(final List<AbstractClassifier> classifierList) {

		this();
		for (AbstractClassifier abstractClassifier : classifierList) {
			addTrainedClassifier(abstractClassifier);
		}

	}

	/**
	 * Add a trained classifiers to the pipeline.
	 * 
	 * @param classifier
	 *            must implement {@link AbstractClassifier}. Add this classifier
	 *            to the pipeline.
	 */
	public void addTrainedClassifier(final AbstractClassifier classifier) {

		classifierSet.add(classifier);
	}

	/**
	 * Classify a record using the pipeline.
	 * 
	 * @param record
	 *            {@link Record} to classify.
	 * @throws IOException
	 *             Error reading from disk.
	 */
	public void classifyRecord(final Record record) throws IOException {

		for (final AbstractClassifier classifier : classifierSet) {
			classifier.classify(record);
			if (exactMatchCheck(record)) {
				return;
			}
		}
	}

	/**
	 * Loops over all classifications in a records classification set and
	 * returns true if the record has been classified successfully by exact
	 * match lookup.
	 * 
	 * @param record
	 *            {@link Record} to check
	 * @return true if record has been classified by Exact Match successfully,
	 *         false if not
	 */
	private boolean exactMatchCheck(final Record record) {

		for (CodeTriple codeTriple : record.getCodeTriples()) {
			if (codeTriple.getConfidence() == 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Classifies each record in the with each of the classiifers in the
	 * pipeline bucket.
	 * 
	 * @param bucket
	 *            the bucket to classify
	 * @throws IOException
	 *             IOException Error reading from disk.
	 */
	public void classifyBucket(final Bucket bucket) throws IOException {

		for (Record record : bucket) {
			classifyRecord(record);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<AbstractClassifier> iterator() {

		return classifierSet.iterator();
	}
}
