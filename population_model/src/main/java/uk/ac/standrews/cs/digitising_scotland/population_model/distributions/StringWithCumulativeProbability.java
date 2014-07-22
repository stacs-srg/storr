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

/**
 * Provides the functionality of a sting with associated cumulative probability.
 *
 */
public class StringWithCumulativeProbability {

    private final String item;
    private final Double cumulative_probability;

    /**
     * Creates a cumulative probability string.
     * 
     * @param item
     * @param cumulative_probability
     */
    public StringWithCumulativeProbability(final String item, final double cumulative_probability) {

        this.item = item;
        this.cumulative_probability = cumulative_probability;
    }

    /**
     * Returns item string.
     * @return
     */
    public String getItem() {
        return item;
    }

    /**
     * Returns the cumulative probability petaining to the given item string.
     * @return
     */
    public Double getCumulativeProbability() {
        return cumulative_probability;
    }
}
