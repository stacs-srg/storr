package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.sql.Date;

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

    Date getDeathDate();

    String getOccupation();

    String getCauseOfDeath();

    String getAddress();

    String getMaidenName();

    int getPartnership();

    int getParentsPartnership();
}
