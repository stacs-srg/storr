package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.Date;

/**
 * Created by graham on 09/06/2014.
 */
public interface IPerson {

    char FEMALE = 'F';
    char MALE = 'M';

    String MALE_STRING = String.valueOf(MALE);

    int getID();

    String getFirstName();

    String getSurname();

    char getGender();

    Date getBirthDate();

    java.util.Date getDeathDate();

    String getOccupation();

    String getCauseOfDeath();

    String getAddress();

    String getMaidenName();

    //What are these supposed to do?

    int getPartnership();

    int getParentsPartnership();
}
