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

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.DivorceReason;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * Distribution modelling instigation of divorce by gender.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalDivorceReasonFemaleDistribution extends TemporalDistribution<DivorceReason> {

	/**
	 * Creates a divorce instigated by gender distribution.
	 *
	 * @param random the random number generator to be used
	 */
	public TemporalDivorceReasonFemaleDistribution(OrganicPopulation population, String distributionKey, final Random random) {
		super(population, distributionKey, random);
	}

	@Override
	public DivorceReason getSample(int date) {
		switch (getIntSample(date)) {
		case 0:
            return DivorceReason.ADULTERY;
        case 1:
            return DivorceReason.BEHAVIOUR;
        case 2:
            return DivorceReason.DESERTION;
        case 3:
        	return DivorceReason.SEPARATION_WITH_CONSENT;
        case 4:
        	return DivorceReason.SEPARATION;
        default:
            throw new RuntimeException("unexpected sample value");
		}
	}

	@Override
	public DivorceReason getSample() {
		return getSample(0);
	}


}
