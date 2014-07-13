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

    private static Connection connection;

    private static synchronized void initStatementIfNecessary(Connection connection) throws SQLException {

        // Delay initialisation of prepared statement until the first call, after the database properties have been set.
        if (connection != DBPerson.connection || get_person_statement == null) {

            String person_query = "SELECT * FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PERSON_TABLE_NAME + " WHERE id = ?";
            get_person_statement = connection.prepareStatement(person_query);
        }

        DBPerson.connection = connection;
    }

    public static void closeCachedConnection() throws SQLException {

        if (get_person_statement != null){
        get_person_statement.close();}
    }

    public DBPerson(final ResultSet result_set) throws SQLException {

        init(result_set);
    }

    public DBPerson(final Connection connection, final int person_id) throws SQLException {

        // The connection passed for the first partnership will be reused for others.
        initStatementIfNecessary(connection);

        get_person_statement.setInt(1, person_id);
        final ResultSet result_set = get_person_statement.executeQuery();

        if (!result_set.first()) {
            throw new SQLException("can't find person id: " + person_id);
        }

        init(result_set);
    }

    private void init(ResultSet result_set) throws SQLException {

        init(
                result_set.getInt(PopulationProperties.PERSON_FIELD_ID),
                result_set.getString(PopulationProperties.PERSON_FIELD_NAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_SURNAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_GENDER).charAt(0),
                result_set.getDate(PopulationProperties.PERSON_FIELD_BIRTH_DATE),
                result_set.getDate(PopulationProperties.PERSON_FIELD_DEATH_DATE),
                result_set.getString(PopulationProperties.PERSON_FIELD_OCCUPATION),
                result_set.getString(PopulationProperties.PERSON_FIELD_CAUSE_OF_DEATH),
                result_set.getString(PopulationProperties.PERSON_FIELD_ADDRESS));
    }

    private void init(final int id, final String first_name, final String surname, final char sex, final Date date_of_birth, final Date date_of_death, final String occupation, final String cause_of_death, final String address) {

        this.id = id;
        this.first_name = first_name;
        this.surname = surname;
        this.sex = sex;
        this.date_of_birth = date_of_birth;
        this.date_of_death = date_of_death;
        this.occupation = occupation;
        this.cause_of_death = cause_of_death;
        this.address = address;

        string_rep = "DB Person: " + id;
    }

//    /**
//     * @return the family to which this person belongs
//     * i.e. the partnership of which this Person is a child.
//     */
//    public DBBackedPartnership getParentsFamily() {
//
//        try {
//            return DBBackedPartnershipFactory.createDBBackedPartnershipForChild(connection, getId());
//        } catch (final SQLException e) {
//            ErrorHandling.error("Cannot get parents' family");
//            return null;
//        }
//    }

    /**
     * @return the person's immediate family - i.e. the family of which this person is a husband or wife
     */
//    public DBBackedPartnership getFamily() {
//
//        try {
//            return DBBackedPartnershipFactory.createDBBackedPartnershipFromSpouse(connection, getId());
//        } catch (final SQLException e) {
//            ErrorHandling.error("Cannot get family");
//            return null;
//        }
//    }
}
