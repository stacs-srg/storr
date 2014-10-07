package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import java.io.IOException;

/**
 * Flattens a MultiValueMap such that all of the Values are migrated to List
 * associated with one key. The rest of the keys are discarded. The key
 * that is kept in the map is the first to be returned by the iterator.
 * Created by fraserdunlop on 07/10/2014 at 09:42.
 */
public class Flattener<K, V> {

    /**
     * Flattens a MultiValueMap. All of the values are migrated to the List associated
     * with the key K which the iterator returns first. All other keys are removed from the
     * map.
     * @param map a MultiValueMap
     * @return a new MultiValueMap with only one Key/Value Pair
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public MultiValueMap<K, V> flatten(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        K key = map.iterator().next();
        return moveAllIntoKey(map, key);
    }

    /**
     * Migrates all Values into key K. All other keys are removed from the map.
     * @param map MultiValueMap.
     * @param key Key to migrate values to.
     */
    private  MultiValueMap<K, V> moveAllIntoKey(MultiValueMap<K,V> map, K key) throws IOException, ClassNotFoundException {
        MultiValueMap<K, V> clone = map.deepClone();
        for(K k : map){
            if(clone.containsKey(k)) {
                clone.get(key).addAll(clone.get(k));
                if (k != key)
                    clone.remove(k);
            }
        }
        return clone;
    }
}
