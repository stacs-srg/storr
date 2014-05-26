package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
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

    private static final String CREATE_DB_SYNTAX = "CREATE DATABASE IF NOT EXISTS";
    private static final String DROP_TABLE_SYNTAX = "DROP TABLE IF EXISTS";

    /**
     * Sets up a database ready to contain population data. A new database is created
     * if it does not already exist. Any existing population tables are dropped, and new empty tables are created.
     * @throws IOException 
     */
    public void setupDB() throws SQLException, IOException {

        IDFactory.resetId();
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

            executeStatement(connection, createPartnershipQuery());
            executeStatement(connection, createPeopleQuery());
            executeStatement(connection, createPartnershipPartnerQuery());
            executeStatement(connection, createPartnershipChildrenQuery());
        }
    }

    private String createPeopleQuery() {

        return createTableQuery(PopulationProperties.PERSON_TABLE_NAME, // the fields:
                "  `id` int(11) NOT NULL auto_increment,", // private int id = next_id++;
                //                        "  `family` int,", // the family to which this person is a child
                "  `gender` varchar(1) default NULL,", //    private char gender;
                "  `name` varchar(50) default NULL,", //    private String name;
                "  `surname` varchar(50) default NULL,", //    private String surname;
                "  `birthdate` DATE default NULL,", //    private Date birthdate;
                "  `deathdate` DATE default NULL,", //    private Date deathdate;
                "  `occupation` varchar(100) default NULL,", //    private String occupation;
                "  `causeOfDeath` varchar(100) default NULL,", //    private String causeOfDeath;
                "  `address` varchar(50) default NULL,", //    private String address;
                //                        "  FOREIGN KEY (`family`) REFERENCES " + PARTNERSHIP_TABLE_NAME + " (`id`),", // people know what family they belong to;
                "  PRIMARY KEY  (`id`)");
    }

    private String createPartnershipQuery() {

        return createTableQuery(PopulationProperties.PARTNERSHIP_TABLE_NAME, // the fields:
                "  `id` int(11) NOT NULL,", // KEY
                "  `date` DATE default NULL,", //    Date of partnership;
                "  PRIMARY KEY  (`id`)");
    }

    private String createPartnershipPartnerQuery() {

        return createTableQuery(PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME, "`person_id` int(11) NOT NULL ,", // FOREIGN KEY - PERSON
                "`partnership_id` int(11) NOT NULL"); //  FOREIGN KEY - PARTNERSHIP
    }

    private String createPartnershipChildrenQuery() {

        return createTableQuery(PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME, "`person_id` int(11) NOT NULL ,", // FOREIGN KEY - PERSON
                "`partnership_id` int(11) NOT NULL"); //  FOREIGN KEY - PARTNERSHIP);
    }

    private static void executeStatement(final Connection connection, final String query) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    private static String createTableQuery(final String table_name, final String... attributes) {

        final StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ");
        builder.append(table_name);
        builder.append("(");

        for (final String attribute : attributes) {
            builder.append(attribute);
        }

        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        return builder.toString();
    }
}
