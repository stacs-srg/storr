package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import java.util.*;

/**
 * Utility class for function which gets a List of all valid Sets of values from a MultiValueMap
 * given a ValidityCriterion and the assumption that values associated with the same key will not
 * be a valid combination. Values from the same key are not considered as combinations.
 * Created by fraserdunlop on 06/10/2014 at 09:45.
 */
public class ValidCombinationGetter<K, V, ValidityCriterion, P_ValidityAssessor extends ValidityAssessor<Set<V>, ValidityCriterion>> {

    private final P_ValidityAssessor validityAssessor;

    public ValidCombinationGetter(P_ValidityAssessor validityAssessor){
        this.validityAssessor = validityAssessor;
    }

    /**
     * Gets a List of all valid Sets of values from a MultiValueMap given a ValidityCriterion and the
     * assumption that values associated with the same key will not be a valid combination. Values from the
     * same key will not be considered as combinations.
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    public List<Set<V>> getValidSets(final MultiValueMap<K, V> map, final ValidityCriterion validityCriterion) throws Exception {
        List<Set<V>> validSets;
        validSets = calculateValidSets(map, validityCriterion);
        return validSets;
    }

    /**
     * Sets up the recursive function for calculating all valid sets.
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    private List<Set<V>> calculateValidSets(MultiValueMap<K, V> map, ValidityCriterion validityCriterion) {
        List<Set<V>> validSets = new ArrayList<>();
        validSets.add(null);
        validSets = recursiveMerge(validSets, map, map.iterator(), validityCriterion);
        validSets.remove(null);
        return validSets;
    }

    /**
     *
     * @param validSets List of known valid sets
     * @param map MultiValueMap to extract valid sets from
     * @param iterator iterator over keys in MultiValueMap incremented during recursion
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    private List<Set<V>> recursiveMerge(final List<Set<V>> validSets,
                                                     final MultiValueMap<K, V> map,
                                                     final Iterator<K> iterator, final ValidityCriterion validityCriterion) {
        if(iterator.hasNext()){
            K k = iterator.next();
            mergeStep(validSets, map, validityCriterion, k);
            recursiveMerge(validSets, map, iterator, validityCriterion);
        }
        return validSets;
    }


    /**
     * A 'merge step' in the recursion. We check all unions of values associated with K and sets in validSets.
     * If the union of a set from validSets and a value from k is valid according to the ValidityAssessor and
     * ValidityCriterion then it is added to validSets.
     * @param validSets List of known valid sets
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @param k the key whose values are being 'merged' into the sets of validSets
     */
    private void mergeStep(List<Set<V>> validSets, MultiValueMap<K, V> map, ValidityCriterion validityCriterion, K k) {
        List<V> kValues = map.get(k);
        List<Set<V>> tempList = new ArrayList<>();
        for (Set<V> set : validSets) {
            for (V kValue : kValues) {
                Set<V> tempSet = copyOfUnion(set, kValue);
                if (tempSetIsValid(validityCriterion, tempSet))
                    tempList.add(tempSet);
            }
        }
        validSets.addAll(tempList);
    }

    /**
     * Purely for readability of mergeStep method.
     */
    private boolean tempSetIsValid(ValidityCriterion validityCriterion, Set<V> tempSet) {
        return validityAssessor.assess(tempSet, validityCriterion);
    }

    /**
     * Returns a new Set<V> containing union of set and v
     * @param set set
     * @param v value
     * @return new set - union of set and v
     */
    private Set<V> copyOfUnion(Set<V> set, V v) {
        Set<V> tempSet = new HashSet<>();
        if(set !=null)
            tempSet.addAll(set);
        tempSet.add(v);
        return tempSet;
    }

}
