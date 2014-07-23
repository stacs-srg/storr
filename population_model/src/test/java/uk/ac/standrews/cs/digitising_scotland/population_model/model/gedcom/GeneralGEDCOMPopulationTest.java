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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.gedcom;

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationComparisonTest;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationConverter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by graham on 07/07/2014.
 */
public class GeneralGEDCOMPopulationTest extends PopulationComparisonTest {

    private Path path;

    public GeneralGEDCOMPopulationTest(final IPopulation population) throws Exception {

        super(population);
    }

    @Before
    public void setUp() throws Exception {

        path = Files.createTempFile(null, ".ged");
        writeCompactPopulationToGEDCOMFile();
        population = new GEDCOMPopulationAdapter(path);
    }

    private void writeCompactPopulationToGEDCOMFile() throws Exception {

        population_writer = new GEDCOMPopulationWriter(path);

        try (PopulationConverter converter = new PopulationConverter(original_population, population_writer)) {
            converter.convert();
        }
    }

    @After
    public void tearDown() throws Exception {

        Files.delete(path);
        population_writer.close();
    }
}
