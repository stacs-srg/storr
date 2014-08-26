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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;

public class TemporalIntegerLogger extends TemporalLogger<Integer> {

    public TemporalIntegerLogger(TemporalIntegerDistribution relatedTemporalDistribution) {
        map = new HashMap<Integer, DistributionIntergerLogger>();
        for (Integer i : relatedTemporalDistribution.getMapKeys()) {
            map.put(i, new DistributionIntergerLogger(relatedTemporalDistribution.getDistributionForYear(i), relatedTemporalDistribution.getMinimumStatedValue(), relatedTemporalDistribution.getMaximumStatedValue()));
        }
        Set<Integer> keys = map.keySet();
        ArrayList<Integer> keyList = new ArrayList<>(keys);
        keyArray = keyList.toArray(new Integer[keyList.size()]);
        Arrays.sort(keyArray);
    }
    
    public void log(int currentDay, int xLabel) {
        map.get(getKey(currentDay)).incCountFor(xLabel);
    }
    
}
