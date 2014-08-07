package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.loss_functions;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

/**
 * Loss function that sums the confidences but weights each confidence by the length of the tokenset.
 * 
 * @author jkc25
 *
 */
public class LengthWeightedLossFunction extends AbstractLossFunction {

    @Override
    public double calculate(final Set<CodeTriple> set) {

        double confidenceSum = 0;

        for (CodeTriple triple : set) {
            confidenceSum += triple.getConfidence() * triple.getTokenSet().size();
        }

        return confidenceSum;
    }

}
