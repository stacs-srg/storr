package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.loss_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the average confidence of all the {@link Classification} in the set.
 * @author jkc25
 *
 */
public class AverageLossFunction extends AbstractLossFunction {

    @Override
    public double calculate(final Set<Classification> set) {

        List<Double> confidences = new ArrayList<>();
        for (Classification triple : set) {
            confidences.add(triple.getConfidence());
        }
        Double confidenceSum = 0.;
        for (Double conf : confidences) {
            confidenceSum += conf;
        }

        double average = confidenceSum / (double) confidences.size();

        return average;
    }

}
