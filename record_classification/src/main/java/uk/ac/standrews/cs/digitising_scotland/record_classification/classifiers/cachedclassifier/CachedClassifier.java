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
