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
package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests of GEDCOM export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public class PopulationToGEDCOMTest extends AbstractExporterTest {

    protected static final String INTENDED_SUFFIX = "_intended.ged";
    private static final String ACTUAL_SUFFIX = "_test.ged";

    public PopulationToGEDCOMTest(final CompactPopulation population, final String file_name) {

        super(population, file_name);
    }

    @Test
    @Ignore
    public void test() throws IOException, InconsistentWeightException {

        final Path actual_output = Paths.get(TEST_DIRECTORY_PATH_STRING, file_name_root + ACTUAL_SUFFIX);
        final Path intended_output = Paths.get(TEST_DIRECTORY_PATH_STRING, file_name_root + INTENDED_SUFFIX);

        final PopulationToGEDCOM exporter = new PopulationToGEDCOM(population, actual_output.toString());
        exporter.export();
        assertThatFilesHaveSameContent(actual_output, intended_output);
    }
}
