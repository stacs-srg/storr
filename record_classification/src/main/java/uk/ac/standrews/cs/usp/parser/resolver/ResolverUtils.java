package uk.ac.standrews.cs.usp.parser.resolver;

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

import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 *
 * Created by fraserdunlop on 10/06/2014 at 14:02.
 */
public class ResolverUtils {

    static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {

        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {

                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Set<CodeTriple> getBest(List<Set<CodeTriple>> triples) {

        Map<Set<CodeTriple>, Double> map = mapCodeTripleSetsToLoss(triples);
        map = sortByValue(map);
        return map.keySet().iterator().next();
    }

    public static Map<Set<CodeTriple>, Double> mapCodeTripleSetsToLoss(List<Set<CodeTriple>> triples) {

        Map<Set<CodeTriple>, Double> map = new HashMap<>();
        for (Set<CodeTriple> set : triples) {
            map.put(set, lossFunction(set));
        }
        return map;
    }

    public static Double lossFunction(Set<CodeTriple> set) {

        List<Double> confidences = new ArrayList<>();
        for (CodeTriple triple : set) {
            confidences.add(triple.getConfidence());
        }
        Double confidenceSum = 0.;
        for (Double conf : confidences) {
            confidenceSum += conf;
        }
        return confidenceSum;
    }

    public static boolean isValid(Multiset<TokenSet> tokenSetSet, Multiset<TokenSet> powerSet) {

        TokenSet tokenSet = getUnion(tokenSetSet);
        return powerSet.contains(tokenSet);
    }

    public static boolean tripleSetIsValid(Set<CodeTriple> triple, Multiset<TokenSet> powerSet) {

        return isValid(getTokenSetsFromTriple(triple), powerSet);
    }

    private static Multiset<TokenSet> getTokenSetsFromTriple(Set<CodeTriple> triples) {

        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        for (CodeTriple triple : triples) {
            tokenSetSet.add(triple.getTokenSet());
        }
        return tokenSetSet;
    }

    static TokenSet getUnion(Multiset<TokenSet> tokenSetSet) {

        Multiset<String> union = HashMultiset.create();
        for (TokenSet tokenSet : tokenSetSet) {
            for (String token : tokenSet) {
                union.add(token);
            }
        }
        return new TokenSet(union);
    }

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

    static boolean codeIsAncestorOfCodeInSet(final Code code, final Set<Code> codes) {

        for (Code c : codes) {
            if (code.isAncestor(c)) { return true; }
        }
        return false;
    }

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
