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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.database.old;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.old.Person;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class DBBackedPerson extends Person {

    private final Connection connection;


    public DBBackedPerson(final Connection connection, final int id, final String firstname, final String surname, final char gender, final Date birthdate, final Date deathdate, final String occupation, final String causeOfDeath, final String address) {

        super(id, firstname, surname, gender, birthdate, deathdate, occupation, causeOfDeath, address);
        this.connection = connection;
    }

    /**
     * @return the family to which this person belongs
     * i.e. the partnership of which this Person is a child.
     */
    public DBBackedPartnership getParentsFamily() {

        try {
            return DBBackedPartnershipFactory.createDBBackedPartnershipForChild(connection, getId());
        }
        catch (final SQLException e) {
            ErrorHandling.error("Cannot get parents' family");
            return null;
        }
    }

    /**
     * @return the person's immediate family - i.e. the family of which this person is a husband or wife
     */
    public DBBackedPartnership getFamily() {

        try {
            return DBBackedPartnershipFactory.createDBBackedPartnershipFromSpouse(connection, getId());
        }
        catch (final SQLException e) {
            ErrorHandling.error("Cannot get family");
            return null;
        }
    }
}
