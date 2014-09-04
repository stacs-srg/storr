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

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class CountLogger extends GraphLogger {
    
    private int count = 0;

    public CountLogger(int minXValue, int maxXValue, String fileName, String graphTitle, String yLabel) {
        super(minXValue, maxXValue, fileName, graphTitle, yLabel);
    }
    
    public void log(int currentDay) {
        log(currentDay, count);
    }
    
    public void log(int xValue, int yValue) {
        int x = Math.round(xValue / OrganicPopulation.getDaysPerYear()) - (minXValue - 1600);
        if (x >= 0 && x < values.length) {
            values[x] = yValue;
        }
    }

    @Override
    public String generateGnuPlotPlottingScript() {
        return "plot \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" using 1:2 title '" + graphTitle + "' with line";
    }
    
    public void incCount() {
        count ++;
    }
    
    public void decCount() {
        count --;
    }

    public int getCount() {
        return count;
    }
    

}
