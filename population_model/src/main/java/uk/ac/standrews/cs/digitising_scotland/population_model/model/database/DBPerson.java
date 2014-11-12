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
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPerson;

import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Tom Dalton
 */
public class DBPerson extends AbstractPerson {

    private static PreparedStatement get_person_statement = null;
    private static PreparedStatement get_parents_statement = null;
    private static PreparedStatement get_partnerships_statement = null;

    private static Connection connection;

    private static synchronized void initStatementIfNecessary(final Connection connection) throws SQLException {

        // Delay initialisation of prepared statement until the first call, after the database properties have been set.
        if (connection != DBPerson.connection || get_person_statement == null) {

            final String person_query = String.format(
                    "SELECT * FROM %1$s.%2$s WHERE %3$s = ?",
                    PopulationProperties.getDatabaseName(),
                    PopulationProperties.PERSON_TABLE_NAME,
                    PopulationProperties.PERSON_FIELD_ID);

            final String parents_query = String.format(
                    "SELECT %1$s FROM %2$s.%3$s WHERE %4$s = ?",
                    PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID,
                    PopulationProperties.getDatabaseName(),
                    PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);
            
            final String partnerships_query = String.format(
                    "SELECT %1$s FROM %2$s.%3$s WHERE %4$s = ?",
                    PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID,
                    PopulationProperties.getDatabaseName(),
                    PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);

            get_person_statement = connection.prepareStatement(person_query);
            get_parents_statement = connection.prepareStatement(parents_query);
            get_partnerships_statement = connection.prepareStatement(partnerships_query);
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

    private void init(final ResultSet result_set) throws SQLException {

        final int person_id = result_set.getInt(PopulationProperties.PERSON_FIELD_ID);

        final int parents_partnership_id = getParentsPartnershipId(person_id);
        
        final List<Integer> partnership_ids = getPartnershipIds(person_id);

        init(
                person_id,
                result_set.getString(PopulationProperties.PERSON_FIELD_NAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_SURNAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_GENDER).charAt(0),
                result_set.getDate(PopulationProperties.PERSON_FIELD_BIRTH_DATE),
                result_set.getString(PopulationProperties.PERSON_FIELD_BIRTH_PLACE),
                result_set.getDate(PopulationProperties.PERSON_FIELD_DEATH_DATE),
                result_set.getString(PopulationProperties.PERSON_FIELD_DEATH_PLACE),
                result_set.getString(PopulationProperties.PERSON_FIELD_DEATH_CAUSE),
                result_set.getString(PopulationProperties.PERSON_FIELD_OCCUPATION),
                parents_partnership_id,
                partnership_ids);
    }

    private static int getParentsPartnershipId(final int person_id) throws SQLException {

        get_parents_statement.setInt(1, person_id);
        try (final ResultSet parents_result_set = get_parents_statement.executeQuery()) {

            if (!parents_result_set.first()) {
                return -1;
            }

            return parents_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID);
        }
    }
    
    private static List<Integer> getPartnershipIds(final int person_id) throws SQLException {

        get_partnerships_statement.setInt(1, person_id);
        try (final ResultSet partnerships_result_set = get_partnerships_statement.executeQuery()) {

            if (!partnerships_result_set.first()) {
                return null;
            }
            
            List<Integer> l = new ArrayList<Integer>();
            
            do {
            	l.add(partnerships_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID));
            } while(partnerships_result_set.next());
            
            return l;
        }
    }

    private void init(final int id, final String first_name, final String surname, final char sex, final Date birth_date, final String birth_place, final Date death_date, final String death_place, final String death_cause, final String occupation, final int parents_partnership_id, final List<Integer> partnership_ids) {

        this.id = id;
        this.first_name = first_name;
        this.surname = surname;
        this.sex = sex;
        this.birth_date = birth_date;
        this.birth_place = birth_place;
        this.death_date = death_date;
        this.death_place = death_place;
        this.death_cause = death_cause;
        this.occupation = occupation;
        this.parents_partnership_id = parents_partnership_id;
        this.partnerships = partnership_ids;

        string_rep = "DB Person: " + id;
    }
}
