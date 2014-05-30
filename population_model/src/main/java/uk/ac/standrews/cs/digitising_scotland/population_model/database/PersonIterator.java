package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPersonFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Provides iteration over people in the database.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PersonIterator implements Iterator<Person>, Iterable<Person>, AutoCloseable {

    private final ResultSet resultSet;
    private Connection connection;
    private Statement statement;
    private boolean empty;

    public PersonIterator() throws SQLException {

        connection = new DBConnector(PopulationProperties.DATABASE_NAME).createConnection();
        statement = connection.createStatement();

        resultSet = statement.executeQuery("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PERSON_TABLE_NAME);

        empty = !resultSet.first();
    }

    @Override
    public Iterator<Person> iterator() {
        return this;
    }

    @Override
    public void close() throws SQLException {

        connection.close();
        resultSet.close();
    }

    @Override
    public boolean hasNext() {

        try {
            return !empty && !resultSet.isAfterLast();

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error iterating over result set");
            return false;
        }
    }

    @Override
    public Person next() {

        try {
            final Person result = DBBackedPersonFactory.createDBBackedPerson(connection, resultSet);
            resultSet.next();
            return result;

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error creating person");
            return null;
        }
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet size_result = statement.executeQuery("SELECT COUNT(*) FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PERSON_TABLE_NAME);

            if (!size_result.first()) throw new SQLException("No rows returned in person count");
            return size_result.getInt(1);
        }
    }
}
