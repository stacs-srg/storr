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

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.DivorceInstigation;

import java.util.Random;

/**
 * Distribution modelling instigation of divorce by gender.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DivorceInstigatedByGenderDistribution implements Distribution<Integer> {
    /*
     * from numberofdivorcesageatdivorceandmaritalstatusbeforemarriage_tcm77-351699
     * from http://www.ons.gov.uk/ons/rel/vsob1/divorces-in-england-and-wales/2012/rtd-divorces---number-of-divorces-age-at-divorce-and-marital-status-before-marriage.xls
     * Number of divorces, age at divorce and marital status before marriage
     * 
     * Data for the year 1975
     * 
     * Gender      Divorces per 10000
     * Male         96
     * Female       96
     * No divorce   9808
     */

    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{96, 96, 9808};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a divorce instigated by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public DivorceInstigatedByGenderDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, 2, AGE_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    /**
     * Returns either a gender or no divorce for which party in the marriage will instigate the divorce.
     * 
     * @return Indicates the gender of the instigator or no divorce
     */
    public DivorceInstigation getDefinedSample() {
        int value = getSample();
        switch (value) {
        case 0:
            return DivorceInstigation.MALE;
        case 1:
            return DivorceInstigation.FEMALE;
        case 2:
            return DivorceInstigation.NO_DIVORCE;
        default:
            break;
        }
        return null;
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }

}
