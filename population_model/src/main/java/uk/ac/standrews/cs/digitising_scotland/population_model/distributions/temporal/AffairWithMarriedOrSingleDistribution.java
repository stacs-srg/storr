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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.FamilyType;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class AffairWithMarriedOrSingleDistribution extends TemporalDistribution<FamilyType> {

    /**
     * Creates a divorce reason by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public AffairWithMarriedOrSingleDistribution(OrganicPopulation population, String distributionKey, Random random) {
		super(population, distributionKey, random);
    }

    /**
     * Returns the reason for divorce.
     *
     * @return Indicates the gender of the instigator or no divorce
     */
    @Override
    public FamilyType getSample(int date) {

        switch (getIntSample(date)) {
            case 0:
                return FamilyType.SINGLE_AFFAIR;
            case 1:
                return FamilyType.INTER_MARITAL_AFFAIR;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }

	@Override
	public FamilyType getSample() {
		return getSample(0);
	}

    @Override
    public FamilyType getSample(int date, int earliestValue, int latestValue) {
        return getSample(date);
    }
}
