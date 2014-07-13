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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory;

import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.GeneralPopulationStructureTests;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulationTestCases;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by graham on 07/07/2014.
 */
public class GeneralCompactPopulationTest extends GeneralPopulationStructureTests {

    // The name string gives informative labels in the JUnit output.
    @Parameterized.Parameters(name = "{0}, {1}")
    public static Collection<Object[]> generateData() throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException, ParseException {

        return expandWithBooleanOptions(CompactPopulationTestCases.getTestPopulations());
    }

    public GeneralCompactPopulationTest(IPopulation population, final boolean consistent_across_iterations) {

        super(population, consistent_across_iterations);
    }

    private static List<Object[]> expandWithBooleanOptions(IPopulation... populations) {

        List<Object[]> result = new ArrayList<>();

        for (IPopulation population : populations) {

            Object[] config1 = new Object[2];
            config1[0] = population;

            Object[] config2 = config1.clone();

            config1[config1.length - 1] = false;
            config2[config2.length - 1] = true;

            result.add(config1);
            result.add(config2);
        }
        return result;
    }
}
