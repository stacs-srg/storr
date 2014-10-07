package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import java.io.IOException;
import java.util.*;

/**
 * Allows pruning of MultiValueMap values using a Comparator.
 * The Comparator is used to sort the lists of values. The values
 * at the heads of the lists are kept and those at the tails discarded.
 * The lists are cut down to a length calculated
 * Created by fraserdunlop on 06/10/2014 at 10:38.
 */
public class MultiValueMapPruner<K, V, C extends Comparator<V>> {

    private int LOWER_BOUND = 1;
    private int COMPLEXITY_UPPER_LIMIT = 2000;
    private final C comparator;

    public MultiValueMapPruner(C comparator){
        this.comparator = comparator;
    }

    /**
     * Prunes the longest list in the MultiValueMap until complexity within bound.
     * The longest lists are pruned first.
     * @param map the MultiValueMap to prune
     * @return a new pruned MultiValueMap
     */
    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {

        MultiValueMap<K, V> clone = map.deepClone();
        int[] sizes = new int[map.size()];
        ArrayList<K> keyList = new ArrayList<>(map.keySet());
        int i = 0;
        for (K key : keyList) {
            sizes[i++] = map.get(key).size();
        }
        int[] prunedListSizes = calculatePrunedListSizes(sizes);
        i = 0;
        for (K k : keyList) {
            int maxLength = Math.max(LOWER_BOUND, prunedListSizes[i++]);
            Collections.sort(clone.get(k), comparator);
            clone.put(k, map.get(k).subList(0, Math.min(map.get(k).size(), maxLength)));
        }
        return clone;

    }

    private int[] calculatePrunedListSizes(int[] listSizes) {
        int[] pruned = listSizes.clone();
        while (complexity(pruned) > COMPLEXITY_UPPER_LIMIT)
            pruned[maxValueIndex(pruned)]--;
        return pruned;
    }

    private int maxValueIndex(int[] pruned) {
        int index = 0;
        int largest = 0;
        for (int i = 0 ; i < pruned.length ; i++){
            if (pruned[i]>largest) {
                largest = pruned[i];
                index = i;
            }
        }
        return index;
    }

    private int complexity(int[] listSizes){
        int complexity = 1;
        for(int i : listSizes)
            complexity *= i;
        return complexity;
    }

    public void setComplexityUpperBound(final int i) {
        COMPLEXITY_UPPER_LIMIT = i;
    }

    public void setListLengthLowerBound(final int i) {
        if(i<1)
            throw new IllegalArgumentException("List length lower bound must be a positive integer.");
        LOWER_BOUND = i;
    }
}
