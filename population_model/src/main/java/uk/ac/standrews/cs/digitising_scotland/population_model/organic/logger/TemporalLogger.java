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

import java.io.PrintWriter;
import java.util.HashMap;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public abstract class TemporalLogger<Value> {

    protected TemporalDistribution<?> relatedTemporalDistribution;
    protected HashMap<Integer, DistributionLogger<Value>> map;
    protected Integer[] keyArray;
    protected int minValue;
    protected int maxValue;
    protected String fileName;
    protected String title;
    protected String xLabel;
    protected boolean convertDaysToYearsOnOutput;
    
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
    
    public void outputToGnuPlotFormat() {
        for(Integer i : keyArray) {
            map.get(i).outputToGnuPlotFormat(i, fileName, convertDaysToYearsOnOutput);
        }
    }
    
    public void generateGnuPlotScriptLines(PrintWriter writer) {
        int c = 0;
        writer.println("set style line 11 lc rgb '#808080' lt 1");
        writer.println("set border 3 back ls 11");
        writer.println("set tics nomirror");
        writer.println("set style line 12 lc rgb '#808080' lt 0 lw 1");
        writer.println("set grid back ls 12");
        writer.println("set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 20 # --- red");
        writer.println("set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 20 # --- green");
        for(Integer i : keyArray) {
            int nextYear = 0;
            if (c < keyArray.length - 1) {
                nextYear = keyArray[++c];
            }
            writer.print("set title \"" + title + " - " + ((int) (i / OrganicPopulation.getDaysPerYear()) + OrganicPopulation.getEpochYear()) + " - ");
            if (nextYear == 0) {
                writer.println("end\"");
            } else {
                writer.println(((int) (nextYear / OrganicPopulation.getDaysPerYear()) + OrganicPopulation.getEpochYear()) + "\"");
            }
            writer.println("set ylabel \"Frequency\"");
            writer.println("set xlabel \"" + xLabel + "\"");
            writer.println(map.get(i).generateGnuPlotPlottingScript());
        }
        writer.println("unset style");
        writer.println("unset border");
        writer.println("unset tics");
        writer.println("unset grid");
    }
}
