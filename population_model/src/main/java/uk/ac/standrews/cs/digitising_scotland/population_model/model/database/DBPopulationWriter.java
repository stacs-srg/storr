/**
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
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulationWriter;
import uk.ac.standrews.cs.digitising_scotland.util.DBManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Writes a representation of a population to a database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class DBPopulationWriter implements IPopulationWriter {

    private static final String RECORD_PERSON_STATEMENT = makeInsertStatement(PopulationProperties.PERSON_TABLE_NAME, 9);
    private static final String RECORD_PARTNERSHIP_STATEMENT = makeInsertStatement(PopulationProperties.PARTNERSHIP_TABLE_NAME, 2);
    private static final String RECORD_PARTNER_WITHIN_PARTNERSHIP_STATEMENT = makeInsertStatement(PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME, 2);
    private static final String RECORD_CHILD_WITHIN_PARTNERSHIP_STATEMENT = makeInsertStatement(PopulationProperties.PARTNERSHIP_CHILD_TABLE_NAME, 2);

    private final Connection connection;
    private final PreparedStatement record_person_statement;
    private final PreparedStatement record_partnership_statement;
    private final PreparedStatement record_partner_within_partnership_statement;
    private final PreparedStatement record_child_within_partnership_statement;

    public DBPopulationWriter() throws IOException, SQLException {

        connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();

        record_person_statement = connection.prepareStatement(RECORD_PERSON_STATEMENT);
        record_partnership_statement = connection.prepareStatement(RECORD_PARTNERSHIP_STATEMENT);
        record_partner_within_partnership_statement = connection.prepareStatement(RECORD_PARTNER_WITHIN_PARTNERSHIP_STATEMENT);
        record_child_within_partnership_statement = connection.prepareStatement(RECORD_CHILD_WITHIN_PARTNERSHIP_STATEMENT);
    }

    public void close() throws SQLException {

        record_person_statement.close();
        record_partnership_statement.close();

        connection.close();
    }

    @SuppressWarnings("FeatureEnvy")
    public void recordPerson(final IPerson person) throws SQLException {

        DBManipulation.configurePreparedStatement(
                record_person_statement,
                person.getId(),
                String.valueOf(person.getSex()),
                person.getFirstName(),
                person.getSurname(),
                DateManipulation.dateToSQLDate(person.getBirthDate()),
                person.getDeathDate() != null ? DateManipulation.dateToSQLDate(person.getDeathDate()) : DBManipulation.NULL_DATE,
                person.getOccupation(),
                person.getCauseOfDeath(),
                person.getAddress());

        record_person_statement.executeUpdate();
    }

    @SuppressWarnings("FeatureEnvy")
    public void recordPartnership(final IPartnership partnership) throws SQLException {

        final int partnership_id = partnership.getId();
        final Date marriage_date = DateManipulation.dateToSQLDate(partnership.getMarriageDate());

        recordPartnership(partnership_id, marriage_date);
        recordPartnerWithinPartnership(partnership_id, partnership.getFemalePartnerId());
        recordPartnerWithinPartnership(partnership_id, partnership.getMalePartnerId());

        for (final Integer child_id : partnership.getChildIds()) {
            recordChildWithinPartnership(partnership_id, child_id);
        }
    }

    private void recordPartnership(final int partnership_id, final Date marriage_date) throws SQLException {

        DBManipulation.configurePreparedStatement(record_partnership_statement, partnership_id, marriage_date);
        record_partnership_statement.executeUpdate();
    }

    private void recordPartnerWithinPartnership(final int partnership_id, final int partner_id) throws SQLException {

        DBManipulation.configurePreparedStatement(record_partner_within_partnership_statement, partner_id, partnership_id);
        record_partner_within_partnership_statement.executeUpdate();
    }

    private void recordChildWithinPartnership(final int partnership_id, final Integer child_id) throws SQLException {

        DBManipulation.configurePreparedStatement(record_child_within_partnership_statement, child_id, partnership_id);
        record_child_within_partnership_statement.executeUpdate();
    }

    private static String makeInsertStatement(final String table_name, final int number_of_values) {

        final StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(table_name);
        builder.append(" VALUES(");
        for (int i = 0; i < number_of_values; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('?');
        }
        builder.append(");");
        return builder.toString();
    }
}
