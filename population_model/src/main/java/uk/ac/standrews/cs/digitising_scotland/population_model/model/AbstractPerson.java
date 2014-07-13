package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.Date;
import java.util.List;

/**
 * Created by graham on 04/07/2014.
 */
public abstract class AbstractPerson implements IPerson {

    protected int id;
    protected String first_name;
    protected String surname;
    protected char sex;
    protected Date date_of_birth;
    protected Date date_of_death;
    protected String occupation;
    protected String cause_of_death;
    protected String address;
    protected String string_rep;
    protected List<Integer> partnerships;
    protected int parents_partnership_id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return first_name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public Date getBirthDate() {
        return date_of_birth;
    }

    @Override
    public Date getDeathDate() {
        return date_of_death;
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

    @Override
    public String getCauseOfDeath() {
        return cause_of_death;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public List<Integer> getPartnerships() {
        return partnerships;
    }

    @Override
    public int getParentsPartnership() {
        return parents_partnership_id;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof IPerson && ((IPerson) other).getId() == id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String toString() {
        return string_rep;
    }
}
