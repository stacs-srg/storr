package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

/**
 * A distribution of integers uniformly selected from the given range.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class UniformDistribution implements Distribution<Integer> {

    private final int lowest;
    private final int range;
    private final Random random;

    /**
     * Creates a uniform distribution of integers within the specified range.
     * @param lowest the lowest value in the range
     * @param highest the highest value in the range
     * @param random the random number generator to be used
     */
    public UniformDistribution(final int lowest, final int highest, final Random random) {

        this.lowest = lowest;
        range = highest - lowest + 1;
        this.random = random;
    }

    @Override
    public Integer getSample() {

        return lowest + random.nextInt(range);
    }
}
