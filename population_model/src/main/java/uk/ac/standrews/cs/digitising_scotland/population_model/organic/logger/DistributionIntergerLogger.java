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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.RestrictedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class DistributionIntergerLogger extends DistributionLogger<Integer> {

    
    
    public DistributionIntergerLogger(RestrictedDistribution<Integer> relatedDistribution, int minStatedValue, int maxStatedValue) {
        this.minXValue = minStatedValue;
        this.maxXValue = maxStatedValue;
        this.relatedDistribution = relatedDistribution;
        counts = new int[maxXValue - minXValue + 1];
    }
    
    public void incCountFor(Integer xLabel) {
        if (xLabel > maxXValue) {
            System.err.println("Array Index Out Of Bounds");
            return;
        }
        counts[xLabel]++;
    }
    
    @Override
    public String generateGnuPlotPlottingScript() {
        return "plot \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" using 1:2 title 'Actual' with line, \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" using 1:3 title 'Dist' with line";
    }
    
    public void outputToGnuPlotFormat(int year, String fileName, boolean convertDaysToYears) {
        PrintWriter writer;
        int[] distWeights = relatedDistribution.getWeights();
        int[] storedCounts = counts;
        if (distWeights.length < counts.length) {
            int c = 0;
            int[] temp = new int[distWeights.length];
            for (int i : counts) {
                temp[(int) ((c++ / (double) counts.length) * distWeights.length)] += i;
            }
            counts = temp;
        }
        
        int sumOfDistWeights = 0;
        for (int i : distWeights) {
            sumOfDistWeights += i;
        }
        
        int sumOfCounts = 0;
        for (int i : counts) {
            sumOfCounts += i;
        }
        if (sumOfCounts == 0) {
            sumOfCounts = sumOfDistWeights;
        }
        
        double scaleFactor = sumOfCounts / (double) sumOfDistWeights;
        
        try {
            filePath = "src/main/resources/output/gnu/" + fileName + "_" + ((int) (year / OrganicPopulation.getDaysPerYear()) + OrganicPopulation.getEpochYear()) + ".dat";
            writer = new PrintWriter(filePath, "UTF-8");
            filePath = new File("").getAbsolutePath() + "/" + filePath;
            writer.println("# This file is called " + fileName + ".dat");
            writer.println("# Value    Actual    Distribution");
            double rangeStep = (relatedDistribution.getMaximumReturnValue() - relatedDistribution.getMinimumReturnValue() + 1) / (double) counts.length;
            for (int i = 0; i < counts.length; i++) {
                if (convertDaysToYears) {
                    writer.printf("%.2f", (rangeStep * i + relatedDistribution.getMinimumReturnValue()) / OrganicPopulation.getDaysPerYear());
                } else {
                    writer.printf("%.2f", rangeStep * i + relatedDistribution.getMinimumReturnValue());
                }
                writer.print("    " + counts[i] + "    ");
                writer.printf("%.2f", distWeights[i] * scaleFactor);
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("_________CAUGHT__________");
            System.out.println(e.getMessage());
        }
        counts = storedCounts;
        
    }

    @Override
    public void incCountFor(Enum<?> xLabel) throws NotSetUpAtClassInitilisationException {
        throw new NotSetUpAtClassInitilisationException();
    }
    
}
