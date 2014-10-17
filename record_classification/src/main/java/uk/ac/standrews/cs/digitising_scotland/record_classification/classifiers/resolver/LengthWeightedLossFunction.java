package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 * Loss function that sums the confidences but weights each confidence by the length of the tokenset.
 * 
 * @author jkc25
 *
 */
public class LengthWeightedLossFunction implements LossFunction<Set<Classification>, Double> {

    @Override
    public Double calculate(final Set<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += triple.getConfidence() * triple.getTokenSet().size();
        }

        return confidenceSum;
    }

}
