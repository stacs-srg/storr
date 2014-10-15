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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NoPermissableValueException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * Provides an Integer based temporal distribution class.
 * @see TemporalDistribution
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalIntegerDistribution extends TemporalDistribution<Integer> {

    /**
     * Constructs an Integer based TemporalDistribution. 
     * 
     * @param population The instance of the population which the distribution pertains to.
     * @param distributionKey The key specified in the config file as the location of the relevant file.
     * @param random The random to be used.
     * @param handleNoPermissibleValueAsZero Indicates if the distribution is to treat the returning of NoPermissibleValueExceptions as returning a zero value.
     * @see TemporalDistribution
     */
    public TemporalIntegerDistribution(final OrganicPopulation population, final String distributionKey, final Random random, final boolean handleNoPermissableValueAsZero) {
        super(population, distributionKey, random, handleNoPermissableValueAsZero);
    }

    @Override
    public Integer getSample(final int date) {
        return getIntSample(date);
    }

    @Override
    public Integer getSample() {
        return getSample(0);
    }

    @Override
    public Integer getSample(final int date, final int earliestValue, final int latestValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException {
        return getIntSample(date, earliestValue, latestValue);
    }

}
