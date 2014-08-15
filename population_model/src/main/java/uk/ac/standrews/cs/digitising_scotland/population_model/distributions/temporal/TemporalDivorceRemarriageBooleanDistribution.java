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

 * Created by victor on 22/07/2014.
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TemporalDivorceRemarriageBooleanDistribution extends TemporalDistribution<Boolean> {

    /**
     * Creates a Remarriage distribution.
     *
     * @param random Takes in random for use in creation of distribution.
     */
    public TemporalDivorceRemarriageBooleanDistribution(OrganicPopulation population, String distributionKey, final Random random) {
    	super(population, distributionKey, random);
    }

    @Override
    public Boolean getSample(int date) {
    	switch (getIntSample(date)) {
    	case 0:
    		return true;
    	case 1:
    		return false;
    	default:
    		throw new RuntimeException("unexpected sample value");
    	}
    }

	@Override
	public Boolean getSample() {
		return getSample(0);
	}
}
