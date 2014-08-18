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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Defines parameters for JDBC connection to a database.
 *
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
class DBConnector {

    private static final String CONNECTION_STRING_PREFIX = "jdbc:mysql://";

    private final String username;
    private final String password;
    private String connection_string;

    /**
     * Initialises the connector to access the server running on "localhost" with default port, username "root" and an empty password.
     */
    public DBConnector() {

        this("");
    }

    /**
     * Initialises the connector with a specific database and default values for the other details.
     *
     * @param database_name the name of the database to be accessed, or the empty string to connect to the server without a specific database
     */
    public DBConnector(final String database_name) {

        this(PopulationProperties.DEFAULT_DB_ADDRESS, 0, PopulationProperties.DEFAULT_DB_USERNAME, PopulationProperties.DEFAULT_DB_PASSWORD, database_name);
    }

    /**
     * Initialises the connector with the specified server/database details.
     *
     * @param address       the network address of the server, or "localhost"
     * @param port          the port on which the server is listening, or 0 for the default port
     * @param username      the username for accessing the server
     * @param password      the password for accessing the server
     * @param database_name the name of the database to be accessed, or the empty string to connect to the server without a specific database
     */
    public DBConnector(final String address, final int port, final String username, final String password, final String database_name) {

        this.username = username;
        this.password = password;

        // Construct connection string of form:
        // "jdbc:mysql://localhost"                  - connect to DB generally rather than a specific database, assuming default port of 3306
        // "jdbc:mysql://localhost:3971"             - connect to DB generally rather than a specific database, using a specific port
        // "jdbc:mysql://localhost:5182/population"  - connect to a specific database using a specific port

        connection_string = CONNECTION_STRING_PREFIX + address;

        if (port > 0) {
            connection_string += ":" + port;
        }
        if (!database_name.isEmpty()) {
            connection_string += '/' + database_name;
        }
    }

    /**
     * Creates a connection to the MariaDB server or database specified via the constructor.
     *
     * @return the connection
     * @throws SQLException if a database access error occurs
     */
    public Connection createConnection() throws SQLException {

        return DriverManager.getConnection(connection_string, username, password);
    }
}
