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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import java.util.Random;

public class AffairNumberOfChildrenDistribution implements Distribution<Integer> {
    /*
     * Tom made these number up
     * 
     * Number  per 100 instances
     * 0        0
     * 1        80
     * 2        10
     * 3        5
     * 4        3
     * 5        1
     * 6        1
     */

    /**
     * Maximum number of children for a relationship.
     */
    public static final int MAXIMUM_NUMBER_OF_CHILDREN = 6;
    private static final int[] NUMBER_DISTRIBUTION_WEIGHTS = new int[] {0, 80, 10, 5, 3, 1, 1};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a number of children distribution.
     *
     * @param random the random number generator to be used
     */
    public AffairNumberOfChildrenDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, MAXIMUM_NUMBER_OF_CHILDREN, NUMBER_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }

}
