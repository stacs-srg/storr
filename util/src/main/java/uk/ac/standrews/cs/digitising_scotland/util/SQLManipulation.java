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
            } else if (parameter == null) {
                statement.setString(pos, "");
            } else {
                throw new SQLException("unknown parameter type: " + parameter.getClass().getSimpleName());
            }

            pos++;
        }
    }
}
