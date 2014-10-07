package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

/**
 * Allows pruning of MultiValueMap values using a Comparator.
 * The Comparator is used to sort the lists of values. The values
 * at the heads of the lists are kept and those at the tails discarded.
 * The lists are cut down to a length calculated
 * Created by fraserdunlop on 06/10/2014 at 10:38.
 */
public class MultiValueMapPruner<K, V, C extends Comparator<V>> {

    private static final int LOWER_BOUND = 1;
    private static final int COMPLEXITY_UPPER_LIMIT = 2000;
    private final C comparator;

    public MultiValueMapPruner(C comparator){
        this.comparator = comparator;
    }

    /**
     * Chop until complexity within bound. Lists are cut to a length based on
     * the LOWER_BOUND, COMPLEXITY_UPPER_LIMIT and size of the cloned map being
     * manipulated. The length this algorithm decides to cut Lists to varies as it
     * iterates over the keys cutting lists.
     * @param map the MultiValueMap to prune
     * @return a new pruned MultiValueMap
     */
    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = map.deepClone();
        for (K k : map) {
            int maxLength = estimateListLengthToCutTo(clone);
            Collections.sort(clone.get(k), comparator);
            clone.put(k, map.get(k).subList(0, Math.min(map.get(k).size(), maxLength)));
        }
        return clone;
    }

    /**
     * Estimates the length to cut Lists to based on the COMPLEXITY_UPPER_LIMIT and LOWER_BOUND
     * parameters. The maxLength is taken to be the maximum of the LOWER_BOUND and
     * COMPLEXITY_UPPER_LIMIT^(map.size()).
     * @param map map used to calculate length
     * @return int length
     */
    private int estimateListLengthToCutTo(MultiValueMap<K, V> map) {
        int maxLength = (int) Math.pow(COMPLEXITY_UPPER_LIMIT, 1.0 / (double) map.size());
        maxLength = Math.max(LOWER_BOUND, maxLength);
        return maxLength;
    }
}
