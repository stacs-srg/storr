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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.database;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.GeneralPopulationStructureTests;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationConverter;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulationTestCases;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by graham on 07/07/2014.
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GeneralDBPopulationTest extends GeneralPopulationStructureTests {

    public static final Random RANDOM = RandomFactory.getRandom();

    private String database_name;
    private Connection connection;
    private IPopulation original_population;
    private DBPopulationWriter population_writer;
    private DBPopulationAdapter population_typed_as_db;

    // The name string gives informative labels in the JUnit output.
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() throws Exception {

        // Use each of the compact population test cases to create a database test population.
        return getDBTestCases(CompactPopulationTestCases.getTestPopulations());
    }

    public GeneralDBPopulationTest(IPopulation population) throws Exception {

        super();
        original_population = population;
    }

    @Before
    public void setUp() throws Exception {

        initialiseDatabase();
        writeCompactPopulationToDatabase();

        population_typed_as_db = new DBPopulationAdapter();
        population = population_typed_as_db;
    }

    private void initialiseDatabase() throws SQLException {

        database_name = "population" + Math.abs(RANDOM.nextInt());
        PopulationProperties.setDatabaseName(database_name);

        connection = new DBConnector().createConnection();
        DBManipulation.dropDatabase(connection, database_name);
        new DBInitialiser().setupDB();
    }

    private void writeCompactPopulationToDatabase() throws Exception {

        population_writer = new DBPopulationWriter();
        new PopulationConverter(original_population, population_writer).convert();
    }

    @After
    public void tearDown() throws SQLException {

        DBPerson.closeCachedConnection();
        DBPartnership.closeCachedConnection();
        DBManipulation.dropDatabase(connection, database_name);

        connection.close();
        population_typed_as_db.close();
        population_writer.close();
    }

    @Test
    public void populationsEquivalent() throws Exception {

        assertEquals(population.getNumberOfPeople(), original_population.getNumberOfPeople());
        assertEquals(population.getNumberOfPartnerships(), original_population.getNumberOfPartnerships());

        assertSamePeopleIds(population.getPeople(), original_population.getPeople());
        assertSamePartnershipIds(population.getPartnerships(), original_population.getPartnerships());
        assertSamePartners(population, original_population);
        assertSameChildren(population, original_population);
    }

    private void assertSamePartners(IPopulation population, IPopulation original_population) {

        for (IPartnership partnership1 : population.getPartnerships()) {

            IPartnership partnership2 = original_population.findPartnership(partnership1.getId());
            assertTrue(partnership1.getMalePartnerId() == partnership2.getMalePartnerId());
            assertTrue(partnership1.getFemalePartnerId() == partnership2.getFemalePartnerId());
        }
    }

    private void assertSameChildren(IPopulation population, IPopulation original_population) {

        for (IPartnership partnership1 : population.getPartnerships()) {

            IPartnership partnership2 = original_population.findPartnership(partnership1.getId());

            assertEqualSets(partnership1.getChildIds(), partnership2.getChildIds());
        }
    }

    private void assertEqualSets(List<Integer> set1, List<Integer> set2) {

        if (set1 == null) {
            assertNull(set2);

        } else {
            assertNotNull(set2);
            assertEquals(set1.size(), set2.size());

            for (int i : set1) {
                assertTrue(set2.contains(i));
            }
        }
    }

    private void assertSamePeopleIds(Iterable<IPerson> people1, Iterable<IPerson> people2) {

        Set<Integer> ids = new HashSet<>();
        for (IPerson person : people1) {
            ids.add(person.getId());
        }

        for (IPerson person : people2) {
            assertTrue(ids.contains(person.getId()));
        }
    }

    private void assertSamePartnershipIds(Iterable<IPartnership> partnerships1, Iterable<IPartnership> partnerships2) {

        Set<Integer> ids = new HashSet<>();
        for (IPartnership partnership : partnerships1) {
            ids.add(partnership.getId());
        }

        for (IPartnership partnership : partnerships2) {
            assertTrue(ids.contains(partnership.getId()));
        }
    }

    private static List<Object[]> getDBTestCases(IPopulation... populations) {

        List<Object[]> result = new ArrayList<>();

        for (IPopulation population : populations) {

            result.add(new Object[]{population});
        }
        return result;
    }
}
