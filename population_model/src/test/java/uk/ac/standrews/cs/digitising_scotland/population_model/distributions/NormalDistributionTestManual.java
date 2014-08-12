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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeDeviationException;

public class NormalDistributionTestManual {

    private static final double DESIRED_MEAN = 15.2;
    private static final double DESIRED_STANDARD_DEVIATION = 4.8;
    private static final int ITERATIONS = 1000000;

    public static void main(final String[] args) throws NegativeDeviationException {

        final double[] samples = NormalDistributionTest.gatherSamples(DESIRED_MEAN, DESIRED_STANDARD_DEVIATION, ITERATIONS);
        final double real_mean = NormalDistributionTest.mean(samples);
        final double real_standard_deviation = NormalDistributionTest.standardDeviation(samples);

        System.out.println("real mean: " + real_mean);
        System.out.println("theoretical mean: " + DESIRED_MEAN);
        System.out.println("real standard deviation: " + real_standard_deviation);
        System.out.println("theoretical deviation: " + DESIRED_STANDARD_DEVIATION);
    }
}
