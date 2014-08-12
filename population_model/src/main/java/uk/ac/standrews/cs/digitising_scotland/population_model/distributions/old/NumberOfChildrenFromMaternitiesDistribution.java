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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.old;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedIntegerDistribution;

/**
 * Distribution ofnumber of children that can be born in a single maternity.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenFromMaternitiesDistribution implements Distribution<Integer> {
    /*
     * characteristicsofbirth2finala_tcm77-338092
     * from http://www.ons.gov.uk/ons/rel/vsob1/characteristics-of-birth-2--england-and-wales/2012/rft-characteristics-of-birth-2.xls
     * Characteristics of Birth 2
     * 
     * Number  per 1000000 materities
     * 1        984144
     * 2        15560
     * 3        288
     * 4        7
     */

    private static final int MAXIMUM_NUMBER_OF_CHILDREN_PER_PREGNANCY = 4;
    private static final int[] NUMBER_DISTRIBUTION_WEIGHTS = new int[]{984144, 15560, 288, 7};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a number of children in a maternity distribution.
     *
     * @param random the random number generator to be used
     */
    public NumberOfChildrenFromMaternitiesDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(1, MAXIMUM_NUMBER_OF_CHILDREN_PER_PREGNANCY, NUMBER_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }
}
