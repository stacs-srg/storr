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

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.DivorceInstigation;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * Distribution modelling instigation of divorce by gender.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalDivorceInstigatedByGenderDistribution extends TemporalDistribution<DivorceInstigation> {

	/**
	 * Creates a divorce instigated by gender distribution.
	 *
	 * @param random the random number generator to be used
	 */
	public TemporalDivorceInstigatedByGenderDistribution(OrganicPopulation population, String distributionKey, final Random random) {
		super(population, distributionKey, random, false);
	}

	@Override
	public DivorceInstigation getSample(int date) {
		switch (getIntSample(date)) {
		case 0:
			return DivorceInstigation.MALE;
		case 1:
			return DivorceInstigation.FEMALE;
		case 2:
			return DivorceInstigation.NO_DIVORCE;
		default:
			throw new RuntimeException("unexpected sample value");
		}
	}

	@Override
	public DivorceInstigation getSample() {
		return getSample(0);
	}

    @Override
    public DivorceInstigation getSample(int date, int earliestValue, int latestValue) {
        return getSample(date);
    }


}
