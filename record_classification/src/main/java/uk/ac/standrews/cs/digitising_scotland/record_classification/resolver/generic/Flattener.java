package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IFlattener;

import java.io.IOException;

/**
 * Flattens a MultiValueMap such that all of the Values are migrated to List
 * associated with one key. The rest of the keys are discarded. The key
 * that is kept in the map is the first to be returned by the iterator.
 * Created by fraserdunlop on 07/10/2014 at 09:42.
 */
public class Flattener<K, V> implements IFlattener<K,V> {

    /**
     * Migrates all Values into key K. All other keys are removed from the map.
     * @param map MultiValueMap.
     * @param key Key to migrate values to.
     */
    @Override
    public   MultiValueMap<K, V> moveAllIntoKey(MultiValueMap<K,V> map, K key) throws IOException, ClassNotFoundException {
        MultiValueMap<K, V> clone = map.deepClone();
        for(K k : map){
            if(clone.containsKey(k)) {
                if (k != key) {
                    clone.get(key).addAll(clone.get(k));
                    clone.remove(k);
                }
            }
        }
        return clone;
    }
}
