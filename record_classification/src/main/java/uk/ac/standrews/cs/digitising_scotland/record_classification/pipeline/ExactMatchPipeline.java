package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * The Class ExactMatchPipeline us a holder class for an {@link ExactMatchClassifier}.
 * Provides convenience methods for classifying records and buckets.
 */
public class ExactMatchPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchPipeline.class);

    /** The exact match classifier. */
    private ExactMatchClassifier classifier;

    /**
     * Instantiates a new exact match pipeline.
     *
     * @param exactMatchClassifier the exact match classifier
     */
    public ExactMatchPipeline(final ExactMatchClassifier exactMatchClassifier) {

        this.classifier = exactMatchClassifier;
    }

    /**
     * Attempt to classify everything in a bucket.
     * If a record has an exact match is put in the classified bucket.
     * If a record does not have an exact match then the record is discarded.
     * The bucket with classified records is returned.
     *
     * @param bucket the bucket to classify
     * @return the bucket of exact matched records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket classified = new Bucket();
        int count = 0;
        int match = 0;
        for (Record record : bucket) {
            count++;
            LOGGER.info("Exact Matching record " + count + " of " + bucket.size());
            final Set<CodeTriple> result = classify(record);
            if (result != null) {
                match++;
                record.addAllCodeTriples(result);
                classified.addRecordToBucket(record);
            }
        }
        LOGGER.info("Total exact matched = " + match + "/" + bucket.size());
        return classified;
    }

    /**
     * Returns the classification of a {@link Record} as a Set of
     * {@link CodeTriple}.
     * 
     * @param record
     *            to classify
     * @return Set<CodeTriple> the classifications
     * @throws IOException
     *             indicates an I/O Error
     */
    public Set<CodeTriple> classify(final Record record) throws IOException {

        TokenSet cleanedTokenSet = new TokenSet(record.getCleanedDescription());

        return classifier.classifyTokenSetToCodeTripleSet(cleanedTokenSet);

    }

}
