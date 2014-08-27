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

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

import edu.umd.cs.findbugs.bcel.OpcodeStackDetector.WithCustomJumpInfo;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public abstract class TemporalLogger<Value> {

    protected TemporalDistribution<?> relatedTemporalDistribution;
    protected HashMap<Integer, DistributionIntergerLogger> map;
    protected Integer[] keyArray;
    protected int minValue;
    protected int maxValue;
    protected String fileName;
    protected String title;
    protected String xLabel;
    
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
    
    public void printGraph() {
        for(Integer i : map.keySet()) {
            map.get(i).printGraph();
        }
    }
    
    public void outputToGnuPlotFormat() {
        for(Integer i : keyArray) {
            map.get(i).outputToGnuPlotFormat(i, fileName);
        }
    }
    
    public void generateGnuPlotScriptLines(PrintWriter writer) {
        int c = 0;
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
    }
}
