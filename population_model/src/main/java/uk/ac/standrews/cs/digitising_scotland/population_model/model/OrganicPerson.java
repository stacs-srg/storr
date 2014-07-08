package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;
import java.util.List;

/**
 * Created by victor on 08/07/14.
 */
public class OrganicPerson implements IPerson{

    private int id;
    private String firstName;
    private String lastName;
    private char sex;
    private Date dateOfBirth;
    private Date dateOfDeath;
    private List<Integer> partnerships;

    private int daysToLive = DateManipulation.dateToDays(dateOfDeath) -  DateManipulation.dateToDays(dateOfBirth);
    private OrganicTimeline timeline = null;

    public int getDayOfLife(Date date){
        int day = DateManipulation.dateToDays(date) -  DateManipulation.dateToDays(dateOfBirth);;
        return day;
    }

    /**
     *  INTERFACE METHODS
     */

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getSurname() {
        return lastName;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public Date getBirthDate() {
        return dateOfBirth;
    }

    @Override
    public Date getDeathDate() {
        return dateOfDeath;
    }

    @Override
    public String getOccupation() {
        return null;
    }

    @Override
    public String getCauseOfDeath() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public List<Integer> getPartnerships() {
        return partnerships;
    }
}
