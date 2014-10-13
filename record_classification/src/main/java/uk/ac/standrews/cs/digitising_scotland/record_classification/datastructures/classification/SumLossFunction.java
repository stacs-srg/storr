package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the sum of all the confidences in the {@link Classification} set.
 * @author jkc25
 *
 */
public class SumLossFunction implements LossFunction<Set<Classification>, Double> {

    @Override
    public Double calculate(final Set<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += triple.getConfidence();
        }

        return confidenceSum;
    }

}
