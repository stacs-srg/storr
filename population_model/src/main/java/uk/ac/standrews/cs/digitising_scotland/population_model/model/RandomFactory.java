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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.util.Random;

/**
 * Manages use of random number generators.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class RandomFactory {

    private static final long SEED = 534534524234234L;

    private static boolean deterministic = true;
    private static boolean configured = false;

    /**
     * Gets a new random number generator.
     * If determinism is set, as defined by {@link uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties#isDeterministic()},
     * then the same seed is used every time.
     *
     * @return the random number generator
     */
    public static synchronized Random getRandom() {

        if (!configured) {

            try {
                deterministic = PopulationProperties.isDeterministic();

            } catch (final IOException e) {
                throw new RuntimeException("Couldn't read determinism flag from properties");
            }
            configured = true;
        }

        // Create a new Random instance every time, so that the user that wants determinism isn't affected
        // by previous calls to the Random instance.

        return deterministic ? new Random(SEED) : new Random();
    }
}
