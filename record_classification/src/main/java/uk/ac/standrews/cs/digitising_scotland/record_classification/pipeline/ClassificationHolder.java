package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;

/**
 * The Class ClassificationHolder.
 */
public class ClassificationHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationHolder.class);

    /** The exact match pipeline. */
    private ExactMatchPipeline exactMatchPipeline;

    /** The machine learning classifier. */
    private MachineLearningClassificationPipeline machineLearningClassifier;

    /** The exact matched. */
    private Bucket exactMatched;

    /** The not exact matched. */
    private Bucket notExactMatched;

    /** The machine learned. */
    private Bucket machineLearned;

    /** The all classified. */
    private Bucket allClassified;

    /**
     * Instantiates a new classification holder with an {@link ExactMatchPipeline} and a {@link MachineLearningClassificationPipeline} that
     * will be used to classify records that are passed in.
     *
     * @param exactMatchPipeline the ExactMatchPipeline used for exact matching records
     * @param machineLearningPipeline the MachineLearningClassificationPipeline used to produce multiply classified records
     */
    public ClassificationHolder(final ExactMatchPipeline exactMatchPipeline, final MachineLearningClassificationPipeline machineLearningPipeline) {

        this.exactMatchPipeline = exactMatchPipeline;
        this.machineLearningClassifier = machineLearningPipeline;
        exactMatched = new Bucket();
        notExactMatched = new Bucket();
        machineLearned = new Bucket();
        allClassified = new Bucket();

    }

    /**
     * Classifies a bucket using the exact match pipeline first then all other records not exact matched
     * are passed into the machine learning pipeline.
     *
     * @param predictionBucket the prediction bucket
     * @return the bucket with all the classified records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket predictionBucket, boolean multipleClassifications) throws IOException {

        LOGGER.info("prediction bucket size: " + predictionBucket.size());
        exactMatched = exactMatchPipeline.classify(predictionBucket);
        LOGGER.info("exactMatched bucket size: " + exactMatched.size());
        notExactMatched = BucketUtils.getComplement(predictionBucket, exactMatched);
        LOGGER.info("notExactMatched bucket size: " + notExactMatched.size());
        Assert.assertEquals(predictionBucket.size(), (notExactMatched.size() + exactMatched.size()));

        machineLearned = machineLearningClassifier.classify(notExactMatched, multipleClassifications);
        LOGGER.info("machineLearned bucket size: " + machineLearned.size());
        Assert.assertEquals(machineLearned.size(), notExactMatched.size());
        allClassified = BucketUtils.getUnion(machineLearned, exactMatched);
        LOGGER.info("allClassified bucket size: " + allClassified.size());
        Assert.assertEquals(predictionBucket.size(), allClassified.size());
        return allClassified;
    }

    /**
     * Gets the bucket of exact matched records.
     *
     * @return the exact matched
     */
    public Bucket getExactMatched() {

        return exactMatched;
    }

    /**
     * Gets the bucket of machine learned records.
     *
     * @return the machine learned
     */
    public Bucket getMachineLearned() {

        return machineLearned;
    }

    /**
     * Gets all the  classified records.
     *
     * @return the all classified
     */
    public Bucket getAllClassified() {

        return allClassified;
    }

}
