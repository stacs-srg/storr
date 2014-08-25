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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.RestrictedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class DistributionIntergerLogger extends DistributionLogger<Integer> {

    public DistributionIntergerLogger(RestrictedDistribution<Integer> relatedDistribution) {
        this.maxXValue = relatedDistribution.getMinimumReturnValue();
        this.maxXValue = relatedDistribution.getMaximumReturnValue();
        this.relatedDistribution = relatedDistribution;
        counts = new int[maxXValue - minXValue + 1];
    }
    
    @Override
    public void incCountFor(Integer xLabel) {
        if (xLabel > maxXValue) {
            System.err.println("Array Index Out Of Bounds");
            return;
        }
        counts[xLabel]++;
    }

    @Override
    public void printGraph() {
        printGraph(counts, minXValue, minXValue, false, 10);

    }
    
    protected void printGraph(int[] values, Integer xStartValue, Integer xEndValue, boolean line, int lineDepth) {
        int sum = 0;
        int max = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
            if (values[i] > max) {
                max = values[i];
            }
        }
        int oneMarkerValue = max / lineDepth;
        if (oneMarkerValue == 0) {
            oneMarkerValue = 1;
        }
        boolean[][] graph = new boolean[values.length][lineDepth];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i] / oneMarkerValue; j++) {
                if (j >= lineDepth) {
                    break;
                }
                graph[i][j] = true;
                if (line && j != 0) {
                    graph[i][j - 1] = false;
                }
            }
        }
        OrganicPopulation.writer.print(max + "|");
        for (int j = lineDepth - 1; j >= 0; j--) {
            if (j != lineDepth - 1) {
                int n = 0;
                if (j == 0) {
                    n++;
                }
                for (int i = n; i < Integer.toString(max).length(); i++) {
                    OrganicPopulation.writer.print(" ");
                }
                if (j == 0) {
                    OrganicPopulation.writer.print("0");
                }
                OrganicPopulation.writer.print("|");
            }
            for (int i = 0; i < values.length; i++) {
                if (!graph[i][j]) {
                    OrganicPopulation.writer.print(" ");
                } else if (graph[i][j]) {
                    OrganicPopulation.writer.print("@");
                }
            }
            OrganicPopulation.writer.println();
        }
        for (int i = 0; i < Integer.toString(max).length(); i++) {
            OrganicPopulation.writer.print(" ");
        }
        OrganicPopulation.writer.print(xStartValue);
        int i = xStartValue.toString().length();
        for (i = 0; i < values.length - xEndValue.toString().length(); i++) {
            OrganicPopulation.writer.print("‾");
        }
        OrganicPopulation.writer.println(xEndValue);
        OrganicPopulation.writer.println();
    }


}
