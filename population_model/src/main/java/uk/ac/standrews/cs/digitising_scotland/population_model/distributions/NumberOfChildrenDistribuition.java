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

public class NumberOfChildrenDistribuition implements Distribution<Integer> {
    /*
     * familiesbynumberofchildrenfinaladhoc_tcm77-371401
     * from http://www.ons.gov.uk/ons/about-ons/business-transparency/freedom-of-information/what-can-i-request/published-ad-hoc-data/pop/july-2014/families-by-number-of-dependent-and-non-dependent-children--uk--1996-2013.xls
     * Families by number of dependent and non-dependent children in the household, UK, 1996-2013
     * 
     * Number  per 1000 families
     * 0        354
     * 1        224
     * 2        232
     * 3        84
     * 4        61
     * 5        16
     * 6        17
     * 7        4
     * 8        4
     * 9        1
     * 10       1
     * 11       1
     */

    private static final int MAXIMUM_NUMBER_OF_CHILDREN = 11;
    private static final int[] NUMBER_DISTRIBUTION_WEIGHTS = new int[]{543, 224, 232, 84, 61, 16, 17, 4, 4, 1, 1, 1};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a number of children distribution.
     *
     * @param random the random number generator to be used
     */
    public NumberOfChildrenDistribuition(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, MAXIMUM_NUMBER_OF_CHILDREN, NUMBER_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }

}
