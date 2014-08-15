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
 * An approximately normal distribution of numbers.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class NormalDistribution implements Distribution<Double> {

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

    @Override
    public Double getSample() {

        return mean + random.nextGaussian() * standard_deviation;
    }
}
