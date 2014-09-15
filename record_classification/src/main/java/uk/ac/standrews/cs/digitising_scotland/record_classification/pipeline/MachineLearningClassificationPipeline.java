package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenClassificationCache;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverUtils;

import com.google.common.collect.Multiset;

/**
 * This class is produces a set of {@link CodeTriple}s that represent the
 * classification for a {@link Record}.
 * 
 * @author jkc25, frjd2
 * 
 */
public class MachineLearningClassificationPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineLearningClassificationPipeline.class);

    /** The Constant CONFIDENCE_CHOP_LEVEL. */
    private static final double CONFIDENCE_CHOP_LEVEL = 0.3;

    /** The cache. */
    private TokenClassificationCache cache;

    /** The record cache. */
    private Map<String, Set<CodeTriple>> recordCache;

    /**
     * Constructs a new {@link MachineLearningClassificationPipeline} with the specified
     * {@link AbstractClassifier} used to perform the classification duties.
     *
     * @param classifier    {@link AbstractClassifier} used for machine learning classification
     * @param trainingBucket the training bucket
     */
    public MachineLearningClassificationPipeline(final AbstractClassifier classifier, final Bucket trainingBucket) {

        this.cache = new TokenClassificationCache(classifier);
        recordCache = new HashMap<>();
        prePopulateCache(trainingBucket);
    }

    /**
     * Classify all records in a bucket.
     *
     * @param bucket the bucket to classifiy
     * @return bucket this is the bucket of classified records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        int count = 0;
        Bucket classified = new Bucket();

        for (Record record : bucket) {
            LOGGER.info("Classifying record " + count + " of " + bucket.size() + " Description: " + record.getDescription());
            classifyRecordAddToBucket(record, classified);
            count++;
        }

        return classified;
    }

    private void classifyRecordAddToBucket(final Record record, final Bucket classified) throws IOException {

        for (String description : record.getDescription()) {
            if (!previouslyClassified(record, description)) {
                Set<CodeTriple> result = recordCache.get(description);
                if (result == null) {
                    result = classify(description);
                    if (result != null) {
                        addResultToRecord(record, description, result);
                        classified.addRecordToBucket(record);
                    }
                    recordCache.put(description, record.getCodeTriples());
                }
            }
        }
    }

    private void addResultToRecord(final Record record, final String description, final Set<CodeTriple> result) {

        record.addAllCodeTriples(result);
        addCodeTriplesAndDescriptions(record, description, result);
    }

    private boolean previouslyClassified(final Record record, final String description) {

        return record.getListOfClassifications().containsKey(description);
    }

    private void addCodeTriplesAndDescriptions(final Record record, final String desc, final Set<CodeTriple> result) {

        for (CodeTriple codeTriple : result) {
            record.getListOfClassifications().put(desc, codeTriple);
        }
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
    public Set<CodeTriple> classify(final String description) throws IOException {

        TokenSet cleanedTokenSet = new TokenSet(description);

        return classifyTokenSet(cleanedTokenSet);

    }

    /**
     * Classify token set.
     *
     * @param cleanedTokenSet the cleaned token set
     * @return the sets the
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Set<CodeTriple> classifyTokenSet(final TokenSet cleanedTokenSet) throws IOException {

        ResolverMatrix resolverMatrix = new ResolverMatrix();

        NGramSubstrings ngs = new NGramSubstrings(cleanedTokenSet);
        Multiset<TokenSet> ngramSet = ngs.getGramMultiset();
        populateMatrix(ngramSet, resolverMatrix);

        resolverMatrix.chopBelowConfidence(CONFIDENCE_CHOP_LEVEL);

        List<Set<CodeTriple>> triples = resolverMatrix.getValidCodeTriples(cleanedTokenSet);
        Set<CodeTriple> best;

        if (!triples.isEmpty()) {
            best = ResolverUtils.getBest(triples);
        }
        else {
            best = new HashSet<>();
        }

        return best;
    }

    /**
     * Populates the resolver matrix.
     *
     * @param tokenSetSet the tokenSet set
     * @param resolverMatrix the resolver matrix
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void populateMatrix(final Multiset<TokenSet> tokenSetSet, final ResolverMatrix resolverMatrix) throws IOException {

        for (TokenSet tokenSet : tokenSetSet) {
            Pair<Code, Double> codeDoublePair = cache.getClassification(tokenSet);
            resolverMatrix.add(tokenSet, codeDoublePair);
        }
    }

    /**
     * Prepopulate cache.
     *
     * @param trainingBucket the training bucket
     */
    private void prePopulateCache(final Bucket trainingBucket) {

        for (Record record : trainingBucket) {
            List<CodeTriple> singles = getSinglyCodedTriples(record);
            cache.addAll(singles);
        }

    }

    /**
     * Gets the singly coded triples, that is codeTriples that have only one coding.
     *
     * @param record the record to get single triples from
     * @return the singly coded triples
     */
    protected List<CodeTriple> getSinglyCodedTriples(final Record record) {

        List<CodeTriple> singles = new ArrayList<>();

        final Set<CodeTriple> goldStandardClassificationSet = record.getGoldStandardClassificationSet();
        for (CodeTriple codeTriple1 : goldStandardClassificationSet) {
            int count = 0;
            for (CodeTriple codeTriple2 : goldStandardClassificationSet) {
                if (codeTriple1.getTokenSet().equals(codeTriple2.getTokenSet())) {
                    count++;
                }
            }
            if (count == 1) {
                singles.add(codeTriple1);
            }
        }

        return singles;
    }

}
