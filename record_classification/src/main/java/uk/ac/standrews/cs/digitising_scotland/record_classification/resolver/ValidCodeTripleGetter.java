package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 09:45.
 */
public class ValidCodeTripleGetter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidCodeTripleGetter.class);
    private static final int KEYSET_SIZE_LIMIT = 30;

    /**
     * Gets the valid sets of {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification}s from the matrix in the form of a List.
     * <p>
     * A {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification} is defined as being valid if the union of the Set of TokenSets is a subset of the specified powerSet.
     * Any null entries in the matrix are not returned.
     * //TODO rename matrix
     * @param originalSet the power set of valid tokenSets
     * @return List<Set<CodeTriple>> the List of Sets of valid {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification}s
     */
    public List<Set<Classification>> getValidCodeTriples(final MultiValueMap<Code,Classification> matrix,
                                                         final TokenSet originalSet) {

        List<Set<Classification>> merged = new ArrayList<>();
        merged.add(null);

        if (matrix.size() > KEYSET_SIZE_LIMIT) {
            //TODO quick hack to test theory
            LOGGER.info("codeList too big - skipping");
        }
        else {
            //TODO refactor as a proper recursive functions
            for (Code code : matrix) {
                merge(merged, matrix.get(code), code, originalSet);
            }
        }
        merged.remove(null);
        return merged;
    }

    /**
     * Helper method used to enumerate all values in the matrix.
     *
     * @param merged      the merged
     * @param codeTriples the pairs
     * @param code        the code
     */
    protected void merge(final List<Set<Classification>> merged, final List<Classification> codeTriples, final Code code, final TokenSet originalSet) {

        List<Set<Classification>> temporaryMerge = new ArrayList<>();
        for (Set<Classification> tripleSet : merged) {
            for (Classification triple : codeTriples) {
                Classification tempCodeTriple = new Classification(code, triple.getTokenSet(), triple.getConfidence());
                Set<Classification> tempTripleSet = new HashSet<>();
                if (tripleSet != null) {
                    tempTripleSet.addAll(tripleSet);
                }
                tempTripleSet.add(tempCodeTriple);
                if (ResolverUtils.tripleSetIsValid(tempTripleSet, originalSet)) {
                    temporaryMerge.add(tempTripleSet);
                }
            }
        }
        merged.addAll(temporaryMerge);
    }
}
