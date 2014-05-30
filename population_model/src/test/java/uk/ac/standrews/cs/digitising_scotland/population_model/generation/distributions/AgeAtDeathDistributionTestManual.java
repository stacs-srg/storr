package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

public class AgeAtDeathDistributionTestManual {

    private static final int ITERATIONS = 1000000;

    public static void main(final String[] args) throws NegativeWeightException {

        final int[] buckets = gatherSamplesIntoBuckets(ITERATIONS);

        printAgeCounts(buckets);
        System.out.println("\n\n");
        printBucketCounts(buckets);
    }

    public static int[] gatherSamplesIntoBuckets(final int iterations) throws NegativeWeightException {

        final AgeAtDeathDistribution distribution = new AgeAtDeathDistribution(new Random());
        final int[] buckets = new int[100];

        for (int i = 0; i < iterations; i++) {

            final int age_in_days = distribution.getSample();
            final int age_in_years = (int) ((double) age_in_days / CompactPopulation.DAYS_PER_YEAR);
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