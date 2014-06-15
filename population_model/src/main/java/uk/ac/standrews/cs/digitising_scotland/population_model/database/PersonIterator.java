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
package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPersonFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Provides iteration over people in the database.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PersonIterator implements Iterator<Person>, Iterable<Person>, AutoCloseable {

    private final Connection connection;
    private final Statement statement;
    private final ResultSet resultSet;

    private final boolean empty;

    public PersonIterator() throws SQLException {

        connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();
        statement = connection.createStatement();

        // TODO this seems to be expensive - try executing a specific lookup for each person
        resultSet = statement.executeQuery("SELECT * FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PERSON_TABLE_NAME);

        empty = !resultSet.first();
    }

    @Override
    public Iterator<Person> iterator() {
        return this;
    }

    @Override
    public void close() throws SQLException {

        connection.close();
        resultSet.close();
    }

    @Override
    public boolean hasNext() {

        try {
            return !empty && !resultSet.isAfterLast();

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error iterating over result set");
            return false;
        }
    }

    @Override
    public Person next() {

        try {
            final Person result = DBBackedPersonFactory.createDBBackedPerson(connection, resultSet);
            resultSet.next();
            return result;

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error creating person");
            return null;
        }
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() throws SQLException {

        return DBManipulation.countRows(connection, PopulationProperties.getDatabaseName(), PopulationProperties.PERSON_TABLE_NAME);
    }
}
