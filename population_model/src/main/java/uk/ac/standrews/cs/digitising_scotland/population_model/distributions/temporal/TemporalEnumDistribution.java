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

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * Provides a temporal distribution for partnership characteristic.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalEnumDistribution<Value> extends TemporalDistribution<Value> {

    /**
     * Creates a partnership characteristic distribution.
     *
     * @param population The instance of the population which the distribution pertains to.
     * @param distributionKey The key specified in the config file as the location of the relevant file.
     * @param random the random number generator to be used.
     */
    public TemporalEnumDistribution(final OrganicPopulation population, final String distributionKey, final Random random, final Enum[] enums) {
        super(population, distributionKey, random, false);
        this.enums = enums;
    }

    /**
     * Samples the correct distribution for the given year and returns the sampled partnership characteristic.
     *
     * @param date The date in days since the 1/1/1600 to be used to identity the distribution for the given year which should be sampled.
     * @return Indicates the gender of the instigator or no divorce
     */
    @SuppressWarnings("unchecked")
    @Override
    public Value getSample(final int date) {
        return (Value) enums[getIntSample(date)];
    }

    @Override
    public Value getSample() {
        return getSample(0);
    }

    @Override
    public Value getSample(final int date, final int earliestValue, final int latestValue) {
        return getSample(date);
    }
}
