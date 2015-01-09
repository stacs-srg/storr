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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

public class LinkedPerson implements IPerson {

    private int id;
    private String firstName;
    private String surname = null;
    private String occupation;
    private String causeOfDeath;
    private char sex;
    private List<Link> partnerships = new ArrayList<Link>();
    private Link parentPartnershipLink;
    
    // Days since epoch (1600)
    private int dayOfBirth;
    private int dayOfDeath;
    
    public LinkedPerson(int id, String firstName, String surname, char sex) {
    	this.id = id;
    	this.firstName = firstName;
    	this.surname = surname;
    	this.sex = sex;
    }
    
    public void setPartnershipLinks(List<Link> partnerships) {
    	this.partnerships = partnerships;    	
    }
    
    public void setParentPartnershipLink(Link parentPartnershipLink) {
    	this.parentPartnershipLink = parentPartnershipLink;
    }
    
    /*
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
        return surname;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public Date getBirthDate() {
        return DateManipulation.daysToDate(dayOfBirth);
    }

    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public Date getDeathDate() {
        return DateManipulation.daysToDate(dayOfDeath);
    }

    @Override
    public String getDeathPlace() {
        return null;
    }

    @Override
    public String getDeathCause() {
        return causeOfDeath;
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

	@Override
	public List<Link> getPartnerships() {
		return partnerships;
	}

	@Override
	public Link getParentsPartnership() {
		return parentPartnershipLink;
	}



}
