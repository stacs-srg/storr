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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class LoggingControl {

    public static PopulationLogger populationLogger;
    public static TemporalIntegerLogger numberOfChildrenFromAffairsDistributionLogger;
    public static TemporalIntegerLogger numberOfChildrenFromCohabitationDistributionLogger;
    public static TemporalIntegerLogger numberOfChildrenFromCohabThenMarriageDistributionLogger;
    public static TemporalIntegerLogger numberOfChildrenFromMarriagesDistributionLogger;

    public static void setUpLogger() {
        LoggingControl.numberOfChildrenFromAffairsDistributionLogger = new TemporalIntegerLogger(OrganicPartnership.getTemporalAffairNumberOfChildrenDistribution(), "ChildrenNumberOfAffairs", "Number of Children Distribution - Affairs", "Number of Children");
        LoggingControl.numberOfChildrenFromCohabitationDistributionLogger = new TemporalIntegerLogger(OrganicPartnership.getTemporalChildrenNumberOfInCohabDistribution(), "ChildrenNumberOfCohab", "Number of Children Distribution - Cohabitation", "Number of Children");
        LoggingControl.numberOfChildrenFromCohabThenMarriageDistributionLogger = new TemporalIntegerLogger(OrganicPartnership.getTemporalChildrenNumberOfInCohabThenMarriageDistribution(), "ChildrenNumberOfCohabTheMarriage", "Number of Children Distribution - Cohabitation Then Marriage", "Number of Children");
        LoggingControl.numberOfChildrenFromMarriagesDistributionLogger = new TemporalIntegerLogger(OrganicPartnership.getTemporalChildrenNumberOfInMarriageDistribution(), "ChildrenNumberOfMarriage", "Number of Children Distribution - Marriage", "Number of Children");
        LoggingControl.populationLogger = new PopulationLogger(OrganicPopulation.getStartYear(), OrganicPopulation.getEndYear(), "Population", "Population Change Over Time", "Population");
    }

    private static void output() {

        LoggingControl.numberOfChildrenFromMarriagesDistributionLogger.outputToGnuPlotFormat();
        LoggingControl.numberOfChildrenFromCohabitationDistributionLogger.outputToGnuPlotFormat();
        LoggingControl.numberOfChildrenFromCohabThenMarriageDistributionLogger.outputToGnuPlotFormat();
        LoggingControl.numberOfChildrenFromAffairsDistributionLogger.outputToGnuPlotFormat();
        LoggingControl.populationLogger.outputToGnuPlotFormat();
    }

    public static void createGnuPlotOutputFilesAndScript() {
        output();
        PrintWriter writer;
        try {
            String filePath = "src/main/resources/output/gnu/log_output_script.p";
            writer = new PrintWriter(filePath, "UTF-8");
            writer.println("# This file is called log_output_script.p");
            writer.println("reset");

            writer.println("set style line 11 lc rgb '#808080' lt 1");
            writer.println("set border 3 back ls 11");
            writer.println("set tics nomirror");
            writer.println("set style line 12 lc rgb '#808080' lt 0 lw 1");
            writer.println("set grid back ls 12");
            writer.println("set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 20 # --- red");
            writer.println("set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 20 # --- green");

            writer.println("set terminal pdf");
            writer.println("set output 'output.pdf'");
            numberOfChildrenFromAffairsDistributionLogger.generateGnuPlotScriptLines(writer);
            numberOfChildrenFromCohabitationDistributionLogger.generateGnuPlotScriptLines(writer);
            numberOfChildrenFromCohabThenMarriageDistributionLogger.generateGnuPlotScriptLines(writer);
            numberOfChildrenFromMarriagesDistributionLogger.generateGnuPlotScriptLines(writer);
            populationLogger.generateGnuPlotScriptLines(writer);
            writer.println("set terminal png");
            writer.println("reset");

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
        }
    }

}
