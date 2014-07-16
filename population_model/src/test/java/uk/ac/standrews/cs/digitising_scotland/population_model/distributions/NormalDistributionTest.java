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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NormalDistributionTest {

    private static final double MAX_ACCEPTABLE_DEVIATION_PERCENTAGE = 2.0;
    private static final int ITERATIONS = 1000000;

    @Parameters
    public static Collection<Object[]> generateData() {

        return Arrays.asList(new Object[][]{{15.2, 4.8}, {-125, 86}, {0.02, 0.01}});
    }

    private final double mean;
    private final double standard_deviation;

    public NormalDistributionTest(final double mean, final double standard_deviation) {

        this.mean = mean;
        this.standard_deviation = standard_deviation;
    }

    @Test
    public void checkDistributionAgainstTheoretical() throws NegativeDeviationException {

        final double[] samples = gatherSamples(mean, standard_deviation, ITERATIONS);
        final double real_mean = mean(samples);
        final double real_standard_deviation = standardDeviation(samples);

        assertTrue(percentageDeviation(real_mean, mean) < MAX_ACCEPTABLE_DEVIATION_PERCENTAGE);
        assertTrue(percentageDeviation(real_standard_deviation, standard_deviation) < MAX_ACCEPTABLE_DEVIATION_PERCENTAGE);
    }

    private double percentageDeviation(final double actual, final double theoretical) {

        return Math.abs((actual - theoretical) / theoretical) * 100.0;
    }

    public static double[] gatherSamples(final double mean, final double standard_deviation, final int iterations) throws NegativeDeviationException {

        final NormalDistribution distribution = new NormalDistribution(mean, standard_deviation, new Random());

        final double[] samples = new double[iterations];

        for (int i = 0; i < iterations; i++) {
            samples[i] = distribution.getSample();
        }
        return samples;
    }

    public static double standardDeviation(final double[] samples) {

        final double mean = mean(samples);
        double temp = 0.0;
        for (final double d : samples) {
            temp += (mean - d) * (mean - d);
        }
        return Math.sqrt(temp / samples.length);
    }

    public static double mean(final double[] samples) {

        double total = 0;
        for (final double d : samples) {
            total += d;
        }
        return total / samples.length;
    }
}
