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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;

/**
 * Writes a representation of the population to file in some external format - specialised by subclasses.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public abstract class PopulationToFile {

    private static final int NUMBER_OF_DIGITS_IN_ID = 8;

    protected final IPopulation population;
    private final String path_string;

    protected final NumberFormat formatter;

    protected abstract void outputHeader(PrintWriter writer);

    protected abstract void outputIndividual(PrintWriter writer, IPerson person);

    protected abstract void outputFamilies(PrintWriter writer);

    protected abstract void outputTrailer(PrintWriter writer);

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param population  the population
     * @param path_string the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public PopulationToFile(final IPopulation population, final String path_string) {

        this.population = population;
        this.path_string = path_string;

        formatter = NumberFormat.getInstance();
        formatter.setMinimumIntegerDigits(NUMBER_OF_DIGITS_IN_ID);
        formatter.setGroupingUsed(false);
    }

    /**
     * Exports representation of the population to file.
     */
    public final synchronized void export() throws IOException {

        Path path = Paths.get(path_string);
        FileManipulation.createParentDirectoryIfDoesNotExist(path);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path, FileManipulation.FILE_CHARSET))) {

            outputHeader(writer);
            outputIndividuals(writer);
            outputFamilies(writer);
            outputTrailer(writer);
        }
    }

    protected void outputIndividuals(final PrintWriter writer) {

        // Copy the set of people before outputting them, to avoid problems with overlapping iterations of the population.

        Collection<IPerson> people = new HashSet<>();
        for (IPerson p : population.getPeople()) {
            people.add(p);
        }

        for (IPerson p : people) {

            outputIndividual(writer, p);
        }
    }

    protected String padId(final int index) {

        return formatter.format(index);
    }
}
