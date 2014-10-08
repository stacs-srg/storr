package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenClassificationCache;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ResolverPipeline;

/**
 * This class is produces a set of {@link Classification}s that represent the
 * classification for a {@link Record}.
 * 
 * @author jkc25, frjd2
 * 
 */
public class ClassifierPipeline implements IPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierPipeline.class);

    /** The Constant CONFIDENCE_CHOP_LEVEL. */
    private static final double CONFIDENCE_CHOP_LEVEL = 0.3;

    /** The record cache. */
    private Map<String, Set<Classification>> recordCache;

    private ResolverPipeline resolverPipeline;

    /**
     * Constructs a new {@link ClassifierPipeline} with the specified
     * {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used to perform the classification duties.
     *
     * @param classifier    {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used for machine learning classification
     * @param cachePopulationBucket the training bucket
     */
    public ClassifierPipeline(final IClassifier classifier, final Bucket cachePopulationBucket, final boolean multipleClassifications) {

        /* The cache. */
        TokenClassificationCache cache = new TokenClassificationCache(classifier);
        recordCache = new HashMap<>();
        cache.prePopulate(cachePopulationBucket);
        this.resolverPipeline = new ResolverPipeline(cache, multipleClassifications, CONFIDENCE_CHOP_LEVEL);
    }

    /**
     * Classify all records in a bucket.
     *
     * @param bucket the bucket to classifiy
     * @return bucket this is the bucket of classified records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws Exception {

        Bucket classified = new Bucket();
        for (Record record : bucket) {
            Record recordToInsert = classifyRecord(record);
            classified.addRecordToBucket(recordToInsert);
        }
        return classified;
    }

    private Record classifyRecord(final Record record) throws Exception {

        for (String description : record.getDescription()) {
            if (!record.descriptionIsClassified(description)) {
                if (!getFromCache(record, description)) {
                    Set<Classification> result = resolverPipeline.classify(new TokenSet(description));
                    if (result != null) {
                        record.addClassificationsToDescription(description, result);
                        recordCache.put(description, result);
                    }
                }
            }
        }
        return record;
    }

    private boolean getFromCache(final Record record, final String description) {

        Set<Classification> result = recordCache.get(description);
        if (result == null) return false;
        record.addClassificationsToDescription(description, result);
        return true;
    }

    @Override
    public Bucket getSuccessfullyClassified() {

        // TODO Auto-generated method stub
        return null;
    }
}