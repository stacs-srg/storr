package uk.ac.standrews.cs.digitising_scotland.parser.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;

import com.google.common.collect.Multiset;

/**
 * ResolverMatrix stores mappings between {@link Code}s and the list of {@link Pair}<TokenSet, Double> objects
 * that were classified as that code.
 * 
 * It implements methods that allow  for the reduction in size of the lists for each code via removing overlapping
 * codes and low confidence codes.
 * 
 * It also implements the getValidCodeTriples() method that allows the user to find all Code->Pair<TokenSet, Double>
 * mappings that are valid with respect to a given power set.
 * 
 * @author frjd2
 * @author jkc25
 * Created by fraserdunlop on 10/06/2014 at 14:57.
 */
public class ResolverMatrix {

    /** The Code, List<Pair> matrix. */
    private Map<Code, List<Pair<TokenSet, Double>>> matrix;

    /**
     * Instantiates a new empty resolver matrix.
     */
    public ResolverMatrix() {

        matrix = new HashMap<>();
    }

    /**
     * Adds a {@link TokenSet} and {@link Pair}<Code, Double> to the matrix.
     * 
     * The Pair<Code, Double> should represent the output of an {@link AbstractClassifier} where the Code is the
     * returned code and the Double represents the confidence of that classification.
     *
     * @param tokenSet the tokenSet to add
     * @param codeDoublePair the Pair<Code, Double> to add
     */
    public void add(final TokenSet tokenSet, final Pair<Code, Double> codeDoublePair) {

        Code code = codeDoublePair.getLeft();
        Double confidence = codeDoublePair.getRight();
        if (matrix.get(code) == null) {
            matrix.put(code, new ArrayList<Pair<TokenSet, Double>>());
        }
        matrix.get(code).add(new Pair<>(tokenSet, confidence));
    }

    /**
     * Gets the valid sets of {@link CodeTriple}s from the matrix in the form of a List.
     * 
     * A {@link CodeTriple} is defined as being valid if the union of the Set of TokenSets is a subset of the specified powerSet.
     * Any null entries in the matrix are not returned.
     * 
     * @param originalSet the power set of valid tokenSets
     * @return List<Set<CodeTriple>> the List of Sets of valid {@link CodeTriple}s
     */
    public List<Set<CodeTriple>> getValidCodeTriples(final TokenSet originalSet) {

        resolveHierarchies();
        List<Set<CodeTriple>> merged = new ArrayList<>();
        merged.add(null);
        for (Code code : matrix.keySet()) {
            merge(merged, matrix.get(code), code, originalSet);
        }
        merged.remove(null);
        return merged;
    }

    /**
     * Calculates a numerical value for the complexity of the matrix.
     * This value is calculated by multiplying the size of each list by the size of every other list.
     * For example, a Map with 3 different codes, with lists of length 4, 5 and 6 would result in a complexity of
     * 120 being returned (4*5*6).
     * 
     * @return the int numerical representation of the complexity of the matrix.
     * 
     */
    public int complexity() {

        int complexity = 1;
        for (Code code : matrix.keySet()) {
            complexity = complexity * matrix.get(code).size();
        }
        return complexity;
    }

    /**
     * Resolves hierarchies in the matrix by removing the ancestors of {@link Code}s in the matrix.
     * This utilises the ResolverUtils.removeAncestors() method to achieve this.
     */
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

    /**
     * Helper method used to enumerate all values in the matrix.
     *
     * @param merged the merged
     * @param pairs the pairs
     * @param code the code
     */
    private void merge(final List<Set<CodeTriple>> merged, final List<Pair<TokenSet, Double>> pairs, final Code code, final TokenSet originalSet) {

        List<Set<CodeTriple>> temporaryMerge = new ArrayList<>();
        for (Set<CodeTriple> tripleSet : merged) {
            for (Pair<TokenSet, Double> pair : pairs) {
                CodeTriple tempCodeTriple = new CodeTriple(code, pair.getLeft(), pair.getRight());
                Set<CodeTriple> tempTripleSet = new HashSet<>();
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

    /**
     * Chop until complexity within bound.
     *
     * @param bound the bound
     */
    public void chopUntilComplexityWithinBound(final int bound) {

        //TODO chop in some way removing poor confidence pairs to get complexity within bound
    }

    /**
     * Removes from the matrix any Pair<Code, Double> where the confidence value is lower than the confidence specified.
     *
     * @param confidence the confidence threshold
     */
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
