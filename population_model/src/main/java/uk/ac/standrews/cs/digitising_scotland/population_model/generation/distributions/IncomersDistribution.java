package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

/**
 * Creates a sequence of boolean values with a given weighting.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class IncomersDistribution implements Distribution<Boolean> {

    private final double incomer_probability;
    private final Random random;

    /**
     * Creates a distribution of booleans.
     * 
     * @param incomer_probability the probability that a person is an incomer
     * @param random the random number generator to be used
     */
    public IncomersDistribution(final double incomer_probability, final Random random) {

        this.incomer_probability = incomer_probability;
        this.random = random;
    }

    @Override
    public Boolean getSample() {

        return random.nextDouble() < incomer_probability;
    }
}
