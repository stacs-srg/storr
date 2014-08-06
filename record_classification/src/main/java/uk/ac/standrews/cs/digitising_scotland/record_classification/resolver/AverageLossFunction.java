package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

public class AverageLossFunction extends LossFunction {

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
