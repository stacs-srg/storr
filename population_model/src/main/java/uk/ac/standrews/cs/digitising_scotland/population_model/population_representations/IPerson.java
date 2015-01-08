/*
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
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations;

import java.util.Date;
import java.util.List;

/**
 * Interface for person objects.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IPerson {

    /**
     * Representation of female sex.
     */
    char FEMALE = 'F';

    /**
     * Representation of male sex.
     */
    char MALE = 'M';

    /**
     * Gets the person's unique identifier. It can be assumed that identifiers are allocated in temporal
     * order, so an older person's identifier is always less than that of a younger person.
     * @return the person's unique identifier
     */
    int getId();

    /**
     * Gets the person's first name.
     * @return the person's first name
     */
    String getFirstName();

    /**
     * Gets the person's surname (family name).
     * @return the person's surname
     */
    String getSurname();

    /**
     * Gets the person's sex, either {@link #FEMALE} or {@link #MALE}.
     * @return the person's sex
     */
    char getSex();

    /**
     * Gets the person's date of birth.
     * @return the person's date of birth
     */
    Date getBirthDate();

    /**
     * Gets the person's place of birth, or null if not recorded.
     * @return the person's place of birth
     */
    String getBirthPlace();

    
    /*
     * TODO Do we have multiple possible death records for an individual?
     * If so the the date place and cause will all arrise from the same record.
     * These need to be constrainted somehow.
     */
    
    /**
     * Gets the person's date of death, or null if they are living.
     * @return the person's date of death
     */
    Date getDeathDate();

    /**
     * Gets the person's place of death, or null if not recorded.
     * @return the person's place of death
     */
    String getDeathPlace();
    
    /**
     * Gets the cause of the person's death, or null if not recorded.
     * @return the cause of the person's death
     */
    String getDeathCause();

    /*
     * End of above comment's condisderation
     */
    
    /**
     * Gets the person's occupation, or null if not recorded.
     * @return the person's occupation
     */
    String getOccupation();

    
    /**
     * Gets the Links of the person's potential partnerships, or null if none are recorded.
     * @return the Linkes to the person's potential partnerships
     */
    List<Link> getPartnerships();

    /**
     * Gets the Link of the person's parents' partnership, or null if none are recorded.
     * @return the identifier of the person's parents' partnership
     */
    Link getParentsPartnership();
}
