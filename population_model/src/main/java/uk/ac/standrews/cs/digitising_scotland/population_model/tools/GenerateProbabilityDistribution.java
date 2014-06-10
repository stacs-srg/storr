/**
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
package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.util.FileDistributionGenerator;

import java.io.IOException;

/**
 * Reads in a text file, and generates a file containing the probability distribution of the lines in the input file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GenerateProbabilityDistribution {

    private static final String INPUT_FILE_FLAG = "-i";
    private static final String OUTPUT_FILE_FLAG = "-o";

    public static void main(final String[] args) throws IOException {

        String input_file_path = CommandLineArgs.getArg(args, INPUT_FILE_FLAG);
        String output_file_path = CommandLineArgs.getArg(args, OUTPUT_FILE_FLAG);

        if (input_file_path != null && output_file_path != null) {

            FileDistributionGenerator analyser = new FileDistributionGenerator();
            analyser.analyseData(input_file_path, output_file_path);
        } else {
            usage();
        }
    }

    private static void usage() {

        System.out.println("Usage: java " + GenerateProbabilityDistribution.class.getSimpleName() + " -i<input file path> -o<output file path>");
    }
}
