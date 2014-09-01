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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalEnumDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class TemporalEnumLogger<Value> extends TemporalLogger<Integer> {

    public TemporalEnumLogger(TemporalEnumDistribution<Value> relatedTemporalDistribution, String fileName, String graphTitle, String xLabel) {
        this.title = graphTitle;
        this.fileName = fileName;
        this.xLabel = xLabel;
        map = new HashMap<Integer, DistributionLogger<Integer>>();
        for (Integer i : relatedTemporalDistribution.getMapKeys()) {
            map.put(i, new DistributionEnumLogger(relatedTemporalDistribution.getDistributionForYear(i), relatedTemporalDistribution.getEnums()));
        }
        Set<Integer> keys = map.keySet();
        ArrayList<Integer> keyList = new ArrayList<>(keys);
        keyArray = keyList.toArray(new Integer[keyList.size()]);
        Arrays.sort(keyArray);
    }

    public void log(int currentDay, Enum<?> xLabel) {
        try {
            map.get(getKey(currentDay)).incCountFor(xLabel);
        } catch (NotSetUpAtClassInitilisationException e) {
            e.printStackTrace();
        }
    }

    public void generateGnuPlotScriptLines(PrintWriter writer) {
        int c = 0;
        writer.println("set style line 11 lc rgb '#808080' lt 1");
        writer.println("set border 3 back ls 11");
        writer.println("set tics nomirror");
        writer.println("set style line 12 lc rgb '#808080' lt 0 lw 1");
        writer.println("set grid back ls 12");
        writer.println("set ylabel \"Frequency\"");
        writer.println("set xlabel \"" + xLabel + "\"");
        writer.println("set style data histogram");
        writer.println("set style histogram cluster gap 1");
        writer.println("set style fill solid border -1");
        writer.println("set boxwidth 0.95");
        writer.println("set xtic scale 0");
        writer.println("set xtic rotate by 45 right");
        for (Integer i : keyArray) {
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
            writer.println(map.get(i).generateGnuPlotPlottingScript());
            
        }
        writer.println("unset ylabel");
        writer.println("unset xlabel");
        writer.println("unset style");
        writer.println("unset boxwidth");
        writer.println("unset xtic");
    }

}
