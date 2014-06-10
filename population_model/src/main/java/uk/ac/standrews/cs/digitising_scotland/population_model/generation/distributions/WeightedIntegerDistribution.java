/**
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
package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

/**
 * A general distribution of integers within a specified range, the shape of which is controlled by a list of weights.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class WeightedIntegerDistribution implements Distribution<Integer> {

    private final int minimum;
    private final int range;
    private final WeightedDistribution weighted_distribution;

    /**
     * Initialises the distribution. See {@link WeightedDistribution} for an explanation of the weights.
     * @param minimum the minimum of the distribution
     * @param maximum the maximum of the distribution
     * @param weights the weights that control the shape of the distribution
     * @throws NegativeWeightException if any of the weights are negative
     * @param random the random number generator to be used
     * @see WeightedDistribution
     */
    public WeightedIntegerDistribution(final int minimum, final int maximum, final int[] weights, final Random random) throws NegativeWeightException {

        this.minimum = minimum;
        range = maximum - minimum + 1;
        weighted_distribution = new WeightedDistribution(weights, random);
    }

    @Override
    public Integer getSample() {

        return minimum + (int) (range * weighted_distribution.getSample());
    }
}
