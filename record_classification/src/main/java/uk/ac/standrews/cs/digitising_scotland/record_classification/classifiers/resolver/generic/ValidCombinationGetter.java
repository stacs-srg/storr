/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.ValidityAssessor;

/**
 * Utility class for function which gets a List of all valid Sets of values from a MultiValueMap
 * given a ValidityCriterion. Values associated with the same key are not considered allowable combinations.
 * Created by fraserdunlop on 06/10/2014 at 09:45.
 */
public class ValidCombinationGetter<K, V, ValidityCriterion, P_ValidityAssessor extends ValidityAssessor<Multiset<V>, ValidityCriterion>> {

    private final P_ValidityAssessor validityAssessor;

    public ValidCombinationGetter(final P_ValidityAssessor validityAssessor) {

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
    public List<Multiset<V>> getValidSets(final MultiValueMap<K, V> map, final ValidityCriterion validityCriterion) throws Exception {

        List<Multiset<V>> validSets;
        validSets = calculateValidSets(map, validityCriterion);
        return validSets;
    }

    /**
     * Sets up the recursive function for calculating all valid sets.
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    private List<Multiset<V>> calculateValidSets(final MultiValueMap<K, V> map, final ValidityCriterion validityCriterion) {

        List<Multiset<V>> validSets = new ArrayList<>();
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
    private List<Multiset<V>> recursiveMerge(final List<Multiset<V>> validSets, final MultiValueMap<K, V> map, final Iterator<K> iterator, final ValidityCriterion validityCriterion) {

        if (iterator.hasNext()) {
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
    private void mergeStep(final List<Multiset<V>> validSets, final MultiValueMap<K, V> map, final ValidityCriterion validityCriterion, final K k) {

        List<V> kValues = map.get(k);
        List<Multiset<V>> tempList = new ArrayList<>();
        for (Multiset<V> set : validSets) {
            for (V kValue : kValues) {
                Multiset<V> tempSet = copyOfUnion(set, kValue);
                if (tempSetIsValid(validityCriterion, tempSet)) {
                    tempList.add(tempSet);
                }
            }
        }
        validSets.addAll(tempList);
    }

    /**
     * Purely for readability of mergeStep method.
     */
    private boolean tempSetIsValid(final ValidityCriterion validityCriterion, final Multiset<V> tempSet) {

        return validityAssessor.assess(tempSet, validityCriterion);
    }

    /**
     * Returns a new Set<V> containing union of set and v.
     * @param set set
     * @param v value
     * @return new set - union of set and v
     */
    private Multiset<V> copyOfUnion(final Multiset<V> set, final V v) {

        Multiset<V> tempSet = HashMultiset.create();
        if (set != null) {
            tempSet.addAll(set);
        }
        tempSet.add(v);
        return tempSet;
    }

}
