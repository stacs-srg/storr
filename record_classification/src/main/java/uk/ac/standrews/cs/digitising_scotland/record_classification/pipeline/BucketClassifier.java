package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

/**
 * The Class BucketClassifier provides convenience methods for classifying all records in a bucket with the {@link MachineLearningClassificationPipeline}.
 * 
 */
public class BucketClassifier {

    private final Logger logger = LoggerFactory.getLogger(BucketClassifier.class);

    /** The record classifier. */
    private MachineLearningClassificationPipeline recordClassifier;

    /**
     * Instantiates a new bucket classifier.
     *
     * @param recordClassifier the record classifier
     */
    public BucketClassifier(final MachineLearningClassificationPipeline recordClassifier) {

        this.recordClassifier = recordClassifier;
    }

    /**
     * Classifies all the records in the specified bucket.
     *
     * @param bucket the bucket
     * @return the bucket
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        int count = 0;
        int total = bucket.size();
        for (Record record : bucket) {
            logger.info("classifying record " + count + " of " + total);
            Set<CodeTriple> result = recordClassifier.classify(record);
            record.addAllCodeTriples(result);
            count++;
        }

        return bucket;

    }
}
