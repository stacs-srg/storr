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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.InconsistentWeightException;

public class EnumeratedDistributionTestManual {

    public static void main(final String[] args) throws IOException, InconsistentWeightException {

        Map<String, Double> item_probabilities = new HashMap<>();

        item_probabilities.put("quick", 0.1);
        item_probabilities.put("brown", 0.1);
        item_probabilities.put("fox", 0.8);

        final EnumeratedDistribution distribution = new EnumeratedDistribution(item_probabilities, new Random());

        for (int i = 0; i < 30; i++) {
            System.out.println(distribution.getSample());
        }
    }
}
