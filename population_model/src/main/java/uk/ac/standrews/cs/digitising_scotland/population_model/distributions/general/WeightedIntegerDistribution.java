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

import java.util.Random;

/**
 * A general distribution of integers within a specified range, the shape of which is controlled by a list of weights.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class WeightedIntegerDistribution extends RestrictedDistribution<Integer> {

    private final int range;
    private final WeightedDistribution weighted_distribution;

    /**
     * Initialises the distribution. See {@link WeightedDistribution} for an explanation of the weights.
     * 
     * @param minimum the minimum of the distribution.
     * @param maximum the maximum of the distribution.
     * @param weights the weights that control the shape of the distribution.
     * @throws NegativeWeightException if any of the weights are negative.
     * @param random the random number generator to be used.
     * @see WeightedDistribution
     */
    public WeightedIntegerDistribution(final int minimum, final int maximum, final int[] weights, final Random random) throws NegativeWeightException {

        minimumSpecifiedValue = Double.valueOf(minimum);
        maximumSpecifiedValue = Double.valueOf(maximum);
        range = maximum - minimum + 1;
        weighted_distribution = new WeightedDistribution(weights, random);
    }

    /**
     * Initialises the distribution. See {@link WeightedDistribution} for an explanation of the weights.
     * 
     * @param minimum the minimum of the distribution.
     * @param maximum the maximum of the distribution.
     * @param weights the weights that control the shape of the distribution.
     * @param random the random number generator to be used.
     * @param handleNoPermissableValueAsZero If set as true then the distribution will view that when it throws a NoPermissableValueException that it is akin to returning a value of 0 to the balance of the distribution - however a NoPermissableValueException will still be thrown.
     * @throws NegativeWeightException if any of the weights are negative.
     */
    public WeightedIntegerDistribution(final int minimum, final int maximum, final int[] weights, final Random random, final boolean handleNoPermissableValueAsZero) throws NegativeWeightException {

        minimumSpecifiedValue = Double.valueOf(minimum);
        maximumSpecifiedValue = Double.valueOf(maximum);
        range = maximum - minimum + 1;
        weighted_distribution = new WeightedDistribution(weights, random, handleNoPermissableValueAsZero);
    }

    @Override
    public Integer getSample() {

        return (int) (minimumSpecifiedValue + (int) (range * weighted_distribution.getSample()));
    }

    @Override
    public Integer getSample(final double earliestValue, final double latestValue) throws NoPermissableValueException {
        return (int) (minimumSpecifiedValue + (int) (range * weighted_distribution.getSample((earliestValue - minimumSpecifiedValue) / range, (latestValue - minimumSpecifiedValue) / range)));
    }

    @Override
    public int[] getWeights() {
        return weighted_distribution.getWeights();
    }
}
