package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.util.Random;

/**
 * Provides a distribution of male first names.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class MaleFirstNameDistribution extends FileBasedEnumeratedDistribution {

    private static final String MALE_FIRST_NAME_DISTRIBUTION_KEY = "male_first_name_distribution_filename";

    /**
     * Creates a distribution of male first names.
     *
     * @param random the random number generator to be used
     */
    public MaleFirstNameDistribution(final Random random) throws IOException, InconsistentWeightException {

        super(PopulationProperties.getProperties().getProperty(MALE_FIRST_NAME_DISTRIBUTION_KEY), random);
    }
}
