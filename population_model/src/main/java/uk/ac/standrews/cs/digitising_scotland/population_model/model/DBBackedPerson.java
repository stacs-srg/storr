package uk.ac.standrews.cs.digitising_scotland.population_model.model;

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
            return DBBackedPartnershipFactory.createDBBackedPartnershipForChild(connection, getID());
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
            return DBBackedPartnershipFactory.createDBBackedPartnershipFromSpouse(connection, getID());
        }
        catch (final SQLException e) {
            ErrorHandling.error("Cannot get family");
            return null;
        }
    }
}
