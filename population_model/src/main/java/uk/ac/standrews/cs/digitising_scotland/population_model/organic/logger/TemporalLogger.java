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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger;

import java.util.HashMap;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDistribution;

public abstract class TemporalLogger<Value> {

    protected TemporalDistribution<?> relatedTemporalDistribution;
    protected HashMap<Integer, DistributionIntergerLogger> map;
    protected Integer[] keyArray;
    
    protected int getKey(final int date) {
        int key = keyArray[keyArray.length - 1];
        if (keyArray[0] > date) {
            key = keyArray[0];
        }
        for (int i = 0; i < keyArray.length - 1; i++) {
            if (keyArray[i] < date && date < keyArray[i + 1]) {
                key = keyArray[i];
                break;
            }
        }
        return key;
    }
    
}
