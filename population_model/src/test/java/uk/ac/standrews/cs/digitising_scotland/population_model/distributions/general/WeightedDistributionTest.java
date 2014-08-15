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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedDistribution;

@RunWith(Parameterized.class)
public class WeightedDistributionTest {

    private static final double MAX_ACCEPTABLE_DEVIATION_PERCENTAGE = 2.0;
    private static final int ITERATIONS = 1000000;

    @Parameters
    public static Collection<Object[]> generateData() {

        return Arrays.asList(new Object[][]{{new int[]{1}}, {new int[]{4}}, {new int[]{1, 4}}, {new int[]{1, 4, 1}}, {new int[]{1, 0, 1}}, {new int[]{1, 4, 1, 2, 5}}, {new int[]{1, 4, 1, 3, 1, 5, 3, 2}}});
    }

    private final int[] weights;

    public WeightedDistributionTest(final int[] weights) {

        this.weights = Arrays.copyOf(weights, weights.length);
    }

    @Test
    public void checkDistributionAgainstTheoretical() throws NegativeWeightException {

        final int[] buckets = gatherSamplesIntoBuckets(weights, ITERATIONS);
        final double max_deviation = getMaxDeviationFromTheoreticalDistribution(weights, buckets, ITERATIONS);

        assertTrue(max_deviation < MAX_ACCEPTABLE_DEVIATION_PERCENTAGE);
    }

    public static double getMaxDeviationFromTheoreticalDistribution(final int[] weights, final int[] buckets, final int iterations) {

        final int total_weight = WeightedDistribution.sum(weights);

        double max_deviation_percentage = 0.0;
        for (int i = 0; i < buckets.length; i++) {
            max_deviation_percentage = Math.max(max_deviation_percentage, getDeviationFromTheoreticalAsPercentage(weights[i], buckets[i], iterations, total_weight));
        }
        return max_deviation_percentage;
    }

    public static int[] gatherSamplesIntoBuckets(final int[] weights, final int iterations) throws NegativeWeightException {

        final WeightedDistribution distribution = new WeightedDistribution(weights, new Random());
        final int[] buckets = new int[weights.length];

        for (int i = 0; i < iterations; i++) {

            final double sample = distribution.getSample();
            buckets[(int) (sample * weights.length)]++;
        }
        return buckets;
    }

    public static double getDeviationFromTheoreticalAsPercentage(final double weight, final int bucket_count, final int iterations, final int total_weight) {

        if (weight == 0.0) { return 0.0; }
        final double occurrences_tended_to = iterations * weight / total_weight;
        return Math.abs((bucket_count - occurrences_tended_to) / occurrences_tended_to) * 100;
    }
}
