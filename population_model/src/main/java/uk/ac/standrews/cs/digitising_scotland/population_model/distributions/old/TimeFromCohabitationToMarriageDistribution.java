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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.old;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;

public class TimeFromCohabitationToMarriageDistribution extends NormalDistribution {

	public static final int mean = (int) (22 * PopulationLogic.DAYS_PER_YEAR) / 12;
	
	public static TimeFromCohabitationToMarriageDistribution timeFromCohabitationToMarriageDistributionFactory(Random random) {
		try {
			return new TimeFromCohabitationToMarriageDistribution(mean, mean/4, random);
		} catch (NegativeDeviationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private TimeFromCohabitationToMarriageDistribution(double mean, double standard_deviation, Random random) throws NegativeDeviationException {
		super(mean, standard_deviation, random);
	}
	
	public int getIntSample() {
		int temp;
		do {
		temp = super.getSample().intValue();
		} while (temp <= 0);
		if (temp < 0)
			System.out.println("Temp: " + temp);
		return temp;
	}
	
}
