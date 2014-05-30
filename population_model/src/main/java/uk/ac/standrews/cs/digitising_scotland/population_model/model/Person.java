package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.sql.Date;

/*
 * An intermediate representation of a person.
 *
 * Has date of birth and date of death encoded fully but does not include family members etc.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class Person {

    public static final char FEMALE = 'F';
    public static final char MALE = 'M';

    public static final String MALE_STRING = String.valueOf(MALE);

    private int id;
    private char gender;
    private String first_name;
    protected String surname;
    private Date birth_date;
    private Date death_date;
    private String occupation;
    private String cause_of_death;
    private String address;
    private String maiden_name;

    public Person() {

    }

    public Person(final int id, final char gender, final Date birth_date, final Date death_date, final String occupation, final String cause_of_death, final String address) {

        this(id, null, null, gender, birth_date, death_date, occupation, cause_of_death, address);
    }

    public Person(final int id, final String first_name, final String surname, final char gender, final Date birth_date, final Date death_date, final String occupation, final String cause_of_death, final String address) {

        this.gender = gender;
        this.first_name = first_name;
        this.surname = surname;
        this.id = id;
        this.birth_date = (Date) birth_date.clone();
        this.death_date = (Date) death_date.clone();
        this.occupation = occupation;
        this.cause_of_death = cause_of_death;
        this.address = address;
    }

    public char getGender() {

        return gender;
    }

    public void setGender(final char gender) {

        if (!(gender == FEMALE || gender == MALE)) throw new RuntimeException("illegal gender char");
        this.gender = gender;
    }

    public String getFirstName() {

        return first_name;
    }

    public String getSurname() {

        return surname;
    }

    public void setSurname(final String surname) {

        this.surname = surname;
    }

    public void setFirstName(final String first_name) {

        this.first_name = first_name;
    }

    public Date getBirthDate() {

        return (Date) birth_date.clone();
    }

    public void setBirthDate(final Date birth_date) {

        this.birth_date = (Date) birth_date.clone();
    }

    public Date getDeathDate() {

        return (Date) death_date.clone();
    }

    public void setDeathDate(final Date death_date) {

        this.death_date = (Date) death_date.clone();
    }

    public String getOccupation() {

        return occupation;
    }

    public void setOccupation(final String occupation) {

        this.occupation = occupation;
    }

    public String getCauseOfDeath() {

        return cause_of_death;
    }

    public void setCauseOfDeath(final String cause_of_death) {

        this.cause_of_death = cause_of_death;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(final String address) {

        this.address = address;
    }

    public void setID(final int id) {

        this.id = id;
    }

    public int getID() {

        return id;
    }

    /**
     * Sets the Surname to be the new surname and the maiden name to be their old surname.
     */
    public void setMarriedName(final String new_surname) {

        maiden_name = surname;
        surname = new_surname;
    }

    /**
     * @return the maiden name of a person.
     */
    public String getMaidenName() {

        return maiden_name;
    }

    public DBBackedPartnership getParentsFamily() {
        throw new RuntimeException("unimplemented");
    }

    public DBBackedPartnership getFamily() {
        throw new RuntimeException("unimplemented");
    }
}
