package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

/**
 * Creates a sequence of uniformly distributed boolean values.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class UniformSexDistribution implements Distribution<Boolean> {

    private final Random random;

    /**
     * Creates a uniform distribution of booleans.
     * @param random the random number generator to be used
     */
    public UniformSexDistribution(final Random random) {

        this.random = random;
    }

    @Override
    public Boolean getSample() {

        return random.nextBoolean();
    }
}
