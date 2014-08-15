/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general;

import java.util.ArrayList;
import java.util.Random;

/**
 * An approximately normal distribution of numbers.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class NormalDistribution extends RestrictedDistribution<Double> {

    private final Random random;
    private final double mean;
    private final double standard_deviation;
    
    

    /**
     * Creates a normal distribution with specified characteristics.
     * @param mean the mean of the distribution
     * @param standard_deviation the standard deviation of the distribution
     * @param random the random number generator to be used
     * @throws NegativeDeviationException if the standard deviation is negative
     */
    public NormalDistribution(final double mean, final double standard_deviation, final Random random) throws NegativeDeviationException {

        if (standard_deviation < 0.0) { throw new NegativeDeviationException("negative standard deviation: " + standard_deviation); }

        this.mean = mean;
        this.standard_deviation = standard_deviation;
        this.random = random;
    }
    
    public NormalDistribution(final double mean, final double standard_deviation, final Random random, double minimumReturnValue, double maximumReturnValue) throws NegativeDeviationException {
        this(mean, standard_deviation, random);
        this.minimumReturnValue = minimumReturnValue;
        this.maximumReturnValue = maximumReturnValue;
    }
    
    

    @Override
    public Double getSample() {

        return mean + random.nextGaussian() * standard_deviation;
    }

    @Override
    public Double getSample(double earliestReturnValue, double latestReturnValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException {
        if (minimumReturnValue == (Double) null || maximumReturnValue == (Double) null) {
            throw new NotSetUpAtClassInitilisationException();
        }
        
        if (earliestReturnValue >= maximumReturnValue || latestReturnValue <= minimumReturnValue) {
            throw new NoPermissableValueException();
        } else {
            if (unusedSampleValues.size() != 0) {
                int j = 0;
                for (double d : unusedSampleValues) {
                    if (inRange(d, earliestReturnValue, latestReturnValue)) {
                        unusedSampleValues.remove(j);
                        return d;
                    }
                    j++;
                }
            }
        }
        double v = getSample();
        while (!inRange(v, earliestReturnValue, latestReturnValue)) {
            unusedSampleValues.add(v);
            v = getSample();
        }
        return v;
    }
}
