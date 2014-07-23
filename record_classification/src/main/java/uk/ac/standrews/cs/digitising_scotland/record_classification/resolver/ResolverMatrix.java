package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * ResolverMatrix stores mappings between {@link Code}s and the list of {@link Pair}<TokenSet, Double> objects
 * that were classified as that code.
 * <p>
 * It implements methods that allow  for the reduction in size of the lists for each code via removing overlapping
 * codes and low confidence codes.
 * <p>
 * It also implements the getValidCodeTriples() method that allows the user to find all Code->Pair<TokenSet, Double>
 * mappings that are valid with respect to a given power set.
 *
 * @author frjd2
 * @author jkc25
 *         Created by fraserdunlop on 10/06/2014 at 14:57.
 */
public class ResolverMatrix {

    /**
     * The Code, List<Pair> matrix.
     */
    private Map<Code, List<CodeTriple>> matrix;

    /**
     * Instantiates a new empty resolver matrix.
     */
    public ResolverMatrix() {

        matrix = new HashMap<>();
    }

    /**
     * Adds a {@link TokenSet} and {@link Pair}<Code, Double> to the matrix.
     * <p>
     * The Pair<Code, Double> should represent the output of an {@link AbstractClassifier} where the Code is the
     * returned code and the Double represents the confidence of that classification.
     *
     * @param tokenSet       the tokenSet to add
     * @param codeDoublePair the Pair<Code, Double> to add
     */
    public void add(final TokenSet tokenSet, final Pair<Code, Double> codeDoublePair) {

        Code code = codeDoublePair.getLeft();
        Double confidence = codeDoublePair.getRight();
        if (matrix.get(code) == null) {
            matrix.put(code, new ArrayList<CodeTriple>());
        }
        matrix.get(code).add(new CodeTriple(code, tokenSet, confidence));
    }

    /**
     * Gets the valid sets of {@link CodeTriple}s from the matrix in the form of a List.
     * <p>
     * A {@link CodeTriple} is defined as being valid if the union of the Set of TokenSets is a subset of the specified powerSet.
     * Any null entries in the matrix are not returned.
     *
     * @param originalSet the power set of valid tokenSets
     * @return List<Set<CodeTriple>> the List of Sets of valid {@link CodeTriple}s
     */
    public List<Set<CodeTriple>> getValidCodeTriples(final TokenSet originalSet) {

        chopUntilComplexityWithinBound(10000);
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
     */
    public int complexity() {

        int complexity = 1;
        for (Code code : matrix.keySet()) {
            if (matrix.get(code).size() > 0) {
                complexity = complexity * matrix.get(code).size();
            }
        }
        if (complexity < 1) {
            complexity = Integer.MAX_VALUE;
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

        for (Code code : keySet) {
            Code anscestor = ResolverUtils.whichCodeIsAncestorOfCodeInSet(code, keySet);
            if (anscestor != null) {
                matrix.get(code).addAll(matrix.get(anscestor));
                matrix.remove(anscestor);
            }
        }
    }

    /**
     * Helper method used to enumerate all values in the matrix.
     *
     * @param merged      the merged
     * @param codeTriples the pairs
     * @param code        the code
     */
    private void merge(final List<Set<CodeTriple>> merged, final List<CodeTriple> codeTriples, final Code code, final TokenSet originalSet) {

        List<Set<CodeTriple>> temporaryMerge = new ArrayList<>();
        for (Set<CodeTriple> tripleSet : merged) {
            for (CodeTriple triple : codeTriples) {
                CodeTriple tempCodeTriple = new CodeTriple(code, triple.getTokenSet(), triple.getConfidence());
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

        int maxNoOfEachCode = (int) Math.pow(bound, (1. / (double) matrix.keySet().size()));
        maxNoOfEachCode = Math.max(6, maxNoOfEachCode);
        for (Code code : matrix.keySet()) {
            Collections.sort(matrix.get(code), new CodeTripleComparator());
            matrix.put(code, matrix.get(code).subList(0, Math.min(matrix.get(code).size(), maxNoOfEachCode)));
        }
    }

    private static class CodeTripleComparator implements Comparator<CodeTriple> {

        @Override
        public int compare(final CodeTriple o1, final CodeTriple o2) {

            double measure1 = o1.getTokenSet().size() * o1.getConfidence();
            double measure2 = o2.getTokenSet().size() * o2.getConfidence();
            if (measure1 < measure2) {
                return 1;
            }
            else if (measure1 > measure2) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    /**
     * Removes from the matrix any Pair<Code, Double> where the confidence value is lower than the confidence specified.
     *
     * @param confidence the confidence threshold
     */
    public void chopBelowConfidence(final Double confidence) {

        for (Code code : matrix.keySet()) {
            List<CodeTriple> oldList = matrix.get(code);
            List<CodeTriple> newList = new ArrayList<>();
            for (CodeTriple triple : oldList) {
                if (triple.getConfidence() >= confidence) {
                    newList.add(triple);
                }
            }
            matrix.put(code, newList);
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ResolverMatrix other = (ResolverMatrix) obj;
        if (matrix == null) {
            if (other.matrix != null) { return false; }
        }
        else if (!matrix.equals(other.matrix)) { return false; }
        return true;
    }

    @Override
    public String toString() {

        return "ResolverMatrix [matrix=" + matrix + "]";
    }

}
