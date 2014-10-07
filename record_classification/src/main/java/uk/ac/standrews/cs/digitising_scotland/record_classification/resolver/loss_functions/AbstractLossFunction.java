package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.loss_functions;

/**
 * Abstract loss function class. Allows swap-able loss functions.
 * @author jkc25
 *
 */
public abstract class AbstractLossFunction<O, LossMetric extends Comparable<LossMetric>> {

    /**
     * The loss function.
     *
     * @param set the set to evaluate
     * @return the double loss value
     */
    public abstract LossMetric calculate(final O set);

}
