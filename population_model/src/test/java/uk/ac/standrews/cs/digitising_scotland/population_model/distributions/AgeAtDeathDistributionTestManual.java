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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;

import java.util.Random;

public class AgeAtDeathDistributionTestManual {

    private static final int ITERATIONS = 1000000;

    public static void main(final String[] args) throws NegativeWeightException {

        final int[] buckets = gatherSamplesIntoBuckets(ITERATIONS);

        printAgeCounts(buckets);
        System.out.println("\n\n");
        printBucketCounts(buckets);
    }

    public static int[] gatherSamplesIntoBuckets(final int iterations) throws NegativeWeightException {

        final DeathAgeDistribution distribution = new DeathAgeDistribution(new Random());
        final int[] buckets = new int[100];

        for (int i = 0; i < iterations; i++) {

            final int age_in_days = distribution.getSample();
            final int age_in_years = (int) ((double) age_in_days / PopulationLogic.DAYS_PER_YEAR);
            buckets[age_in_years]++;
        }
        return buckets;
    }

    private static void printAgeCounts(final int[] buckets) {

        for (int i = 0; i < buckets.length; i++) {

            System.out.print(i);
            System.out.print("\t");
            System.out.println(buckets[i]);
        }
    }

    private static void printBucketCounts(final int[] buckets) {

        for (int i = 0; i < buckets.length; i += 5) {

            System.out.print(i + "-" + (i + 4));
            System.out.print("\t");

            int count = 0;
            for (int j = 0; j < 5; j++) {
                count += buckets[i + j];
            }
            System.out.println(count);
        }
    }
}
