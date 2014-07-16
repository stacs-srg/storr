package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;

/**
 * All classifiers should extend this abstract defining training and
 * classification methods.
 * 
 * @author jkc25, frjd2
 */
public abstract class AbstractClassifier {

    /** The vector factory. */
    protected VectorFactory vectorFactory;

    /**
     * Creates an {@link AbstractClassifier} with the specified Vector Factory.
     * 
     * @param vectorFactory
     *            vector factory
     */
    public AbstractClassifier(final VectorFactory vectorFactory) {

        this.vectorFactory = vectorFactory;
    }

    /**
     * Creates a default {@link AbstractClassifier} with no
     * {@link VectorFactory}.
     */
    public AbstractClassifier() {

    }

    /**
     * Trains the extending classifier on a {@link Bucket} of {@link Record}
     * objects.
     * 
     * @param bucket
     *            bucket to train on
     * @throws Exception
     *             An exception will be thrown for any number of reasons,
     *             including using invalid training data, codes or malformed
     *             data
     */
    public abstract void train(final Bucket bucket) throws Exception;

    /**
     * Classifies a single {@link Record}.
     * 
     * @param record
     *            Record to classify
     * @return record record with a new {@link ClassificationSet}
     * @throws IOException
     *             An exception will be thrown for any number of reasons,
     *             including using invalid training data, codes or malformed
     *             data
     */
    public abstract Record classify(Record record) throws IOException;

    /**
     * Classifies all {@link Record}s in a {@link Bucket}.
     * 
     * @param bucket
     *            bucket containing Records to classify
     * @return Bucket with classified records
     * @throws IOException
     *             An exception will be thrown for any number of reasons,
     *             including using invalid training data, codes or malformed
     *             data
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket classifiedBucket = bucket;

        for (final Record record : bucket) {
            classifiedBucket.addRecordToBucket(classify(record));
        }

        return classifiedBucket;
    }

    /**
     * Classifies a {@link TokenSet} and returns a Mapping of a Code to it's
     * confidence.
     * 
     * @param string
     *            the TokenSet to classify
     * @return Map<Code, Double> The result of the classification.
     * @throws IOException
     *             Indicatte I/O Error
     */
    public abstract Pair<Code, Double> classify(final TokenSet string) throws IOException;

    /**
     * Reads a trained model from the default storage location for a classifier.
     *
     */
    public abstract void getModelFromDefaultLocation();

}
