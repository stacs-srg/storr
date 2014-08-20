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

/**
 * Provides a temporal distribution for partnership characteristic.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalPartnershipCharacteristicDistribution extends TemporalDistribution<FamilyType> {

    /**
     * Creates a partnership characteristic distribution.
     *
     * @param population The instance of the population which the distribution pertains to.
     * @param distributionKey The key specified in the config file as the location of the relevant file.
     * @param random the random number generator to be used.
     */
    public TemporalPartnershipCharacteristicDistribution(final OrganicPopulation population, final String distributionKey, final Random random) {
        super(population, distributionKey, random, false);
    }

    /**
     * Samples the correct distribution for the given year and returns the sampled partnership characteristic.
     *
     * @param date The date in days since the 1/1/1600 to be used to identity the distribution for the given year which should be sampled.
     * @return Indicates the gender of the instigator or no divorce
     */
    @Override
    public FamilyType getSample(final int date) {
        switch (getIntSample(date)) {
            case 0:
                return FamilyType.SINGLE;
            case 1:
                return FamilyType.COHABITATION;
            case 2:
                return FamilyType.COHABITATION_THEN_MARRIAGE;
            case 3:
                return FamilyType.MARRIAGE;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }

    @Override
    public FamilyType getSample() {
        return getSample(0);
    }

    @Override
    public FamilyType getSample(final int date, final int earliestValue, final int latestValue) {
        return getSample(date);
    }
}
