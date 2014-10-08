package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.tools.MapSorter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * Created by fraserdunlop on 07/10/2014 at 16:26.
 */
public class LossFunctionApplier<V,LossMetric extends Comparable<LossMetric>,F extends LossFunction<V,LossMetric>> {

    private MapSorter mapSorter = new MapSorter();
    private F lossFunction;

    public LossFunctionApplier(final F lossFunction){
        this.lossFunction = lossFunction;
    }

    public V getBest(final Collection<V> values) {
        Map<V, LossMetric> map = mapValuesToLoss(values);
        map = mapSorter.sortByValue(map);
        return map.keySet().iterator().next();
    }

    private Map<V, LossMetric> mapValuesToLoss(final Collection<V> values) {
        Map<V, LossMetric> map = new HashMap<>();
        for (V set : values) {
            map.put(set, lossFunction.calculate(set));
        }
        return map;
    }
}
