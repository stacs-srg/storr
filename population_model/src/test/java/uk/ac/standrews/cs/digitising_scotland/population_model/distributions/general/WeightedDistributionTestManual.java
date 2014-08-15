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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedDistribution;

public class WeightedDistributionTestManual {

    private static final int ITERATIONS = 1000000;

    public static void main(final String[] args) throws NegativeWeightException {

        final int[] weights = new int[]{1, 4, 1};
        final int[] buckets = WeightedDistributionTest.gatherSamplesIntoBuckets(weights, ITERATIONS);
        final double max_deviation = WeightedDistributionTest.getMaxDeviationFromTheoreticalDistribution(weights, buckets, ITERATIONS);

        printBucketCounts(weights, buckets, ITERATIONS);
        System.out.println("\nmax deviation from theoretical as percentage: " + max_deviation);
    }

    private static void printBucketCounts(final int[] weights, final int[] buckets, final int iterations) {

        final int total_weight = WeightedDistribution.sum(weights);

        for (int i = 0; i < buckets.length; i++) {

            System.out.print(buckets[i]);
            System.out.print("\t");
            System.out.println(WeightedDistributionTest.getDeviationFromTheoreticalAsPercentage(weights[i], buckets[i], iterations, total_weight));
        }
    }
}
