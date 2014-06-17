package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.Pair;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.ResolverMatrix;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.ResolverUtils;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.TokenClassificationCache;

import com.google.common.collect.Multiset;

/**
 * This class is produces a set of {@link CodeTriple}s that represent the classification for a {@link Record}.
 * @author jkc25, frjd2
 *
 */
public class RecordClassificationPipeline {

    /**
     * WORDLIMIT sets the maximum number of tokens that a TokenSet can contain before attempting multiple classification becomes unfeasible.
     * Any TokenSets with more tokens than WORDLIMIT will be skipped and not classified.
     */
    private static final int WORDLIMIT = 7;

    private TokenClassificationCache cache;

    /**
     * Constructs a new {@link RecordClassificationPipeline} with the specified {@link AbstractClassifier} used
     * to perform the classification duties.
     * @param classifier {@link AbstractClassifier} used for machine learning classification.
     */
    public RecordClassificationPipeline(final AbstractClassifier classifier) {

        this.cache = new TokenClassificationCache(classifier);
    }

    /**
     * Returns the classification of a {@link Record} as a Set of {@link CodeTriple}.
     * @param record to classify
     * @return Set<CodeTriple> the classifications
     * @throws IOException indicates an I/O Error
     */
    public Set<CodeTriple> classify(final Record record) throws IOException {

        if (new TokenSet(record.getCleanedDescription()).size() < WORDLIMIT) {

            TokenSet cleanedTokenSet = new TokenSet(record.getCleanedDescription());
            Multiset<TokenSet> powerSet = ResolverUtils.powerSet(cleanedTokenSet);
            powerSet.remove(new TokenSet(""));

            ResolverMatrix resolverMatrix = new ResolverMatrix();
            for (TokenSet tokenSet : powerSet) {
                Pair<Code, Double> codeDoublePair = cache.getClassification(tokenSet);
                resolverMatrix.add(tokenSet, codeDoublePair);
            }

            resolverMatrix.chopBelowConfidence(0.3);
            List<Set<CodeTriple>> triples = resolverMatrix.getValidCodeTriples(powerSet);
            Set<CodeTriple> best;
            if (triples.size() > 0) {
                best = ResolverUtils.getBest(triples);
            }
            else {
                best = new HashSet<>();
            }

            return best;
        }
        else {
            System.err.println("Record skipped: Too long");
            return new HashSet<>();
        }
    }

}
