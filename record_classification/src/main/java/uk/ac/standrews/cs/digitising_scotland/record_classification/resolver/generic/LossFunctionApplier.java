package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import java.util.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.tools.MapSorter;

/**
 * Helper function for applying a loss function to a collection
 * of Sets and finding set with the best loss.
 * Created by fraserdunlop on 07/10/2014 at 16:26.
 */
public class LossFunctionApplier<V, LossMetric extends Comparable<LossMetric>, F extends LossFunction<Set<V>, LossMetric>> {

    private MapSorter mapSorter = new MapSorter();
    private F lossFunction;

    public LossFunctionApplier(final F lossFunction) {

        this.lossFunction = lossFunction;
    }

    /**
     * Returns the Set with the best loss. Will pick the
     * set whose loss sorts to the head of a list of LossMetric
     * objects. If the comparator for your LossMetric is implemented
     * the wrong way round then getBest will return the worst set
     * rather than the best.
     * @param sets sets to rank based on loss
     * @return best according to lossFunction or null if sets is empty.
     */
    public Set<V> getBest(final Collection<Set<V>> sets) {

        Map<Set<V>, LossMetric> map = mapValuesToLoss(sets);
        map = mapSorter.sortByValue(map);
        if (map.keySet().iterator().hasNext()) { return map.keySet().iterator().next(); }
        return new HashSet<>();

    }

    private Map<Set<V>, LossMetric> mapValuesToLoss(final Collection<Set<V>> values) {

        Map<Set<V>, LossMetric> map = new HashMap<>();
        for (Set<V> set : values) {
            map.put(set, lossFunction.calculate(set));
        }
        return map;
    }
}
