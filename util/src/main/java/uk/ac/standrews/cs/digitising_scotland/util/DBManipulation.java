/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility methods for SQL manipulation.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class DBManipulation {

    private static final String CREATE_DB_SYNTAX = "CREATE DATABASE IF NOT EXISTS";
    private static final String DROP_DB_SYNTAX = "DROP DATABASE";

    public static final Object NULL_DATE = new Object();

    /**
     * Configures the parameters for a given prepared statement.
     * The constant value {@link #NULL_DATE} should be used to set a date parameter to null.
     *
     * @param statement the statement to be configured
     * @param parameters a sequence of integer, string or date values
     * @throws SQLException if a value does not have one of the allowed types
     */
    public static void configurePreparedStatement(final PreparedStatement statement, final Object... parameters) throws SQLException {

        int pos = 1;

        for (Object parameter : parameters) {

            if (parameter instanceof Integer) {
                statement.setInt(pos, (Integer) parameter);
            } else if (parameter instanceof String) {
                statement.setString(pos, (String) parameter);
            } else if (parameter instanceof Date) {
                statement.setDate(pos, (Date) parameter);
            } else if (parameter == NULL_DATE) {
                statement.setDate(pos, null);
            } else if (parameter == null) {
                statement.setString(pos, "");
            } else {
                throw new SQLException("unknown parameter type: " + parameter.getClass().getSimpleName());
            }

            pos++;
        }
    }

    public static void executeStatement(final Connection connection, final String query) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    public static int countRows(Connection connection, String database_name, String table_name) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet size_result = statement.executeQuery("SELECT COUNT(*) FROM " + database_name + "." + table_name);

            if (!size_result.first()) {
                throw new SQLException("No rows returned in count");
            }
            return size_result.getInt(1);
        }
    }

    public static boolean databaseExists(Connection connection, String database_name) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet check_result = statement.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + database_name + "'");

            return check_result.first() && check_result.getString(1).equals(database_name);
        }
    }

    public static void createDatabaseIfDoesNotExist(Connection connection, String database_name) throws SQLException {

        executeStatement(connection, CREATE_DB_SYNTAX + " " + database_name);
    }

    public static void dropDatabase(Connection connection, String database_name) throws SQLException {

        executeStatement(connection, DROP_DB_SYNTAX + " " + database_name);
    }

    public static boolean tableExists(Connection connection, String database_name, String table_name) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + database_name + "' AND TABLE_NAME = '" + table_name + "'";
            System.out.println("query: " + sql);
            ResultSet check_result = statement.executeQuery(sql);

            return check_result.first() && check_result.getString(1).equalsIgnoreCase(table_name);
        }
    }
}
