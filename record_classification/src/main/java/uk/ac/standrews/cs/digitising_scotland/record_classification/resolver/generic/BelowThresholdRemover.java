package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IBelowThresholdRemover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The V.compareTo(Threshold) method implemented by V is used for comparing values V with a threshold.
 * Values which fall below this threshold are removed from the MultiValueMap.
 * Created by fraserdunlop on 06/10/2014 at 14:21.
 */
public class BelowThresholdRemover<K, V extends Comparable<Threshold>, Threshold> implements IBelowThresholdRemover<K, V, Threshold> {

    private final Threshold threshold;

    public BelowThresholdRemover(final Threshold threshold){
        this.threshold = threshold;
    }

    /**
     * Removes from the map any value V which returns a compareTo
     * value less than one when compared to the threshold threshold
     * @param map a MultiValueMap whose values extend Comparable<threshold>
     * @return a new MultiValueMap with values falling below threshold removed
     */
    @Override
    public MultiValueMap<K, V> removeBelowThreshold(final MultiValueMap<K, V> map)
                                                            throws IOException, ClassNotFoundException {
        MultiValueMap<K, V> clone = map.deepClone();
        for (K k : clone) {
            List<V> oldList = clone.get(k);
            List<V> newList = new ArrayList<>();
            for (V v : oldList) {
                if (v.compareTo(threshold)>=0)
                    newList.add(v);
            }
            clone.put(k, newList);
        }
        return clone;
    }
}
