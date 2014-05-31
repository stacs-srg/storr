package uk.ac.standrews.cs.digitising_scotland.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by graham on 31/05/2014.
 */
public class SQLManipulation {

    public static void configurePreparedStatement(PreparedStatement statement, Object... parameters) throws SQLException {

        int pos = 1;

        for (Object parameter : parameters) {

            if (parameter instanceof Integer) {
                statement.setInt(pos++, (Integer) parameter);
            }
            if (parameter instanceof String) {
                statement.setString(pos++, (String) parameter);
            }
            if (parameter instanceof Date) {
                statement.setDate(pos++, (Date) parameter);
            }
        }
    }
}
