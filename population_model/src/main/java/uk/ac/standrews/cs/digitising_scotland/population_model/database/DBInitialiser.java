package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sets up a database ready to contain population data, overwriting any existing data.
 *
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class DBInitialiser {

    private static final String CREATE_DB_SYNTAX = "CREATE DATABASE IF NOT EXISTS ";
    private static final String DROP_TABLE_SYNTAX = "DROP TABLE IF EXISTS ";
    private static final String CREATE_TABLE_SYNTAX = "CREATE TABLE ";
    private static final String SQL_TYPE_INTEGER = "int(11)";
    private static final String SQL_TYPE_DATE = "DATE";
    private static final String SQL_TYPE_STRING_1 = "varchar(1)";
    private static final String SQL_TYPE_STRING_50 = "varchar(50)";
    private static final String SQL_TYPE_STRING_100 = "varchar(100)";

    /**
     * Sets up a database ready to contain population data. A new database is created
     * if it does not already exist. Any existing population tables are dropped, and new empty tables are created.
     *
     * @throws IOException
     */
    public void setupDB() throws SQLException, IOException {

        createDB();
        dropExistingTables();
        createTables();
    }

    private void createDB() throws SQLException {

        try (Connection connection = new DBConnector().createConnection()) {

            executeStatement(connection, CREATE_DB_SYNTAX + " " + PopulationProperties.DATABASE_NAME);
        }
    }

    private void dropExistingTables() throws SQLException {

        try (Connection connection = new DBConnector(PopulationProperties.DATABASE_NAME).createConnection()) {

            dropTable(connection, PopulationProperties.PERSON_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME);
            dropTable(connection, PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME);
        }
    }

    private void dropTable(final Connection connection, final String table_name) throws SQLException {

        executeStatement(connection, DROP_TABLE_SYNTAX + " " + table_name);
    }

    private void createTables() throws SQLException {

        try (Connection connection = new DBConnector(PopulationProperties.DATABASE_NAME).createConnection()) {

            executeStatement(connection, createPartnershipTableQuery());
            executeStatement(connection, createPeopleTableQuery());
            executeStatement(connection, createPartnershipPartnerTableQuery());
            executeStatement(connection, createPartnershipChildrenTableQuery());
        }
    }

    private String createPeopleTableQuery() throws SQLException {

        String[] attribute_names = new String[]{

                PopulationProperties.PERSON_FIELD_ID, PopulationProperties.PERSON_FIELD_GENDER, PopulationProperties.PERSON_FIELD_NAME,
                PopulationProperties.PERSON_FIELD_SURNAME, PopulationProperties.PERSON_FIELD_BIRTH_DATE,
                PopulationProperties.PERSON_FIELD_DEATH_DATE, PopulationProperties.PERSON_FIELD_OCCUPATION,
                PopulationProperties.PERSON_FIELD_CAUSE_OF_DEATH, PopulationProperties.PERSON_FIELD_ADDRESS
        };

        String[] attribute_types = new String[]{

                SQL_TYPE_INTEGER, SQL_TYPE_STRING_1, SQL_TYPE_STRING_50, SQL_TYPE_STRING_50, SQL_TYPE_DATE,
                SQL_TYPE_DATE, SQL_TYPE_STRING_100, SQL_TYPE_STRING_100, SQL_TYPE_STRING_50};

        boolean[] nulls_allowed = new boolean[]{false, true, true, true, true, true, true, true, true};

        return createTableQuery(PopulationProperties.PERSON_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, PopulationProperties.PERSON_FIELD_ID);
    }

    private String createPartnershipTableQuery() throws SQLException {

        String[] attribute_names = new String[]{PopulationProperties.PARTNERSHIP_FIELD_ID, PopulationProperties.PARTNERSHIP_FIELD_DATE};
        String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_DATE};
        boolean[] nulls_allowed = new boolean[]{false, false};

        return createTableQuery(PopulationProperties.PARTNERSHIP_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, PopulationProperties.PARTNERSHIP_FIELD_ID);
    }

    private String createPartnershipPartnerTableQuery() throws SQLException {

        String[] attribute_names = new String[]{PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID, PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID};
        String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_INTEGER};
        boolean[] nulls_allowed = new boolean[]{false, false};

        return createTableQuery(PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, null);
    }

    private String createPartnershipChildrenTableQuery() throws SQLException {

        String[] attribute_names = new String[]{PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID, PopulationProperties.PARTNERSHIP_FIELD_PARTNERSHIP_ID};
        String[] attribute_types = new String[]{SQL_TYPE_INTEGER, SQL_TYPE_INTEGER};
        boolean[] nulls_allowed = new boolean[]{false, false};

        return createTableQuery(PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME, attribute_names, attribute_types, nulls_allowed, null);
    }

    private static void executeStatement(final Connection connection, final String query) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    private static String createTableQuery(final String table_name, final String[] attribute_names, final String[] attribute_types, final boolean[] nulls_allowed, final String primary_key) throws SQLException {

        if (attribute_names.length != attribute_types.length || attribute_names.length != nulls_allowed.length) {
            throw new SQLException("inconsistent attribute details");
        }

        final StringBuilder builder = new StringBuilder();

        builder.append(CREATE_TABLE_SYNTAX);
        builder.append(table_name);
        builder.append("(");

        for (int i = 0; i < attribute_names.length; i++) {
            builder.append("`");
            builder.append(attribute_names[i]);
            builder.append("` ");
            builder.append(attribute_types[i]);
            builder.append(nulls_allowed[i] ? " DEFAULT NULL" : " NOT NULL");
            if (i < attribute_names.length - 1) {
                builder.append(",");
            }
        }

        if (primary_key != null) {
            builder.append(", PRIMARY KEY  (`" + primary_key + "`)");
        }

        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        return builder.toString();
    }
}
