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

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;
import java.util.List;

/**
 * Created by victor on 08/07/14.
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPerson implements IPerson {

    private int id;
    private String firstName;
    private String lastName;
    private char sex;
    private int age_in_days;
    private List<Integer> partnerships;

    //    private int daysToLive = DateManipulation.dateToDays(dateOfDeath) -  DateManipulation.dateToDays(dateOfBirth);
    private OrganicTimeline timeline = null;

    public int getDayOfLife(Date date) {
        int day = DateManipulation.dateToDays(date) - DateManipulation.dateToDays(getBirthDate());
        return day;
    }

    public OrganicPerson() {
        setTimeline(null);
    }

    public OrganicPerson(char sex) {
        this.sex = sex;
    }

    public OrganicPerson(int id, char sex) {
        this.sex = sex;
        this.id = id;
    }

    public void setTimeline(OrganicTimeline t) {
        this.timeline = t;
    }

    public OrganicTimeline getTimeline() {
        return timeline;
    }
    
//    public OrganicEvent getCurrentEvent() {
//		timeline = getTimeline();
//		
//    	return null;	
//    }

    /**
     * INTERFACE METHODS
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
        return this.getTimeline().getStartDate();
    }

    @Override
    public Date getDeathDate() {
        return this.getTimeline().getEndDate();
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

    @Override
    public int getParentsPartnership() {
        throw new RuntimeException("unimplemented");
    }
}
