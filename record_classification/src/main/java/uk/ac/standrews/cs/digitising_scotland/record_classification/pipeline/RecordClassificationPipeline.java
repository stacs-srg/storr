package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.TokenClassificationCache;

import com.google.common.collect.Multiset;

/**
 * This class is produces a set of {@link CodeTriple}s that represent the classification for a {@link Record}.
 * @author jkc25, frjd2
 *
 */
public class RecordClassificationPipeline {

    private static final int WORDLIMIT = 1;

    private TokenClassificationCache cache;
    private ExactMatchClassifier exactMatchClassifier;

    /**
     * Constructs a new {@link RecordClassificationPipeline} with the specified {@link AbstractClassifier} used
     * to perform the classification duties.
     * @param classifier {@link AbstractClassifier} used for machine learning classification.
     */
    public RecordClassificationPipeline(final AbstractClassifier classifier) {

        this.cache = new TokenClassificationCache(classifier);
        this.exactMatchClassifier = null;
    }

    /**
     * Constructs a new {@link RecordClassificationPipeline} with an {@link ExactMatchClassifier} used to
     * perform an exact match lookup before the specified {@link AbstractClassifier} is used
     * to perform the classification duties.
     * @param classifier {@link AbstractClassifier} used for machine learning classification
     * @param exactMatchClassifier {@link ExactMatchClassifier} used to carry out an exact match classification

     */
    public RecordClassificationPipeline(final AbstractClassifier classifier, final ExactMatchClassifier exactMatchClassifier) {

        this.cache = new TokenClassificationCache(classifier);
        this.exactMatchClassifier = exactMatchClassifier;
    }

    /**
     * Returns the classification of a {@link Record} as a Set of {@link CodeTriple}.
     * @param record to classify
     * @return Set<CodeTriple> the classifications
     * @throws IOException indicates an I/O Error
     */
    public Set<CodeTriple> classify(final Record record) throws IOException {

        TokenSet cleanedTokenSet = new TokenSet(record.getCleanedDescription());

        if (exactMatchClassifier != null) {
            Set<CodeTriple> exactMatchResult = exactMatchClassifier.classifyTokenSetToCodeTripleSet(cleanedTokenSet);
            if (exactMatchResult != null) { return exactMatchResult; }
        }
        return classifyTokenSet(cleanedTokenSet);

    }

    private Set<CodeTriple> classifyTokenSet(final TokenSet cleanedTokenSet) throws IOException {

        ResolverMatrix resolverMatrix = new ResolverMatrix();
        if (cleanedTokenSet.size() < WORDLIMIT) {
            Multiset<TokenSet> powerSet = ResolverUtils.powerSet(cleanedTokenSet);
            powerSet.remove(new TokenSet(""));
            populateMatrix(powerSet, resolverMatrix);
        }
        else {
            NGramSubstrings ngs = new NGramSubstrings(cleanedTokenSet);
            Multiset<TokenSet> ngramSet = ngs.getGramMultiset();
            populateMatrix(ngramSet, resolverMatrix);

        }

        resolverMatrix.chopBelowConfidence(0.3);
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

    private void populateMatrix(final Multiset<TokenSet> tokenSetSet, final ResolverMatrix resolverMatrix) throws IOException {

        for (TokenSet tokenSet : tokenSetSet) {
            Pair<Code, Double> codeDoublePair = cache.getClassification(tokenSet);
            resolverMatrix.add(tokenSet, codeDoublePair);
        }
    }

}
