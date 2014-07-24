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
import java.text.NumberFormat;

/**
 * Writes a representation of the population to file in some external format - specialised by subclasses.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public abstract class AbstractFilePopulationWriter implements IPopulationWriter {

    private static final int NUMBER_OF_DIGITS_IN_ID = 8;

    private static final NumberFormat formatter;

    protected abstract void outputHeader(PrintWriter writer);

    protected abstract void outputTrailer(PrintWriter writer);

    protected PrintWriter writer;

    static {

        formatter = NumberFormat.getInstance();
        formatter.setMinimumIntegerDigits(NUMBER_OF_DIGITS_IN_ID);
        formatter.setGroupingUsed(false);
    }

    /**
     * Initialises the exporter. This includes potentially expensive scanning of the population graph.
     *
     * @param path the path for the output file
     * @throws IOException if the file does not exist and cannot be created
     */
    public AbstractFilePopulationWriter(final Path path) throws IOException {


        FileManipulation.createParentDirectoryIfDoesNotExist(path);

        writer = new PrintWriter(Files.newBufferedWriter(path, FileManipulation.FILE_CHARSET));

        outputHeader(writer);
    }

    @Override
    public void close() throws Exception {

        outputTrailer(writer);
        writer.close();
    }

    protected static String individualLabel(final int person_id) {
        return 'p' + padId(person_id);
    }

    protected static String familyLabel(final int partnership_id) {
        return 'm' + padId(partnership_id);
    }

    private static String padId(final int index) {

        return formatter.format(index);
    }
}
