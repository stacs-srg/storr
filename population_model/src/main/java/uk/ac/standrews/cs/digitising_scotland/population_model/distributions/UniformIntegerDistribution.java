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

/**
 * A distribution of integers uniformly selected from the given range.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class UniformIntegerDistribution implements Distribution<Integer> {

    private final int lowest;
    private final int range;
    private final Random random;

    /**
     * Creates a uniform distribution of integers within the specified range.
     * 
     * @param lowest the lowest value in the range
     * @param highest the highest value in the range
     * @param random the random number generator to be used
     */
    public UniformIntegerDistribution(final int lowest, final int highest, final Random random) {

        this.lowest = lowest;
        range = highest - lowest + 1;
        this.random = random;
    }

    @Override
    public Integer getSample() {

        return lowest + random.nextInt(range);
    }
}
