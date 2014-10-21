package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 *
 * Created by fraserdunlop on 17/10/2014 at 11:44.
 */
public class LogLengthWeightedLossFunction implements LossFunction<Set<Classification>, Double> {

    @Override
    public Double calculate(final Set<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += Math.abs(triple.getConfidence()) * Math.log(triple.getTokenSet().size());
        }

        return confidenceSum;
    }

}
