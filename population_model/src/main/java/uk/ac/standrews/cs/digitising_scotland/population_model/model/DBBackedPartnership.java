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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DBBackedPartnership {

    private final Set<Person> partners = new HashSet<>();
    private Date start_date = null;
    private DBBackedPerson groom = null;
    private DBBackedPerson bride = null;
    private int id;

    public DBBackedPartnership(final Connection connection, final int id) throws SQLException {

        this.id = id;

        // TODO might be able to factor this out.
        try (final PreparedStatement get_partners_statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME + " WHERE partnership_id= ?")) {

            get_partners_statement.setInt(1, id);

            final ResultSet partner_result_set = get_partners_statement.executeQuery();

            while (partner_result_set.next()) {

                final int partner_id = partner_result_set.getInt(PopulationProperties.PARTNERSHIP_FIELD_PERSON_ID);
                final DBBackedPerson partner = DBBackedPersonFactory.createDBBackedPerson(connection, partner_id);

                partners.add(partner);
                if (partner.getSex() == IPerson.MALE) {
                    groom = partner;
                } else {
                    bride = partner;
                }
            }
        }

        if (bride == null || groom == null) {
            throw new SQLException("bride or groom not found for partnership: " + id);
        }

        bride.setMarriedName(groom.getSurname());

        try (final PreparedStatement get_marriage_statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME + " WHERE id= ?")) {

            get_marriage_statement.setInt(1, id);

            final ResultSet marriage_result_set = get_marriage_statement.executeQuery();

            if (marriage_result_set.first()) {
                start_date = marriage_result_set.getDate(PopulationProperties.PARTNERSHIP_FIELD_DATE);
            }
        }
    }

    public int getId() {

        return id;
    }

    public Set<Person> getPartners() {

        return partners;
    }

    public Person getGroom() {

        return groom;
    }

    public Person getBride() {

        return bride;
    }

    public Date getStartDate() {

        return (Date) start_date.clone();
    }
}
