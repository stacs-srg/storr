package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.IOException;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;

/**
 * The Class BucketClassifier provides convenience methods for classifying all records in a bucket with the {@link RecordClassificationPipeline}.
 * 
 */
public class BucketClassifier {

    /** The record classifier. */
    private RecordClassificationPipeline recordClassifier;

    /**
     * Instantiates a new bucket classifier.
     *
     * @param recordClassifier the record classifier
     */
    public BucketClassifier(final RecordClassificationPipeline recordClassifier) {

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

        for (Record record : bucket) {
            Set<CodeTriple> result = recordClassifier.classify(record);
            record.addAllCodeTriples(result);
        }

        return bucket;

    }
}
