package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO test
 * An extension of the Java Collections map interface with a method
 * for getting the value of the closest matching key according to a
 * similarity metric supplied at construction.
 * Created by fraserdunlop on 01/10/2014 at 15:38.
 */
public class ClosestMatchMap<K, V> implements Map<K, V> {

    private final Similaritor<K> similaritor;
    private Map<K, V> map;

    ClosestMatchMap(final SimilarityMetric<K> metric, final Map<K, V> map) {

        this.map = map;
        this.similaritor = new Similaritor<>(metric);
    }

    /**
     * WARNING: make sure that the Similaritor supplied at construction
     * returns a Comparator which puts the most similar key at the head
     * of the list otherwise this will return the value associated with the
     * least similar key.
     * @param key the key to fetch.
     * @return the value of the key which matches the supplied key most closely.
     */
    public V getClosestMatch(final K key) {

        if (containsKey(key)) { return get(key); }
        return get(getClosestKey(key));
    }

    /**
     *
     * @param key used to construct comparator to sort keys.
     * @return the key at the head of the sorted list of keys.
     */
    private K getClosestKey(final K key) {

        List<K> keyList = new ArrayList<>(keySet());
        Collections.sort(keyList, similaritor.getComparator(key));
        return keyList.get(0);
    }

    @Override
    public int size() {

        return map.size();
    }

    @Override
    public boolean isEmpty() {

        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {

        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {

        return map.containsValue(value);
    }

    @Override
    public V get(final Object key) {

        return map.get(key);
    }

    @Override
    public V put(final K key, final V value) {

        return map.put(key, value);
    }

    @Override
    public V remove(final Object key) {

        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {

        map.putAll(m);
    }

    @Override
    public void clear() {

        map.clear();
    }

    @Override
    public Set<K> keySet() {

        return map.keySet();
    }

    @Override
    public Collection<V> values() {

        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        return map.entrySet();
    }
}
