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
package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulationWriter;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.gedcom.GEDCOMPopulationWriter;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulationAdapter;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Manual test of Graphviz export.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GenerateCompactPopulationInGEDCOMFile extends AbstractPopulationToFile {

    private static final int DEFAULT_SIZE = 100;

    @Override
    public IPopulation getPopulation(final String[] args) throws Exception {

        final int population_size = CommandLineArgs.extractIntFromCommandLineArgs(args, SIZE_FLAG, DEFAULT_SIZE);
        return new CompactPopulationAdapter(population_size);
    }

    @Override
    public IPopulationWriter getPopulationWriter(final Path path, final IPopulation population) throws IOException {

        return new GEDCOMPopulationWriter(path);
    }

    public static void main(final String[] args) throws Exception {

        new GenerateCompactPopulationInGEDCOMFile().export(args);
    }
}
