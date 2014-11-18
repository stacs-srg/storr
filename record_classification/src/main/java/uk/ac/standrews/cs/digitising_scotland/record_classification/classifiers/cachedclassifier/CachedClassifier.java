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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.cachedclassifier;

import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;

/**
 * TODO test! - fraser 8/Oct
 * TODO document!
 */
public class CachedClassifier<K, V> implements IClassifier<K, V> {

    private Map<K, V> cache;
    private IClassifier<K, V> classifier;

    public CachedClassifier(final IClassifier<K, V> classifier, final Map<K, V> map) {

        this.classifier = classifier;
        this.cache = map;
    }

    @Override
    public V classify(final K k) throws Exception {

        V v = cache.get(k);
        if (v == null) {
            v = classifier.classify(k);
            cache.put(k, v);
        }
        return v;
    }
}
