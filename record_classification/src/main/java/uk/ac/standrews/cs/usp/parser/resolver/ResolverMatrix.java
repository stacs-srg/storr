package uk.ac.standrews.cs.usp.parser.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;

import com.google.common.collect.Multiset;

/**
 *
 * Created by fraserdunlop on 10/06/2014 at 14:57.
 */
public class ResolverMatrix {

    Map<Code, List<Pair<TokenSet, Double>>> matrix;

    public ResolverMatrix() {

        matrix = new HashMap<>();
    }

    public void add(final TokenSet tokenSet, final Pair<Code, Double> codeDoublePair) {

        Code code = codeDoublePair.getLeft();
        Double confidence = codeDoublePair.getRight();
        if (matrix.get(code) == null) {
            matrix.put(code, new ArrayList<Pair<TokenSet, Double>>());
        }
        matrix.get(code).add(new Pair<>(tokenSet, confidence));
    }

    public List<Set<CodeTriple>> getValidCodeTriples(final Multiset<TokenSet> powerSet) {

        resolveHierarchies();
        List<Set<CodeTriple>> merged = new ArrayList<>();
        merged.add(null);
        for (Code code : matrix.keySet()) {
            merge(merged, matrix.get(code), code, powerSet);
        }
        merged.remove(null);
        return merged;
    }

    public int complexity() {

        int complexity = 1;
        for (Code code : matrix.keySet()) {
            complexity = complexity * matrix.get(code).size();
        }
        return complexity;
    }

    protected void resolveHierarchies() {

        Set<Code> keySet = new HashSet<>();
        keySet.addAll(matrix.keySet());
        Set<Code> resolved = ResolverUtils.removeAncestors(keySet);
        for (Code code : keySet) {
            if (!resolved.contains(code)) {
                matrix.remove(code);
            }
        }
    }

    private void merge(final List<Set<CodeTriple>> merged, final List<Pair<TokenSet, Double>> pairs, final Code code, final Multiset<TokenSet> powerSet) {

        List<Set<CodeTriple>> temporaryMerge = new ArrayList<>();
        for (Set<CodeTriple> tripleSet : merged) {
            for (Pair<TokenSet, Double> pair : pairs) {
                CodeTriple tempCodeTriple = new CodeTriple(code, pair.getLeft(), pair.getRight());
                Set<CodeTriple> tempTripleSet = new HashSet<>();
                if (tripleSet != null) {
                    tempTripleSet.addAll(tripleSet);
                }
                tempTripleSet.add(tempCodeTriple);
                if (ResolverUtils.tripleSetIsValid(tempTripleSet, powerSet)) {
                    temporaryMerge.add(tempTripleSet);
                }
            }
        }
        merged.addAll(temporaryMerge);
    }

    public void chopUntilComplexityWithinBound(final int bound) {

        //TODO chop in some way removing poor confidence pairs to get complexity within bound
    }

    public void chopBelowConfidence(final Double confidence) {

        for (Code code : matrix.keySet()) {
            List<Pair<TokenSet, Double>> oldList = matrix.get(code);
            List<Pair<TokenSet, Double>> newList = new ArrayList<>();
            for (Pair<TokenSet, Double> pair : oldList) {
                if (pair.getRight() >= confidence) {
                    newList.add(pair);
                }
            }
            matrix.put(code, newList);
        }
    }
}
