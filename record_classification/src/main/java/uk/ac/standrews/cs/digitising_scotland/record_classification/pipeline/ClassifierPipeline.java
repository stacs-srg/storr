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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenClassificationCache;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.*;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools.*;

/**
 * This class is produces a set of {@link Classification}s that represent the
 * classification for a {@link Record}.
 * 
 * @author jkc25, frjd2
 * 
 */
public class ClassifierPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierPipeline.class);

    /** The Constant CONFIDENCE_CHOP_LEVEL. */
    private static final double CONFIDENCE_CHOP_LEVEL = 0.3;

    /** The cache. */
    private TokenClassificationCache cache;

    /** The record cache. */
    private Map<String, Set<Classification>> recordCache;

    /**
     * Constructs a new {@link ClassifierPipeline} with the specified
     * {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used to perform the classification duties.
     *
     * @param classifier    {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used for machine learning classification
     * @param cachePopulationBucket the training bucket
     */
    public ClassifierPipeline(final IClassifier classifier, final Bucket cachePopulationBucket) {

        this.cache = new TokenClassificationCache(classifier);
        recordCache = new HashMap<>();
        prePopulateCache(cachePopulationBucket);
    }

    /**
     * Classify all records in a bucket.
     *
     * @param bucket the bucket to classifiy
     * @return bucket this is the bucket of classified records
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket, final boolean multipleClassifications) throws IOException, ClassNotFoundException {

        int count = 0;
        Bucket classified = new Bucket();

        for (Record record : bucket) {
            classifyRecordAddToBucket(record, classified, multipleClassifications);
            count++;
        }

        return classified;
    }

    private void classifyRecordAddToBucket(final Record record, final Bucket classified, final boolean multipleClassifications) throws IOException, ClassNotFoundException {

        for (String description : record.getDescription()) {
            if (!previouslyClassified(record, description)) {
                Set<Classification> result = recordCache.get(description);
                if (result == null) {
                    getResultAddToCache(record, classified, description, multipleClassifications);
                }
                else {
                    addResultToRecord(record, description, result);
                    classified.addRecordToBucket(record);
                }
            }
        }
    }

    private void getResultAddToCache(final Record record, final Bucket classified, final String description, final boolean multipleClassifications) throws IOException, ClassNotFoundException {

        Set<Classification> result;
        result = classify(description, multipleClassifications);
        if (result != null) {
            addResultToRecord(record, description, result);
            classified.addRecordToBucket(record);
        }
        recordCache.put(description, record.getClassifications());
    }

    private void addResultToRecord(final Record record, final String description, final Set<Classification> result) {

        addCodeTriplesAndDescriptions(record, description, result);
    }

    private boolean previouslyClassified(final Record record, final String description) {

        return record.getListOfClassifications().containsKey(description);
    }

    private void addCodeTriplesAndDescriptions(final Record record, final String desc, final Set<Classification> result) {

        for (Classification codeTriple : result) {
            record.addClassification(desc, codeTriple);
        }
    }

    /**
     * Returns the classification of a {@link Record} as a Set of
     * {@link Classification}.
     * 
     * @param description
     *            the description to classify
     * @return Set<CodeTriple> the classifications
     * @throws IOException
     *             indicates an I/O Error
     */
    public Set<Classification> classify(final String description, final boolean multipleClassifications) throws IOException, ClassNotFoundException {

        TokenSet cleanedTokenSet = new TokenSet(description);

        return classifyTokenSet(cleanedTokenSet, multipleClassifications);

    }

    /**
     * Classify token set.
     *
     * @param cleanedTokenSet the cleaned token set
     * @return the sets the
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Set<Classification> classifyTokenSet(final TokenSet cleanedTokenSet, final boolean multipleClassifications) throws IOException, ClassNotFoundException {

        MultiValueMap<Code,Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code,List<Classification>>());

        NGramSubstrings ngs = new NGramSubstrings(cleanedTokenSet);
        Multiset<TokenSet> ngramSet = ngs.getGramMultiset();
        populateMatrix(ngramSet, multiValueMap);

        MultiValueMapPruner<Code,Classification,
                        ClassificationComparator> multiValueMapPruner
                = new MultiValueMapPruner<>(new ClassificationComparator());
        BelowThresholdRemover belowThresholdRemover = new BelowThresholdRemover();
        ValidCodeTripleGetter validCodeTripleGetter = new ValidCodeTripleGetter();
        HierarchyResolver hierarchyResolver = new HierarchyResolver();

        multiValueMap = belowThresholdRemover.removeBelowThreshold(multiValueMap,CONFIDENCE_CHOP_LEVEL);
        if(multipleClassifications) {
            multiValueMap = hierarchyResolver.moveAncestorsToDescendantKeys(multiValueMap);
        } else {
            multiValueMap = hierarchyResolver.flattenForSingleClassifications(multiValueMap);
        }
        multiValueMap = multiValueMapPruner.pruneUntilComplexityWithinBound(multiValueMap);

        List<Set<Classification>> triples = validCodeTripleGetter.getValidCodeTriples(multiValueMap,cleanedTokenSet);
        Set<Classification> best;

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
     * @param multiValueMap the resolver matrix
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void populateMatrix(final Multiset<TokenSet> tokenSetSet, final MultiValueMap<Code,Classification> multiValueMap) throws IOException {

        for (TokenSet tokenSet : tokenSetSet) {
            Pair<Code, Double> codeDoublePair = cache.getClassification(tokenSet);
            multiValueMap.add(codeDoublePair.getLeft(), new Classification(codeDoublePair.getLeft(),tokenSet,codeDoublePair.getRight()));
        }
    }

    /**
     * Prepopulate cache.
     *
     * @param trainingBucket the training bucket
     */
    private void prePopulateCache(final Bucket trainingBucket) {

        for (Record record : trainingBucket) {
            List<Classification> singles = getSinglyCodedTriples(record);
            cache.addAll(singles);
        }

    }

    /**
     * Gets the singly coded triples, that is codeTriples that have only one coding.
     *
     * @param record the record to get single triples from
     * @return the singly coded triples
     */
    protected List<Classification> getSinglyCodedTriples(final Record record) {

        List<Classification> singles = new ArrayList<>();

        final Set<Classification> goldStandardClassificationSet = record.getGoldStandardClassificationSet();
        for (Classification codeTriple1 : goldStandardClassificationSet) {
            int count = 0;
            for (Classification codeTriple2 : goldStandardClassificationSet) {
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
