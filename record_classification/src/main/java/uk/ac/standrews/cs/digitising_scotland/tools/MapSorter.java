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
package uk.ac.standrews.cs.digitising_scotland.tools;

import java.util.*;

/**
 * Map sorter tool.
 * Created by fraserdunlop on 07/10/2014 at 16:31.
 */
public class MapSorter {

    /**
     * Sorts a Map<K,V> by it's values in descending order.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to sort
     * @return the sorted map
     */
    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {

        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {

                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
