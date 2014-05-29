package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

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
