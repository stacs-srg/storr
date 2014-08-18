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
import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Sets up a database ready to contain population data, overwriting any existing data.
 *
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class DBInitialiser {

    private static final String DROP_TABLE_SYNTAX = "DROP TABLE IF EXISTS ";
    private static final String CREATE_TABLE_SYNTAX = "CREATE TABLE ";
    private static final String SQL_TYPE_INTEGER = "int(11)";
    private static final String SQL_TYPE_DATE = "DATE";
    private static final String SQL_TYPE_STRING_1 = "varchar(1)";
    private static final String SQL_TYPE_STRING_50 = "varchar(50)";
    private static final String SQL_TYPE_STRING_100 = "varchar(100)";

    private static final String ACCESS_DENIED_PREFIX = "Access denied";
    private static final String INNODB_ENGINE = "InnoDB";
    private static final String UTF8 = "utf8";

    /**
     * Sets up a database ready to contain population data. A new database is created
     * if it does not already exist. Any existing population tables are dropped, and new empty tables are created.
     *
     * @throws SQLException if the database initialisation fails
     */
    public static void setupDB() throws SQLException {

        try {
            createDB();
            dropExistingTables();
            createTables();
        } catch (final SQLException e) {
            throw addExceptionExplanation(e);
        }
    }

    private static SQLException addExceptionExplanation(final SQLException e) {

        if (e.getMessage().startsWith(ACCESS_DENIED_PREFIX)) {
            return new SQLException(getAccessDeniedHint());
        }
        return e;
    }

    private static String getAccessDeniedHint() {

        return "Database access denied: make sure that local database user '" + PopulationProperties.DEFAULT_DB_USERNAME + "' exists, and has full access privileges from 'localhost'.";
    }

    private static void createDB() throws SQLException {

        try (Connection connection = new DBConnector().createConnection()) {

            DBManipulation.createDatabaseIfDoesNotExist(connection, PopulationProperties.getDatabaseName());
        }
    }

    private static void dropExistingTables() throws SQLException {

        try (Connection connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection()) {

            dropTable(connection, PopulationProperties.PERSON_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME);
        }
    }

    private static void dropTable(final Connection connection, final String table_name) throws SQLException {

        DBManipulation.executeStatement(connection, DROP_TABLE_SYNTAX + ' ' + table_name);
    }

    @SuppressWarnings("FeatureEnvy")
    private static void createTables() throws SQLException {

        try (Connection connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection()) {

            DBManipulation.executeStatement(connection, createPartnershipTableQuery());
            DBManipulation.executeStatement(connection, createPeopleTableQuery());
            DBManipulation.executeStatement(connection, createPartnershipPartnerTableQuery());
            DBManipulation.executeStatement(connection, createPartnershipChildrenTableQuery());
        }
    }

    private static String createPeopleTableQuery() throws SQLException {

        final String[] attribute_names = new String[]{

                PopulationProperties.PERSON_FIELD_ID,
                PopulationProperties.PERSON_FIELD_GENDER,
                PopulationProperties.PERSON_FIELD_NAME,
                PopulationProperties.PERSON_FIELD_SURNAME,
                PopulationProperties.PERSON_FIELD_BIRTH_DATE,
                PopulationProperties.PERSON_FIELD_BIRTH_PLACE,
                PopulationProperties.PERSON_FIELD_DEATH_DATE,
                PopulationProperties.PERSON_FIELD_DEATH_PLACE,
                PopulationProperties.PERSON_FIELD_DEATH_CAUSE,
                PopulationProperties.PERSON_FIELD_OCCUPATION,
        };

        final String[] attribute_types = new String[]{

                SQL_TYPE_INTEGER,
                SQL_TYPE_STRING_1,
                SQL_TYPE_STRING_50,
                SQL_TYPE_STRING_50,
                SQL_TYPE_DATE,
                SQL_TYPE_STRING_100,
                SQL_TYPE_DATE,
                SQL_TYPE_STRING_100,
                SQL_TYPE_STRING_100,
                SQL_TYPE_STRING_50};

        final boolean[] nulls_allowed = new boolean[]{false, true, true, true, true, true, true, true, true, true};

        return createTableQuery(PopulationProperties.PERSON_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, PopulationProperties.PERSON_FIELD_ID);
    }

    private static String createPartnershipTableQuery() throws SQLException {

        final String[] attribute_names = new String[]{
                PopulationProperties.PARTNERSHIP_FIELD_ID,
                PopulationProperties.PARTNERSHIP_FIELD_DATE,
                PopulationProperties.PARTNERSHIP_FIELD_PLACE};

        final String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_DATE, SQL_TYPE_STRING_100};
        final boolean[] nulls_allowed = new boolean[]{false, true, true};

        return createTableQuery(PopulationProperties.PARTNERSHIP_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, PopulationProperties.PARTNERSHIP_FIELD_ID);
    }

    private static String createPartnershipPartnerTableQuery() throws SQLException {

        final String[] attribute_names = new String[]{PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID, PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID};
        final String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_INTEGER};
        final boolean[] nulls_allowed = new boolean[]{false, false};

        return createTableQuery(PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, null);
    }

    private static String createPartnershipChildrenTableQuery() throws SQLException {

        final String[] attribute_names = new String[]{PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID, PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID};
        final String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_INTEGER};
        final boolean[] nulls_allowed = new boolean[]{false, false};

        return createTableQuery(PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, null);
    }

    private static String createTableQuery(final String table_name, final String[] attribute_names, final String[] attribute_types, final boolean[] nulls_allowed, final String primary_key) throws SQLException {

        if (attribute_names.length != attribute_types.length || attribute_names.length != nulls_allowed.length) {
            throw new SQLException("inconsistent attribute details");
        }

        final StringBuilder builder = new StringBuilder();

        builder.append(CREATE_TABLE_SYNTAX);
        builder.append(table_name);
        builder.append('(');

        for (int i = 0; i < attribute_names.length; i++) {
            builder.append('`');
            builder.append(attribute_names[i]);
            builder.append("` ");
            builder.append(attribute_types[i]);
            builder.append(nulls_allowed[i] ? " DEFAULT NULL" : " NOT NULL");
            if (i < attribute_names.length - 1) {
                builder.append(',');
            }
        }

        if (primary_key != null) {
            builder.append(", PRIMARY KEY  (`").append(primary_key).append("`)");
        }

        builder.append(") ENGINE=" + INNODB_ENGINE + " DEFAULT CHARSET=" + UTF8 + ';');
        return builder.toString();
    }
}
