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

/**
 * Flattens a MultiValueMap such that all of the Values are migrated to List
 * associated with one key. The rest of the keys are discarded. The key
 * that is kept in the map is the first to be returned by the iterator.
 * Created by fraserdunlop on 07/10/2014 at 09:42.
 */
public class Flattener<K, V> {

    /**
     * Migrates all Values into key K. All other keys are removed from the map.
     * @param map MultiValueMap.
     * @param key Key to migrate values to.
     */
    public MultiValueMap<K, V> moveAllIntoKey(MultiValueMap<K, V> map, K key) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = map.deepClone();
        for (K k : map) {
            if (clone.containsKey(k)) {
                if (k != key) {
                    clone.get(key).addAll(clone.get(k));
                    clone.remove(k);
                }
            }
        }
        return clone;
    }
}
