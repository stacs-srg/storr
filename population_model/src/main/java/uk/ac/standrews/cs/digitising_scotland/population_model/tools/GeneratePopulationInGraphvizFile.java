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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulationAdapter;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.graphviz.PopulationToGraphviz;

import java.io.IOException;

/**
 * Manual test of Graphviz export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GeneratePopulationInGraphvizFile {

    private static final String FILE_FLAG = "f";
    private static final String SIZE_FLAG = "s";

    public static void main(final String[] args) throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        String path_string = CommandLineArgs.getArg(args, FILE_FLAG);
        String population_size_string = CommandLineArgs.getArg(args, SIZE_FLAG);

        if (path_string != null && population_size_string != null) {

            int population_size = Integer.parseInt(population_size_string);
            final CompactPopulation population = new CompactPopulation(population_size);
            final IPopulation population_interface = new CompactPopulationAdapter(population);

            final PopulationToGraphviz exporter = new PopulationToGraphviz(population_interface, path_string);
            System.out.println("exporting...");

            exporter.export();
            System.out.println("done");
        }
        else {
            usage();
        }
    }

    private static void usage() {

        System.out.println("Usage: java GeneratePopulationInGraphvizFile -f<file path> -s<population size>");
    }
}
