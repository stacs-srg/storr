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

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPerson;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBPerson extends AbstractPerson {

    private static PreparedStatement get_person_statement = null;
    private static PreparedStatement get_parents_statement = null;

    private static Connection connection;

    private static synchronized void initStatementIfNecessary(Connection connection) throws SQLException {

        // Delay initialisation of prepared statement until the first call, after the database properties have been set.
        if (connection != DBPerson.connection || get_person_statement == null) {

            String person_query = String.format(
                    "SELECT * FROM %1$s.%2$s WHERE %3$s = ?",
                    PopulationProperties.getDatabaseName(),
                    PopulationProperties.PERSON_TABLE_NAME,
                    PopulationProperties.PERSON_FIELD_ID);

            String parents_query = String.format(
                    "SELECT %1$s FROM %2$s.%3$s WHERE %4$s = ?",
                    PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID,
                    PopulationProperties.getDatabaseName(),
                    PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);

            get_person_statement = connection.prepareStatement(person_query);
            get_parents_statement = connection.prepareStatement(parents_query);
        }

        DBPerson.connection = connection;
    }

    public static void closeCachedConnection() throws SQLException {

        if (get_person_statement != null) {
            get_person_statement.close();
        }

        if (get_parents_statement != null) {
            get_parents_statement.close();
        }
    }

    private DBPerson(final Connection connection) throws SQLException {

        // The connection will be reused for others.
        initStatementIfNecessary(connection);
    }

    public DBPerson(final Connection connection, final ResultSet result_set) throws SQLException {

        this(connection);
        init(result_set);
    }

    public DBPerson(final Connection connection, final int person_id) throws SQLException {

        this(connection);

        get_person_statement.setInt(1, person_id);
        final ResultSet person_result_set = get_person_statement.executeQuery();

        if (!person_result_set.first()) {
            throw new SQLException("can't find person id: " + person_id);
        }

        init(person_result_set);
    }

    private void init(ResultSet result_set) throws SQLException {

        int person_id = result_set.getInt(PopulationProperties.PERSON_FIELD_ID);

        int parents_partnership_id = getParentsPartnershipId(person_id);

        init(
                person_id,
                result_set.getString(PopulationProperties.PERSON_FIELD_NAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_SURNAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_GENDER).charAt(0),
                result_set.getDate(PopulationProperties.PERSON_FIELD_BIRTH_DATE),
                result_set.getDate(PopulationProperties.PERSON_FIELD_DEATH_DATE),
                result_set.getString(PopulationProperties.PERSON_FIELD_OCCUPATION),
                result_set.getString(PopulationProperties.PERSON_FIELD_CAUSE_OF_DEATH),
                result_set.getString(PopulationProperties.PERSON_FIELD_ADDRESS),
                parents_partnership_id);
    }

    private int getParentsPartnershipId(int person_id) throws SQLException {

        get_parents_statement.setInt(1, person_id);
        final ResultSet parents_result_set = get_parents_statement.executeQuery();

        if (!parents_result_set.first()) {
            return -1;
        }

        return parents_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID);
    }

    private void init(final int id, final String first_name, final String surname, final char sex, final Date date_of_birth, final Date date_of_death, final String occupation, final String cause_of_death, final String address, int parents_partnership_id) {

        this.id = id;
        this.first_name = first_name;
        this.surname = surname;
        this.sex = sex;
        this.date_of_birth = date_of_birth;
        this.date_of_death = date_of_death;
        this.occupation = occupation;
        this.cause_of_death = cause_of_death;
        this.address = address;
        this.parents_partnership_id = parents_partnership_id;

        string_rep = "DB Person: " + id;
    }
}
