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
package uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.ChildrenAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.DeathAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.MarriageAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.ParentAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.PopulationAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

/**
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * 
 * Tests of population operations.
 */
public class PopulationAnalyticsTestManual {

    public static void main(final String[] args) throws NegativeDeviationException, NegativeWeightException {

        final int population_size = 1000;
        final CompactPopulation population = new CompactPopulation(population_size);

        new PopulationAnalytics(population).printAllAnalytics();
        new MarriageAnalytics(population).printAllAnalytics();
        new ChildrenAnalytics(population).printAllAnalytics();
        new DeathAnalytics(population).printAllAnalytics();
        new ParentAnalytics(population).printAllAnalytics();
    }
}
