package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> matrix) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = matrix.deepClone();
        int maxNoOfEachCode = (int) Math.pow(COMPLEXITY_UPPERLIMIT, 1.0 / (double) matrix.size());
        maxNoOfEachCode = Math.max(LOWER_BOUND, maxNoOfEachCode);
        for (K k : matrix) {
            Collections.sort(clone.get(k), comparator);
            clone.put(k, matrix.get(k).subList(0, Math.min(matrix.get(k).size(), maxNoOfEachCode)));
        }
        return clone;
    }
}
