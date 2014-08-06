package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.loss_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

/**
 * Calculates the loss for a set of CodeTriples.
 * The loss function is defined as the sum of all the confidences in the {@link CodeTriple} set.
 * @author jkc25
 *
 */
public class SumLossFunction extends AbstractLossFunction {

    @Override
    public double calculate(final Set<CodeTriple> set) {

        List<Double> confidences = new ArrayList<>();

        for (CodeTriple triple : set) {
            confidences.add(triple.getConfidence());
        }

        Double confidenceSum = 0.;
        for (Double conf : confidences) {
            confidenceSum += conf;
        }

        return confidenceSum;
    }

}
