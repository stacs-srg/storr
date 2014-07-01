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
package uk.ac.standrews.cs.digitising_scotland.population_model.tools.old;

import org.gedcom4j.parser.GedcomParserException;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.old.GEDCOMToDBWriter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Reads in a GEDCOM file and populates a database.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class LoadDBFromGEDCOMFile {

    private static final String FILE_FLAG = "f";

    public static void main(final String[] args) throws SQLException, IOException, GedcomParserException, ParseException {

        String file_path = CommandLineArgs.getArg(args, FILE_FLAG);
        if (file_path != null) {

            try (GEDCOMToDBWriter loader = new GEDCOMToDBWriter(file_path)) {

                final int number_of_people_imported = loader.importPeople();
                final int number_of_families_imported = loader.importFamilies();

                System.out.println("Imported " + number_of_people_imported + " people.");
                System.out.println("Imported " + number_of_families_imported + " families.");
            }
        }
        else {
            usage();
        }
    }

    private static void usage() {

        System.out.println("Usage: java " + LoadDBFromGEDCOMFile.class.getSimpleName() + " -f<file path>");
    }
}