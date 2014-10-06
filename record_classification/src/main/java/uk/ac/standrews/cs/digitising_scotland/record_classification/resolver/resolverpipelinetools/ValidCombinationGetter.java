package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.MultiValueMap;
import java.util.*;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 09:45.
 */
public class ValidCombinationGetter<K, V, ValidityCriterion, P_ValidityAssessor extends ValidityAssessor<Set<V>, ValidityCriterion>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidCombinationGetter.class);
    private static final int KEYSET_SIZE_LIMIT = 30;
    private final P_ValidityAssessor validityAssessor;

    public ValidCombinationGetter(P_ValidityAssessor validityAssessor){
        this.validityAssessor = validityAssessor;
    }

    /**
     *
     */
    public List<Set<V>> getValidSets(final MultiValueMap<K, V> map, final ValidityCriterion validityCriterion) throws Exception {
        List<Set<V>> validSets;
        if (map.size() > KEYSET_SIZE_LIMIT) {
            throw new Exception("ValidCombinationGetter KEYSET_SIZE_LIMIT exceeded. KEYSET_SIZE_LIMIT set to: " + KEYSET_SIZE_LIMIT);
        }
        validSets = calculateValidSets(map, validityCriterion);
        return validSets;
    }

    private List<Set<V>> calculateValidSets(MultiValueMap<K, V> map, ValidityCriterion originalSet) {
        List<Set<V>> validSets = new ArrayList<>();
        validSets.add(null);
        validSets = recursiveMerge(validSets, map, map.iterator(), originalSet);
        validSets.remove(null);
        return validSets;
    }

    /**
     */
    private List<Set<V>> recursiveMerge(final List<Set<V>> validSets,
                                                     final MultiValueMap<K, V> map,
                                                     final Iterator<K> iterator, final ValidityCriterion originalSet) {
        if(iterator.hasNext()){
            K k = iterator.next();
            mergeStep(validSets, map, originalSet, k);
            recursiveMerge(validSets, map, iterator, originalSet);
        }
        return validSets;
    }

    private void mergeStep(List<Set<V>> validSets, MultiValueMap<K, V> map, ValidityCriterion originalSet, K k) {
        List<V> vs = map.get(k);
        List<Set<V>> temporaryMerge = new ArrayList<>();
        for (Set<V> set : validSets) {
            for (V v : vs) {
                Set<V> tempSet = new HashSet<>();
                if(set!=null)
                    tempSet.addAll(set);
                tempSet.add(v);
                if (validityAssessor.assess(tempSet, originalSet)) {
                    temporaryMerge.add(tempSet);
                }
            }
        }
        validSets.addAll(temporaryMerge);
    }
}
