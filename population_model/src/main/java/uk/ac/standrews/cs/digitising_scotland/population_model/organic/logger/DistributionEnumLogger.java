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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.RestrictedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class DistributionEnumLogger<Value> extends DistributionLogger<Value> {
    
    Enum<?>[] enums;
    
    public DistributionEnumLogger(RestrictedDistribution<Value> relatedDistribution, Enum<?>[] enums) {
        this.minXValue = 0;
        this.maxXValue = enums.length - 1;
        
        this.relatedDistribution = relatedDistribution;
        this.enums = enums;
        counts = new int[(int) (relatedDistribution.getMaximumReturnValue() - relatedDistribution.getMinimumReturnValue() + 1)];
    }

    public void incCountFor(Enum<?> xLabel) {
        int c = 0;
        for (Enum<?> e : enums) {
            if (e.toString().equals(xLabel.toString())) {
                break;
            }
            c++;
        }
        incCountFor(c);
        
    }

    @Override
    public String generateGnuPlotPlottingScript() {
        return "plot \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" using 3:xtic(2) ti col fc rgb '#8b1a0e', \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" u 4 ti col fc rgb '#5e9c36'";
    }

    @Override
    public void outputToGnuPlotFormat(int year, String fileName, boolean convertDaysToYears) {
        PrintWriter writer;
        int[] distWeights = relatedDistribution.getWeights();
        
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
            writer.println("Count    Value    Actual    Distribution");
            for (int i = 0; i < counts.length; i++) {
                writer.print(i + "    " + enums[i]);
                writer.print("    " + counts[i] + "    ");
                writer.printf("%.2f", distWeights[i] * scaleFactor);
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("_________CAUGHT__________");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void incCountFor(Integer xLabel) {
        if (xLabel > maxXValue) {
            System.err.println("Array Index Out Of Bounds");
            return;
        }
        counts[xLabel]++;
    }

    
}
