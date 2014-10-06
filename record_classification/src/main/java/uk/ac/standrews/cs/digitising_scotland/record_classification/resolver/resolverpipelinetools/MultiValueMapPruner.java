package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.MultiValueMap;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 10:38.
 */
public class MultiValueMapPruner<K, V, C extends Comparator<V>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiValueMapPruner.class);
    private static final int LOWER_BOUND = 1;
    private static final int COMPLEXITY_UPPERLIMIT = 2000;
    private final C comparator;

    public MultiValueMapPruner(C comparator){
        this.comparator = comparator;
    }

    /**
     * Chop until complexity within bound.
     *
     */
    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = map.deepClone();
        int maxNoValuesAtEachKey = (int) Math.pow(COMPLEXITY_UPPERLIMIT, 1.0 / (double) map.size());
        maxNoValuesAtEachKey = Math.max(LOWER_BOUND, maxNoValuesAtEachKey);
        for (K k : map) {
            Collections.sort(clone.get(k), comparator);
            clone.put(k, map.get(k).subList(0, Math.min(map.get(k).size(), maxNoValuesAtEachKey)));
        }
        return clone;
    }
}
