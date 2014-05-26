package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.util.Random;

/**
 * Provides a distribution of surnames.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SurnameDistribution extends FileBasedEnumeratedDistribution {

    private static final String SURNAME_DISTRIBUTION_KEY = "surname_distribution_filename";

    /**
     * Creates a distribution of surnames.
     *
     * @param random the random number generator to be used
     */
    public SurnameDistribution(final Random random) throws IOException, InconsistentWeightException {

        super(PopulationProperties.getProperties().getProperty(SURNAME_DISTRIBUTION_KEY), random);
    }
}
