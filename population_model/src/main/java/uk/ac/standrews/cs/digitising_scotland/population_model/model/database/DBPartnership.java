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
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBPartnership extends AbstractPartnership {

    private static PreparedStatement get_partners_statement = null;
    private static PreparedStatement get_partner_statement = null;
    private static PreparedStatement get_partnership_statement = null;
    private static PreparedStatement get_children_statement = null;

    private static Connection connection;

    /**
     * Assumes result set contains rows from Partnership table.
     *
     * @param connection
     * @param result_set
     * @throws SQLException
     */
    public DBPartnership(final Connection connection, final ResultSet result_set) throws SQLException {

        this(connection, result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_ID));
    }

    public static void closeCachedConnection() throws SQLException {

        if (get_partners_statement != null) {

            get_partners_statement.close();
            get_partner_statement.close();
            get_partnership_statement.close();
            get_children_statement.close();
        }
    }

    public DBPartnership(final Connection connection, final int id) throws SQLException {

        // The connection passed for the first partnership will be reused for others.
        initStatementsIfNecessary(connection);

        this.id = id;

        setPartners(id);
        setMarriageDate(id);
        setChildren(id);
    }

    private void setPartners(int id) throws SQLException {

        // Get the ids of the two partners.

        get_partners_statement.setInt(1, id);
        final ResultSet partners_result_set = get_partners_statement.executeQuery();

        if (!partners_result_set.first()) {
            throw new SQLException("can't find partnership id: " + id);
        }

        int partner1_id = partners_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);
        partners_result_set.next();
        int partner2_id = partners_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);

        // Work out the sexes.

        get_partner_statement.setInt(1, partner1_id);
        final ResultSet partner_result_set = get_partner_statement.executeQuery();

        if (!partner_result_set.first()) {
            throw new SQLException("can't find partner id: " + partner1_id);
        }

        boolean partner1_is_female = partner_result_set.getString(PopulationProperties.PERSON_FIELD_GENDER).charAt(0) == IPerson.FEMALE;

        female_partner_id = partner1_is_female ? partner1_id : partner2_id;
        male_partner_id = partner1_is_female ? partner2_id : partner1_id;
    }

    private void setMarriageDate(int id) throws SQLException {

        get_partnership_statement.setInt(1, id);
        final ResultSet partnership_result_set = get_partnership_statement.executeQuery();

        if (partnership_result_set.first()) {
            marriage_date = partnership_result_set.getDate(PopulationProperties.PARTNERSHIP_FIELD_DATE);
        }
    }

    private void setChildren(int id) throws SQLException {

        get_children_statement.setInt(1, id);
        final ResultSet children_result_set = get_children_statement.executeQuery();

        children = new ArrayList<>();
        while (children_result_set.next()) {
            children.add(children_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID));
        }
    }

    private static synchronized void initStatementsIfNecessary(Connection connection) throws SQLException {

        // Delay initialisation of prepared statement until the first call, after the database properties have been set.
        if (connection != DBPartnership.connection || get_partners_statement == null) {

            String database_name = PopulationProperties.getDatabaseName();

            String generic_query = "SELECT * FROM %1$s.%2$s WHERE %3$s = ?";

            String partners_query = String.format(
                    generic_query,
                    database_name,
                    PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID);

            String partner_query = String.format(
                    generic_query,
                    database_name,
                    PopulationProperties.PERSON_TABLE_NAME,
                    PopulationProperties.PERSON_FIELD_ID);

            String partnership_query = String.format(
                    generic_query,
                    database_name,
                    PopulationProperties.PARTNERSHIP_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_ID);

            String children_query = String.format(
                    generic_query,
                    database_name,
                    PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME,
                    PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID
                    );

            get_partners_statement = connection.prepareStatement(partners_query);
            get_partner_statement = connection.prepareStatement(partner_query);
            get_partnership_statement = connection.prepareStatement(partnership_query);
            get_children_statement = connection.prepareStatement(children_query);
        }

        DBPartnership.connection = connection;
    }
}
