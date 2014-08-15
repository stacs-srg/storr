package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general;

import java.util.ArrayList;

public abstract class RestrictedDistribution<Value> implements Distribution<Value>{


    // Restricted Distribution Helper Values
    protected Double minimumReturnValue = (Double) null;
    protected Double maximumReturnValue = (Double) null;
    
    protected ArrayList<Double> unusedSampleValues = new ArrayList<Double>();
    
    public abstract Value getSample(double earliestReturnValue, double latestReturnValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException;
    
    protected boolean inRange(final double d, final double earliestReturnValue, final double latestReturnValue) {
        if (earliestReturnValue < d && d < latestReturnValue) {
            return true;
        } else {
            return false;
        }
    }
    
}
