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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The V.compareTo(Threshold) method implemented by V is used for comparing values V with a threshold.
 * Values which fall below this threshold are removed from the MultiValueMap.
 * Created by fraserdunlop on 06/10/2014 at 14:21.
 */
public class BelowThresholdRemover<K, V extends Comparable<Threshold>, Threshold> {

    private final Threshold threshold;

    public BelowThresholdRemover(final Threshold threshold) {

        this.threshold = threshold;
    }

    /**
     * Removes from the map any value V which returns a compareTo
     * value less than one when compared to the threshold threshold.
     * @param map a MultiValueMap whose values extend Comparable<threshold>
     * @return a new MultiValueMap with values falling below threshold removed
     */
    public MultiValueMap<K, V> removeBelowThreshold(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {

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
