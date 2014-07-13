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
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by graham on 15/06/2014.
 */
public class DBTest {

    private String database_name;
    private Connection connection;

    @Before
    public void setUpAll() throws SQLException {

        database_name = "population" + Math.abs(RandomFactory.getRandom().nextInt());
        PopulationProperties.setDatabaseName(database_name);
        connection = new DBConnector().createConnection();
    }

    @After
    public void tearDownAll() throws SQLException {

        DBManipulation.dropDatabase(connection, database_name);
        assertDBDoesNotExist(database_name);
        connection.close();
    }

    @Test
    public void dbIsCreated() throws IOException, SQLException {

        new DBInitialiser().setupDB();

        assertDBExists(database_name);
    }

    @Test
    public void personTableIsCreated() throws IOException, SQLException {

        new DBInitialiser().setupDB();

        assertTableExists(database_name, PopulationProperties.PERSON_TABLE_NAME);
    }

    private void assertDBExists(String database_name) throws SQLException {

        assertTrue(DBManipulation.databaseExists(connection, database_name));
    }

    private void assertDBDoesNotExist(String database_name) throws SQLException {

        assertFalse(DBManipulation.databaseExists(connection, database_name));
    }

    private void assertTableExists(String database_name, String table_name) throws SQLException {

        assertTrue(DBManipulation.tableExists(connection, database_name, table_name));
    }
}
