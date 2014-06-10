package uk.ac.standrews.cs.digitising_scotland.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility methods for SQL manipulation.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SQLManipulation {

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
            } else {
                throw new SQLException("unknown parameter type");
            }

            pos++;
        }
    }
}
