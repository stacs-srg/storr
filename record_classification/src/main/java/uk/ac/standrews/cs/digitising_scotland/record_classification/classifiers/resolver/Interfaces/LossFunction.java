package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces;

/**
 * Abstract loss function class. Allows swap-able loss functions.
 * @author jkc25
 *
 */
public interface LossFunction<O, LossMetric extends Comparable<LossMetric>> {

    /**
     * The loss function.
     * @param o the object to evaluate
     * @return the double loss value
     */
    public abstract LossMetric calculate(final O o);

}
