package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

public abstract class LossFunction {

    /**
     * The loss function. This should be implemented by extending classes.
     * 
     *
     * @param set the set to evaluate
     * @return the double loss value
     */
    public abstract double calculate(final Set<CodeTriple> set);

}
