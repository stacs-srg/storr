package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 *
 * This class implements the utility methods used when creating multiple classifications for TokenSets.
 *
 * Created by fraserdunlop on 10/06/2014 at 14:02.
 * @author jkc25
 * @author frjd2
 */
public final class ResolverUtils {

    private static LossFunction lossFunction = new SumLossFunction();

    private ResolverUtils() {

        //Utility class -  private constructor
    }

    /**
     * Sorts a Map<K,V> by it's values in descending order.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to sort
     * @return the sorted map
     */
    static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {

        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {

                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Given a List of Sets of {@link CodeTriple}s this method will return the set
     * with the best output from the loss function as defined by the lossFunction() method.
     *
     * @param triples the list of sets of {@link CodeTriple}s to evaluate with the loss function
     * @return the set of {@link CodeTriple}s with the best return from the lossFunction method
     */
    public static Set<CodeTriple> getBest(final List<Set<CodeTriple>> triples) {

        Map<Set<CodeTriple>, Double> map = mapCodeTripleSetsToLoss(triples);
        map = sortByValue(map);
        return map.keySet().iterator().next();
    }

    /**
     * Creates a Map of Sets of code triples to their loss as defined by the lossFunction() method.
     *
     * @param triples the triples to evaluate
     * @return the map of sets to loss values
     */
    public static Map<Set<CodeTriple>, Double> mapCodeTripleSetsToLoss(final List<Set<CodeTriple>> triples) {

        Map<Set<CodeTriple>, Double> map = new HashMap<>();
        for (Set<CodeTriple> set : triples) {
            map.put(set, lossFunction(set));
        }
        return map;
    }

    /**
     * The loss function.
     * This is currently a very simple function and can easily be replaced by something more complex.
     * Current behaviour is defined as summing all the individual confidences in the set and returning the total.
     *
     * @param set the set to evaluate
     * @return the double loss value
     */
    public static double lossFunction(final Set<CodeTriple> set) {

        return lossFunction.calculate(set);
    }

    /**
     * Checks if the union of a {@link Multiset} of {@link TokenSet}s is a member of powerSet.
     *
     * @param tokenSetSet a Multiset of TokenSets
     * @param powerSet the power set of the token set from the original data
     * @return true, if valid subset, false otherwise
     */
    public static boolean isValid(final Multiset<TokenSet> tokenSetSet, final Multiset<TokenSet> powerSet) {

        TokenSet tokenSet = getUnion(tokenSetSet);
        return powerSet.contains(tokenSet);
    }

    /**
     * Checks if the union of the token sets from a set of {@link CodeTriple}s
     * is a a member of powerSet.
     *
     * @param triple a set of {@link CodeTriple}s
     * @param originalTokenSet the token set from the original data
     * @return true, if successful
     */
    public static boolean tripleSetIsValid(final Set<CodeTriple> triple, final TokenSet originalTokenSet) {

        TokenSet union = getUnion(getTokenSetsFromTriple(triple));
        return originalTokenSet.containsAll(union) && noTokenAppearsInUnionMoreOftenThanInOriginalSet(originalTokenSet, union);
    }

    private static boolean noTokenAppearsInUnionMoreOftenThanInOriginalSet(final TokenSet originalTokenSet, final TokenSet union) {

        for (String token : union) {
            TokenSet originalCopy = new TokenSet(originalTokenSet);
            TokenSet unionCopy = new TokenSet(union);
            originalCopy.retainAll(new TokenSet(token));
            unionCopy.retainAll(new TokenSet(token));
            if (unionCopy.size() > originalCopy.size()) { return false; }
        }
        return true;
    }

    /**
     * Creates a Multiset of the token sets belonging to a set of {@link CodeTriple}s.
     *
     * @param triples the triples
     * @return the token sets from triple
     */
    private static Multiset<TokenSet> getTokenSetsFromTriple(final Set<CodeTriple> triples) {

        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        for (CodeTriple triple : triples) {
            tokenSetSet.add(triple.getTokenSet());
        }
        return tokenSetSet;
    }

    /**
     * Gets the union of a multiset of tokenSets.
     *
     * @param tokenSetSet the tokenSet Multiset to create a union from
     * @return the union of all sets in the Multiset
     */
    static TokenSet getUnion(final Multiset<TokenSet> tokenSetSet) {

        Multiset<String> union = HashMultiset.create();
        for (TokenSet tokenSet : tokenSetSet) {
            for (String token : tokenSet) {
                union.add(token);
            }
        }
        return new TokenSet(union);
    }

    /**
     * Removes codes from the Set of codes which are ancestors of other codes in the set.
     *
     * @param codes the codes
     * @return the sets the
     */
    public static Set<Code> removeAncestors(final Set<Code> codes) {

        Set<Code> resolvedCodes = new HashSet<>();
        resolvedCodes.addAll(codes);
        for (Code code : codes) {
            if (codeIsAncestorOfCodeInSet(code, codes)) {
                resolvedCodes = removeClassificationsCodedTo(code.getCodeAsString(), resolvedCodes);
            }
        }
        return resolvedCodes;
    }

    /**
     * Removes the code corresponding to the string from the set of codes.
     *
     * @param code the code as a string
     * @param codes the set of codes
     * @return the set of codes with the code as string removed if present
     */
    static Set<Code> removeClassificationsCodedTo(final String code, final Set<Code> codes) {

        Set<Code> newList = new HashSet<>();
        newList.addAll(codes);
        for (Code c : codes) {
            if (code.equals(c.getCodeAsString())) {
                newList.remove(c);
            }
        }
        return newList;
    }

    /**
     * Evaluates true if code is an ancestor of one of the codes in the set of codes.
     *
     * @param code the code
     * @param codes the codes
     * @return true, if codes contains child of code
     */
    static boolean codeIsAncestorOfCodeInSet(final Code code, final Set<Code> codes) {

        for (Code c : codes) {
            if (code.isAncestor(c)) { return true; }
        }
        return false;
    }

    /**
     * Finds if the specified code is an anscetor of a code in the set of codes, and returns which one.
     *
     * @param code the code
     * @param codes the codes
     * @return true, if codes contains child of code
     */
    static Code whichCodeIsAncestorOfCodeInSet(final Code code, final Set<Code> codes) {

        for (Code c : codes) {
            if (code.isAncestor(c)) { return c; }
        }
        return null;
    }

    /**
     *  Calculates the power set of the original set.
     *
     * @param originalSet the original set {@link TokenSet}
     * @return Multiset of {@link TokenSet}s power set
     */
    public static Multiset<TokenSet> powerSet(final TokenSet originalSet) {

        Multiset<TokenSet> sets = HashMultiset.create();
        if (originalSet.isEmpty()) {
            sets.add(new TokenSet());
            return sets;
        }
        List<String> list = new ArrayList<>(originalSet);
        String head = list.get(0);
        TokenSet rest = new TokenSet();
        rest.addAll(list.subList(1, list.size()));
        for (TokenSet set : powerSet(rest)) {
            TokenSet newSet = new TokenSet();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

}
