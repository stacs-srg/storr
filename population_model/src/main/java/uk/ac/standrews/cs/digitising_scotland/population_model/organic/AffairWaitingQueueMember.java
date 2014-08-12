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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AffairWithMarriedOrSingleDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;

public class AffairWaitingQueueMember implements Comparable<AffairWaitingQueueMember>{

	OrganicPerson person;
	int affairDay;
	boolean interMarital;
	
    private static AffairWithMarriedOrSingleDistribution affairWithMarriedOrSingleDistribution;

	public static void initialiseAffairWithMarrieadOrDingleDistribution(OrganicPopulation population, String distributionKey, Random random) {
		affairWithMarriedOrSingleDistribution = new AffairWithMarriedOrSingleDistribution(population, distributionKey, random);
	}
	
    
	public AffairWaitingQueueMember(OrganicPerson person, int affairDay) {
		this.person = person;
		this.affairDay = affairDay;
		switch (affairWithMarriedOrSingleDistribution.getSample()) {
		    case SINGLE_AFFAIR:
		    	this.interMarital = false;
			    break;
		    case INTER_MARITAL_AFFAIR:
		    	this.interMarital = true;
		    	break;
		}	
	}

	@Override
	public int compareTo(AffairWaitingQueueMember o) {
		if (affairDay < o.affairDay) {
			return -1;
		} else if (affairDay == o.affairDay) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
