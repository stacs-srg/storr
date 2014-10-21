package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;

/**
 * Created to make length a less the dominating factor in the LengthWeightedLossFunction
 * Created by fraserdunlop on 17/10/2014 at 11:44.
 */
public class LogLengthWeightedLossFunction  implements LossFunction<Multiset<Classification>, Double> {

    @Override
    public Double calculate(final Multiset<Classification> set) {

        double confidenceSum = 0;

        for (Classification triple : set) {
            confidenceSum += triple.getConfidence() * Math.log(triple.getTokenSet().size()+1);
        }

        return confidenceSum;
    }

}