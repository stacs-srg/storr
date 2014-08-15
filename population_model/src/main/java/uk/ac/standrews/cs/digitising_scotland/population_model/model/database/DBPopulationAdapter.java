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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.database;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides an abstract population interface over a database representation.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
public class DBPopulationAdapter implements IPopulation {

    private String description;

    private final Connection connection;
    private final PreparedStatement person_select_statement;
    private final PreparedStatement partnership_select_statement;

    /**
     * Initialises an adapter using the standard database as specified in {@link uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties}.
     *
     * @throws SQLException if the population cannot be accessed
     */
    public DBPopulationAdapter() throws SQLException {

        final String get_people_query = getAllQuery(PopulationProperties.PERSON_TABLE_NAME);
        final String get_partnerships_query = getAllQuery(PopulationProperties.PARTNERSHIP_TABLE_NAME);

        connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();

        person_select_statement = connection.prepareStatement(get_people_query);
        partnership_select_statement = connection.prepareStatement(get_partnerships_query);
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException if the database connection cannot be closed
     */
    public void close() throws SQLException {

        person_select_statement.close();
        partnership_select_statement.close();
        connection.close();
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

    @Override
    public void setConsistentAcrossIterations(final boolean consistent_across_iterations) {

        // Ignored, since this implementation does not perform caching.
    }

    @Override
    public Iterable<IPerson> getPeople() {

        return new Iterable<IPerson>() {

            @Override
            public Iterator<IPerson> iterator() {

                return new PersonIterator();
            }

            class PersonIterator implements Iterator<IPerson> {

                private final ResultSet person_result_set;
                private final boolean person_table_empty;

                PersonIterator() {

                    try {
                        person_result_set = person_select_statement.executeQuery();
                        person_table_empty = !person_result_set.first();

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error retrieving people from database: " + e.getMessage());
                    }
                }

                @Override
                public boolean hasNext() {

                    try {
                        return !person_table_empty && !person_result_set.isAfterLast();

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error retrieving people from database: " + e.getMessage());
                    }
                }

                @Override
                public IPerson next() {

                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }

                    try {
                        final IPerson result = new DBPerson(connection, person_result_set);
                        person_result_set.next();
                        return result;

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error creating person: " + e.getMessage());
                    }
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }
            }
        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        return new Iterable<IPartnership>() {

            @Override
            public Iterator<IPartnership> iterator() {

                return new PartnershipIterator();
            }

            class PartnershipIterator implements Iterator<IPartnership> {

                ResultSet partnership_result_set;
                boolean partnership_table_empty;

                PartnershipIterator() {

                    try {
                        partnership_result_set = partnership_select_statement.executeQuery();
                        partnership_table_empty = !partnership_result_set.first();

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error retrieving partnerships from database: " + e.getMessage());
                    }
                }

                @Override
                public boolean hasNext() {

                    try {
                        return !partnership_table_empty && !partnership_result_set.isAfterLast();

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error retrieving people from database: " + e.getMessage());
                    }
                }

                @Override
                public IPartnership next() {

                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }

                    try {
                        final IPartnership result = new DBPartnership(connection, partnership_result_set);
                        partnership_result_set.next();
                        return result;

                    } catch (final SQLException e) {
                        throw new RuntimeException("Error creating partnership: " + e.getMessage());
                    }
                }

                @Override
                public void remove() {

                    throw new UnsupportedOperationException("remove");
                }
            }
        };
    }

    @Override
    public IPerson findPerson(final int id) {

        try {
            return new DBPerson(connection, id);

        } catch (final SQLException e) {
            return null;
        }
    }

    @Override
    public IPartnership findPartnership(final int id) {

        try {
            return new DBPartnership(connection, id);

        } catch (final SQLException e) {
            return null;
        }
    }

    @Override
    public int getNumberOfPeople() throws SQLException {

        return DBManipulation.countRows(connection, PopulationProperties.getDatabaseName(), PopulationProperties.PERSON_TABLE_NAME);
    }

    @Override
    public int getNumberOfPartnerships() throws SQLException {

        return DBManipulation.countRows(connection, PopulationProperties.getDatabaseName(), PopulationProperties.PARTNERSHIP_TABLE_NAME);
    }

    private static String getAllQuery(final String table_name) {

        return "SELECT * FROM " + PopulationProperties.getDatabaseName() + '.' + table_name;
    }
}
