/**
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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;

/**
 * Distribution modelling ages of males at marriage, represented in days.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleAgeAtMarriageDistrobution implements Distribution<Integer> {
    
	/*
     * from ageatmarriageandpreviousmaritalstatus_tcm77-366510.xls
     * from http://www.ons.gov.uk/ons/rel/vsob1/marriages-in-england-and-wales--provisional-/2012/rtd-age-at-marriage-and-previous-marital-status.xls
     * Age of husband and previous marital status, 1846–2011
     * 
     * from MYE6TS3C_mid-1971-mid-2012-unformatted-ctry_quin_sex-data-file
     * from http://www.ons.gov.uk/ons/rel/vsob1/cancer-statistics-registrations--england--series-mb1-/no--43--2012/rft-mid-1971-to-mid-2012-population-estimates.xls
     * Mid-1971 to Mid-2012 Population Estimates: Quinary age groups for Constituent Countries in the United Kingdom
     * 
     * Age     Marriages per 1000
     * 0-4     0
     * 5-9     0
     * 10-14   0
     * 15-19   20
     * 20-24   107
     * 25-29   49
     * 30-34   18
     * 35-39   10
     * 40-44   7
     * 45-49   5
     * 50-54   4
     * 55-59   4
     * 60-64   4
     * 65-69   3
     * 70-74   3
     * 75-79   3
     * 80-84   1
     * 85-89   1
     * 90+     0
     */

	private static final int MAXIMUM_AGE_IN_YEARS = 100;
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{0,0,0,20,107,49,18,10,7,5,4,4,4,3,3,3,1,1,0};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates an age at marriage distribution
     * @param random the random number generator to be used
     */
    public MaleAgeAtMarriageDistrobution(final Random random) {
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
