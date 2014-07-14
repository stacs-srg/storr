package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /** The Constant WORDLIMIT. */
    private static final int WORDLIMIT = 1;

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
     * @param classifier            {@link AbstractClassifier} used for machine learning
     *            classification.
     * @param trainingBucket the training bucket
     */
    public MachineLearningClassificationPipeline(final AbstractClassifier classifier, Bucket trainingBucket) {

        this.cache = new TokenClassificationCache(classifier);
        recordCache = new HashMap<>();
        prepopulateCache(trainingBucket);
    }

    /**
     * Classify all records in a bucket.
     *
     * @param bucket the bucket to classifiy
     * @return bucket this is the bucket of classified records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket classified = new Bucket();

        for (Record record : bucket) {
            Set<CodeTriple> result = recordCache.get(record.getCleanedDescription());
            if (result == null) {
                result = classify(record);
                if (result != null) {
                    record.addAllCodeTriples(result);
                    classified.addRecordToBucket(record);
                }
                recordCache.put(record.getCleanedDescription(), record.getCodeTriples());
            }
        }

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
        if (cleanedTokenSet.size() < WORDLIMIT) {
            Multiset<TokenSet> powerSet = ResolverUtils.powerSet(cleanedTokenSet);
            powerSet.remove(new TokenSet("")); // remove empty token set
            populateMatrix(powerSet, resolverMatrix);
        }
        else {
            NGramSubstrings ngs = new NGramSubstrings(cleanedTokenSet);
            Multiset<TokenSet> ngramSet = ngs.getGramMultiset();
            populateMatrix(ngramSet, resolverMatrix);

        }

        resolverMatrix.chopBelowConfidence(CONFIDENCE_CHOP_LEVEL);
        List<Set<CodeTriple>> triples = resolverMatrix.getValidCodeTriples(cleanedTokenSet);

        Set<CodeTriple> best;
        if (triples.size() > 0) {
            best = ResolverUtils.getBest(triples);
        }
        else {
            best = new HashSet<>();
        }

        return best;
    }

    /**
     * Populate matrix.
     *
     * @param tokenSetSet the token set set
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
    private void prepopulateCache(final Bucket trainingBucket) {

        for (Record record : trainingBucket) {
            List<CodeTriple> singles = getSinglyCodedTripes(record);
            cache.addAll(singles);
        }

    }

    /**
     * Gets the singly coded tripes.
     *
     * @param record the record
     * @return the singly coded tripes
     */
    protected List<CodeTriple> getSinglyCodedTripes(final Record record) {

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
