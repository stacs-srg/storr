package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

/**
 * Abstract loss function class. Allows swap-able loss functions.
 * @author jkc25
 *
 */
public abstract class AbstractLossFunction<O, LossMetric extends Comparable<LossMetric>> {

    /**
     * The loss function.
     * @param o the object to evaluate
     * @return the double loss value
     */
    public abstract LossMetric calculate(final O o);

}
