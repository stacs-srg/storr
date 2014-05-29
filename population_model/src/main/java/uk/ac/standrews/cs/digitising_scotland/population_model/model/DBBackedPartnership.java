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
        final PreparedStatement get_partners_statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_PARTNER_TABLE_NAME + " WHERE partnership_id= ?");
        get_partners_statement.setInt(1, id);

        final ResultSet partner_result_set = get_partners_statement.executeQuery();

        while (partner_result_set.next()) {

            final int partner_id = partner_result_set.getInt("person_id");
            final DBBackedPerson partner = DBBackedPersonFactory.createDBBackedPerson(connection, partner_id);

            partners.add(partner);
            if (partner.getGender() == 'M') {
                groom = partner;
            } else {
                bride = partner;
            }
        }

        if (bride == null || groom == null) {
            throw new RuntimeException("bride or groom not found for partnership: " + id);
        }

        bride.setMarriedName(groom.getSurname());

        final PreparedStatement get_marriage_statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PARTNERSHIP_TABLE_NAME + " WHERE id= ?");
        get_marriage_statement.setInt(1, id);

        final ResultSet marriage_result_set = get_marriage_statement.executeQuery();

        if (marriage_result_set.first()) {
            start_date = marriage_result_set.getDate("date");
        }
    }

    public int getId() {

        return id;
    }

    public Set<Person> getChildren() {

        return null; //TODO write this
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

        return (Date)start_date.clone();
    }

    public Date getEndDate() {

        return null; // TODO write this.
    }

    public String getOtherInfo() {

        return ""; // TODO
    }
}
