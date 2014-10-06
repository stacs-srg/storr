package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.MultiValueMap;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 14:21.
 */
public class BelowThresholdRemover<K, V extends Comparable<Q>, Q> {

    /**
     * Removes from the map any value V which returns a compareTo
     * value less than one when compared to the threshold threshold
     * @param threshold the threshold threshold
     */
    public MultiValueMap<K, V> removeBelowThreshold(final MultiValueMap<K, V> map, final Q threshold) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = map.deepClone();
        for (K k : clone) {
            List<V> oldList = clone.get(k);
            List<V> newList = new ArrayList<>();
            for (V v : oldList) {
                if (v.compareTo(threshold) >= 0) {
                    newList.add(v);
                }
            }
            clone.put(k, newList);
        }
        return clone;
    }
}
