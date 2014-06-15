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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBBackedPartnershipFactory {

    public static DBBackedPartnership createDBBackedPartnershipForChild(final Connection connection, final int child_id) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME + " WHERE person_id = ?");
        statement.setInt(1, child_id);

        return getDBBackedPartnership(connection, statement);
    }

    public static DBBackedPartnership createDBBackedPartnershipFromSpouse(final Connection connection, final int partner_id) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.getDatabaseName() + "." + PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME + " WHERE person_id = ?");
        statement.setInt(1, partner_id);

        return getDBBackedPartnership(connection, statement);
    }

    /**
     * @param connection an open database connection.
     * @param result_set expected to be of row type from: Partnership Table/ Table has id,date
     * @return the corresponding partnership object.
     * @throws SQLException
     */
    public static DBBackedPartnership createDBBackedPartnershipFromPartnershipRS(final Connection connection, final ResultSet result_set) throws SQLException {

        final int partnership_id = result_set.getInt("id");
        return new DBBackedPartnership(connection, partnership_id);
    }

    private static DBBackedPartnership getDBBackedPartnership(final Connection connection, final PreparedStatement statement) throws SQLException {

        final ResultSet resultSet = statement.executeQuery();

        if (resultSet.first()) {
            final int partnership_id = resultSet.getInt("partnership_id");
            return new DBBackedPartnership(connection, partnership_id);
        } else {
            // There is no marriage in this case
            return null;
        }
    }
}
