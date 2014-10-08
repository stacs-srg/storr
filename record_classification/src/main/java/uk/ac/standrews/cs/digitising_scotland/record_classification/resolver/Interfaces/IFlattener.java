package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;

import java.io.IOException;

/**
 * Flattens a MultiValueMap such that all of the Values are migrated to List
 * associated with one key. The rest of the keys are discarded.
 * Created by fraserdunlop on 08/10/2014 at 14:50.
 */
public interface IFlattener<K, V> {

    /**
     * Migrates all Values into key K. All other keys are removed from the map.
     * @param map MultiValueMap.
     * @param key Key to migrate values to.
     */
    public MultiValueMap<K, V> moveAllIntoKey(MultiValueMap<K,V> map, K key) throws IOException, ClassNotFoundException;
}
