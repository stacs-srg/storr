package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;


import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the sum of all the confidences in the {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification} set.
 * @author jkc25
 *
 */
public class SumLossFunction implements LossFunction<Multiset<Classification>, Double> {

    @Override
    public Double calculate(final Multiset<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += triple.getConfidence();
        }

        return confidenceSum;
    }

}
