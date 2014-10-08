package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * The Class ExactMatchPipeline us a holder class for an {@link ExactMatchClassifier}.
 * Provides convenience methods for classifying records and buckets.
 */
public class ExactMatchPipeline implements IPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchPipeline.class);

    /** The exact match classifier. */
    private ExactMatchClassifier classifier;

    private Bucket classifed;

    /**
     * Instantiates a new exact match pipeline.
     *
     * @param exactMatchClassifier the exact match classifier
     */
    public ExactMatchPipeline(final ExactMatchClassifier exactMatchClassifier) {

        this.classifier = exactMatchClassifier;
        setClassifed(new Bucket());
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
        int descriptionCount = 0;
        int match = 0;
        boolean allMatch = false;

        for (Record record : bucket) {
            count++;
            LOGGER.info("Exact Matching record " + count + " of " + bucket.size());
            allMatch = true;

            for (String description : record.getDescription()) {
                descriptionCount++;
                final Set<Classification> result = classify(description);

                if (result != null) {
                    match++;
                    addResultToRecord(record, description, result);
                }
                else {
                    allMatch = false;
                }
            }

            if (allMatch) {
                classified.addRecordToBucket(record);
            }

        }

        LOGGER.info("Total exact matched = " + match + "/" + descriptionCount);
        LOGGER.info("Size of classified bucket = " + classified.size());

        this.setClassifed(classified);

        return getUnClassified(classified, bucket);
    }

    private Bucket getUnClassified(final Bucket classified, final Bucket bucket) {

        return BucketUtils.getComplement(bucket, classified);
    }

    protected void addResultToRecord(final Record record, final String description, final Set<Classification> result) {

        for (Classification codeTriple : result) {
            record.addClassification(description, codeTriple);
        }
    }

    /**
     * Classifies a String, which should correspond to a description, to a set of {@link Classification} objects.
     * @param description String to classify
     * @return A set of {@link Classification}s that contain the code, confidence and tokens used to produce classification.
     * @throws IOException I/O Exception
     */
    public Set<Classification> classify(final String description) throws IOException {

        return classifier.classifyTokenSetToCodeTripleSet(new TokenSet(description));

    }

    private void setClassifed(final Bucket classifed) {

        this.classifed = classifed;
    }

    @Override
    public Bucket getSuccessfullyClassified() {

        return classifed;
    }

}
