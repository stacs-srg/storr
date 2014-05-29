package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

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
