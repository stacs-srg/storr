package uk.ac.standrews.cs.digitising_scotland.population_model.database;

import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPartnershipFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Provides iteration over partnerships in the database.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class PartnershipIterator implements Iterator<DBBackedPartnership>, Iterable<DBBackedPartnership>, AutoCloseable {

    private final ResultSet result_set;
    private Connection connection;
    private Statement statement;
    private boolean empty;

    public PartnershipIterator() throws SQLException {

        connection = new DBConnector(PopulationProperties.DATABASE_NAME).createConnection();
        statement = connection.createStatement();

        result_set = statement.executeQuery("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME);

        empty = !result_set.first();
    }

    @Override
    public Iterator<DBBackedPartnership> iterator() {
        return this;
    }

    @Override
    public void close() throws SQLException {

        connection.close();
        result_set.close();
    }

    @Override
    public boolean hasNext() {

        try {
            return !empty && !result_set.isAfterLast();

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error iterating over result set");
            return false;
        }
    }

    @Override
    public DBBackedPartnership next() {

        try {
            final DBBackedPartnership result = DBBackedPartnershipFactory.createDBBackedPartnershipFromPartnershipRS(connection, result_set);
            result_set.next();
            return result;

        } catch (final SQLException e) {
            ErrorHandling.exceptionError(e, "error creating partnership");
            return null;
        }
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove");
    }

    public int size() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet size_result = statement.executeQuery("SELECT COUNT(*) FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME);

            size_result.first();
            return size_result.getInt(1);
        }
    }
}
