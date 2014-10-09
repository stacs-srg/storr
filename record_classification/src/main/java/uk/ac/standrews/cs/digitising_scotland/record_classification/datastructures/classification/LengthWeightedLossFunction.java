package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification;

import java.util.Set;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;

/**
 * Loss function that sums the confidences but weights each confidence by the length of the tokenset.
 * 
 * @author jkc25
 *
 */
public class LengthWeightedLossFunction implements LossFunction<Set<Classification>,Double> {

    @Override
    public Double calculate(final Set<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += triple.getConfidence() * triple.getTokenSet().size();
        }

        return confidenceSum;
    }

}