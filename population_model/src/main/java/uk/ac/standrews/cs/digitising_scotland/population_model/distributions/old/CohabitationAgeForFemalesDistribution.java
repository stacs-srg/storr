/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.old;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;

public class CohabitationAgeForFemalesDistribution implements Distribution<Integer> {

    /*
     * from marriagecohabfinal_tcm77-247857
     * Age at first cohabitation which did not end in marriage by year cohabitation began and sex
     * 
     * Data for 1980-1989
     * 
     * Age     Marriages per 1000
     * 15-19   39
     * 20-24   39
     * 25-29   13
     * 30-34   5
     * 35-39   2
     * 40-44   1
     * 45-49   1
     * 50-54   0
     * 55-59   0
     */

	private static final int MINIMUM_AGE_IN_YEARS = 15;
    private static final int MAXIMUM_AGE_IN_YEARS = 59;

    @SuppressWarnings("MagicNumber")
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{39, 39, 13, 5, 2, 1, 1, 0, 0};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a male age at marriage distribution.
     *
     * @param random the random number generator to be used
     */
    public CohabitationAgeForFemalesDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution((int) (MINIMUM_AGE_IN_YEARS * PopulationLogic.DAYS_PER_YEAR), (int) (MAXIMUM_AGE_IN_YEARS * PopulationLogic.DAYS_PER_YEAR) - 1, AGE_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }

}