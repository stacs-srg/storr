package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

/**
 * Distribution modelling ages at death, represented in days.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class AgeAtDeathDistribution implements Distribution<Integer> {

    /*
     * from referencetablefinal_tcm77-301305.xls
     * from http://www.ons.gov.uk/ons/publications/re-reference-tables.html?edition=tcm%3A77-294534
     * Age-Specific Mortality Rates by NS-SEC in England and Wales, 1982-86 to 2002-06
     * A (ii) Males, NS-SEC 1, 1987-1991
     * 
     * Age     Deaths per 1000
     * 0-4     2 // Al made this row up!
     * 5-9     2 // Al made this row up!
     * 9-14    2 // Al made this row up!
     * 15-19   3
     * 20-24   7
     * 25-29   4 // Al made this row up!
     * 30-34   3 // Al made this row up!
     * 35-39   5
     * 40-44   20
     * 45-49   21
     * 50-54   35
     * 55-59   63
     * 60-64   115
     * 65-69   139
     * 70-74   143
     * 75-79   143
     * 80-84   149
     * 85-89   94
     * 90+     40
     */

    private static final int MAXIMUM_AGE_IN_YEARS = 100;
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{2, 2, 2, 3, 7, 4, 3, 5, 20, 21, 35, 63, 115, 139, 143, 143, 149, 94, 20, 20};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates an age at death distribution.
     * @param random the random number generator to be used
     */
    public AgeAtDeathDistribution(final Random random) {

        try {
            distribution = new WeightedIntegerDistribution(0, (int) (MAXIMUM_AGE_IN_YEARS * CompactPopulation.DAYS_PER_YEAR) - 1, AGE_DISTRIBUTION_WEIGHTS, random);
        }
        catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {

        return distribution.getSample();
    }
}
