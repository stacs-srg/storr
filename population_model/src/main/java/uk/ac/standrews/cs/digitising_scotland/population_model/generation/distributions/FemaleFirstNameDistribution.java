package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.util.Random;

/**
 * Provides a distribution of female first names.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class FemaleFirstNameDistribution extends FileBasedEnumeratedDistribution {

    private static final String FEMALE_FIRST_NAME_DISTRIBUTION_KEY = "female_first_name_distribution_filename";

    /**
     * Creates a distribution of female first names.
     *
     * @param random the random number generator to be used
     */
    public FemaleFirstNameDistribution(final Random random) throws IOException, InconsistentWeightException {

        super(PopulationProperties.getProperties().getProperty(FEMALE_FIRST_NAME_DISTRIBUTION_KEY), random);
    }
}
