package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the average confidence of all the {@link Classification} in the set.
 * @author jkc25
 *
 */
public class AverageLossFunction implements LossFunction<Set<Classification>,Double> {

    @Override
    public Double calculate(final Set<Classification> set) {

        List<Double> confidences = new ArrayList<>();
        for (Classification triple : set) {
            confidences.add(triple.getConfidence());
        }
        Double confidenceSum = 0.;
        for (Double conf : confidences) {
            confidenceSum += conf;
        }

        return confidenceSum / (double) confidences.size();
    }

}
