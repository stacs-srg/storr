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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.database.old;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBConnector;
import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Provides iteration over partnerships in the database.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PartnershipIterator implements Iterator<DBBackedPartnership>, Iterable<DBBackedPartnership>, AutoCloseable {

    private final ResultSet result_set;
    private Connection connection;
    private Statement statement;
    private boolean empty;

    public PartnershipIterator() throws SQLException {

        System.out.println("creating partnership iterator");
        connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();
        statement = connection.createStatement();

        result_set = statement.executeQuery("SELECT * FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME);

        empty = !result_set.first();
    }

    @Override
    public Iterator<DBBackedPartnership> iterator() {
        return this;
    }

    @Override
    public void close() throws SQLException {

        connection.close();
        result_set.close();
    }

    @Override
    public boolean hasNext() {

        try {
            return !empty && !result_set.isAfterLast();

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error iterating over result set");
            return false;
        }
    }

    @Override
    public DBBackedPartnership next() {

        try {
            final DBBackedPartnership result = DBBackedPartnershipFactory.createDBBackedPartnershipFromPartnershipRS(connection, result_set);
            result_set.next();
            return result;

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error creating partnership");
            return null;
        }
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet size_result = statement.executeQuery("SELECT COUNT(*) FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME);

            if (!size_result.first()) {
                throw new SQLException("No rows returned in partnership count");
            }
            return size_result.getInt(1);
        }
    }
}
