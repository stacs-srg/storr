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

public class AffairWithMarriedOrSingleDistribution implements Distribution<FamilyType> {
    /*
     *  
     * With       per 100
     * Single      90
     * Married     10
     */

    private static final int[] WITH_DISTRIBUTION_WEIGHTS = new int[]{90, 10};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a divorce reason by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public AffairWithMarriedOrSingleDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, 1, WITH_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    /**
     * Returns the reason for divorce.
     *
     * @return Indicates the gender of the instigator or no divorce
     */
    @Override
    public FamilyType getSample() {

        switch ((int) distribution.getSample()) {
            case 0:
                return FamilyType.SINGLE_AFFAIR;
            case 1:
                return FamilyType.INTER_MARITAL_AFFAIR;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }
}
