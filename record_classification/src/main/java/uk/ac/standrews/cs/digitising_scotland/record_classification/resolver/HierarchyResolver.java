package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 10:00.
 */
public class HierarchyResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyResolver.class);

    public  <K, V> MultiValueMap<K, V> flattenForSingleClassifications(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        MultiValueMap<K, V> clone = map.deepClone();
        K key = map.iterator().next();
        moveAllIntoKey(map, clone, key);
        return clone;
    }

    public <K extends AncestorAble<K>, V> MultiValueMap<K, V> moveAncestorsToDescendantKeys(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        MultiValueMap<K, V> clone = map.deepClone();
        for (K key : map)
            moveAncestorsIntoKey(map, clone, key);
        return clone;
    }

    private <K, V> void moveAllIntoKey(MultiValueMap<K,V> map, MultiValueMap<K, V> clone, K key) {
        for(K k : map){
            if(clone.containsKey(k)) {
                clone.get(key).addAll(clone.get(k));
                if (k != key)
                    clone.remove(k);
            }
        }
    }

    private <K extends AncestorAble<K>, V> void moveAncestorsIntoKey(MultiValueMap<K, V> matrix, MultiValueMap<K, V> clone, K decedentKey) {
        for(K ancestor : getAncestors(decedentKey, clone.keySet())) {
                clone.get(decedentKey).addAll(matrix.get(ancestor));
                clone.remove(ancestor);
        }
    }

    /**
     * Returns the set of ancestors of K k contained in Set<K> keys.
     * @param k the key
     * @param keys the keys
     * @return the ancestors of k.
     */
    private <K extends AncestorAble<K>> Set<K> getAncestors(final K k, final Set<K> keys) {
        Set<K> ancestors = new HashSet<>();
        for (K key : keys) {
            if (k.isAncestor(key)) { ancestors.add(key); }
        }
        return ancestors;
    }

}
