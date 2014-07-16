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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationToFile;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;

/**
 * Manual test of Graphviz export.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class AbstractPopulationToFile {

    protected static final String FILE_FLAG = "-f";
    protected static final String SIZE_FLAG = "-s";

    public abstract IPopulation getPopulation(String[] args) throws Exception;

    public abstract PopulationToFile getExporter(IPopulation population, String path_string);

    protected void export(String[] args) throws Exception {

        final String path_string = CommandLineArgs.getArg(args, FILE_FLAG);

        if (path_string != null) {

            System.out.println("exporting...");
            getExporter(getPopulation(args), path_string).export();
            System.out.println("done");

        } else {
            usage();
        }
    }

    private void usage() {

        System.out.println("Usage: java " + getClass().getSimpleName() + " " +
                FILE_FLAG + "<file path> " +
                SIZE_FLAG + "<population size> ");
    }
}