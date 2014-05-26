package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

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
