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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;

public class CohabitationLengthDistribution implements Distribution<Integer> {

    /*
     * from numberofdivorcesageatdivorceandmaritalstatusbeforemarriage_tcm77-351699
     * from http://www.ons.gov.uk/ons/rel/vsob1/divorces-in-england-and-wales/2012/rtd-divorces---number-of-divorces-age-at-divorce-and-marital-status-before-marriage.xls
     * Number of divorces, age at divorce and marital status before marriage
     * 
     * Data for the year 1975
     * 
     * Years  per 1000 cohabitations
     * 1     200
     * 2     200
     * 3     150
     * 4     100
     * 5     70
     * 6     50
     * 7     40
     * 8     30
     * 9     30
     * 10    20
     * 11    20
     * 12    10
     * 13    10
     * 14    10
     * 15    10
     * 16    10
     * 17    10
     * 18    10
     * 19    10
     * 20    10
     * 20 - 40 (1 for each year)
     *  
     */

    private static final int MINIMUM_TIME_IN_YEARS = 0;
    private static final int MAXIMUM_TIME_IN_YEARS = 40;

    @SuppressWarnings("MagicNumber")
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{200, 200, 150, 100, 70, 50, 40, 30, 30, 20, 20, 10, 10, 10, 10, 10, 10, 10, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates an age at divorce for males distribution.
     *
     * @param random The random number generator to be used.
     */
    public CohabitationLengthDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution((int) (MINIMUM_TIME_IN_YEARS * PopulationLogic.DAYS_PER_YEAR), (int) (MAXIMUM_TIME_IN_YEARS * PopulationLogic
                    .DAYS_PER_YEAR) - 1, AGE_DISTRIBUTION_WEIGHTS, random);

        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }
}
