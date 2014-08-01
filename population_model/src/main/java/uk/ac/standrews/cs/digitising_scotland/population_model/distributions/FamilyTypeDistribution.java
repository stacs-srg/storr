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

public class FamilyTypeDistribution implements Distribution<FamilyType> {
    /*
     * from cohabitationandcohortanalyses_tcm77-366514
     * from http://www.ons.gov.uk/ons/rel/vsob1/marriages-in-england-and-wales--provisional-/2012/rtd-marriage-statistics-cohabitation-and-cohort-analyses.xls
     * Previous marital statuses of bride and bridegroom, type of ceremony, sex, age and cohabitation prior to marriage (numbers and percentages), 2011
     * 
     * from familiesbyfamilytype2001and2011_tcm77-360615
     * from http://www.ons.gov.uk/ons/rel/family-demography/stepfamilies/2011/families-rft.xls
     * Table 1: Families by family type and presence of children, 2001 and 2011
     * 
     * Data for the year 2011
     * 
     * Type            per 1000 People
     * Single               143
     * Lone Mother          74
     * Lone Father          12
     * Cohabitation         159     
     * Cohab then marraige  520
     * Marriage             91
     */

    private static final int[] TYPE_DISTRIBUTION_WEIGHTS = new int[]{143, 74, 12, 159, 520, 91};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a divorce instigated by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public FamilyTypeDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, 5, TYPE_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    /**
     * Returns either a gender or no divorce for which party in the marriage will instigate the divorce.
     *
     * @return Indicates the gender of the instigator or no divorce
     */
    @Override
    public FamilyType getSample() {

        switch ((int) distribution.getSample()) {
            case 0:
                return FamilyType.SINGLE;
            case 1:
                return FamilyType.LONE_MOTHER;
            case 2:
                return FamilyType.LONE_FATHER;
            case 3:
            	return FamilyType.COHABITATION;
            case 4:
            	return FamilyType.COHABITATION_THEN_MARRIAGE;
            case 5:
            	return FamilyType.MARRIAGE;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }
}