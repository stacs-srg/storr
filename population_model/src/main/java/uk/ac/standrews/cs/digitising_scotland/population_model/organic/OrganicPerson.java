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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FemaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.MaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by victor on 08/07/14.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPerson implements IPerson {

    private int id;
    private String firstName;
    private String lastName;
    private char sex;
    private int age_in_days;
    private ArrayList<Integer> partnerships = new ArrayList<Integer>();
	private OrganicTimeline timeline = null;
    
    private static Random random = RandomFactory.getRandom();
	private static Distribution<Integer> seed_age_distribution = new UniformDistribution(0, 70, random);
	private static Distribution<Integer> maleAgeAtMarriageDistribution = new MaleAgeAtMarriageDistribution(random);
	private static Distribution<Integer> femaleAgeAtMarriageDistribution = new FemaleAgeAtMarriageDistribution(random);
	private static UniformSexDistribution sex_distribution = new UniformSexDistribution(random);

    public OrganicPerson(final int id) {
    	this.id = id;
    	if (sex_distribution.getSample()) {
    		sex = 'M';
    		setPersonsBirthAndDeathDates();
		} else {
			sex = 'F';
			setPersonsBirthAndDeathDates();
		}
    }
	
	public int getDayOfLife(final Date date) {
        int day = DateManipulation.dateToDays(date) - DateManipulation.dateToDays(getBirthDate());
        return day;
    }

    public void setTimeline(final OrganicTimeline t) {
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
    
	private void setPersonsBirthAndDeathDates() {
		final UniformDistribution days_of_year_distribution = new UniformDistribution(1, (int) OrganicPopulation.DAYS_PER_YEAR, random);

		// Find an age for person
		int age = seed_age_distribution.getSample();
		int auxiliary = (int) ((age - 1) * (OrganicPopulation.DAYS_PER_YEAR)) + days_of_year_distribution.getSample();
		int currentDayOfBirth = DateManipulation.dateToDays(OrganicPopulation.START_YEAR, 1, 1) - auxiliary;

		// Set birth date

		if(currentDayOfBirth < OrganicPopulation.getEarliestDate())
			OrganicPopulation.setEarliestDate(currentDayOfBirth);


		// Calculate and set death date
        // TODO allow death before start age
		Distribution<Integer> seed_death_distribution = new UniformDistribution(age, 100, random);
		int currentDayOfDeath = DateManipulation.dateToDays(OrganicPopulation.START_YEAR, 1, 1) + (seed_death_distribution.getSample() - age) * (int) OrganicPopulation.DAYS_PER_YEAR;
		// Create timeline
		timeline = new OrganicTimeline(currentDayOfBirth, currentDayOfDeath);
		timeline.addEvent(currentDayOfDeath, new OrganicEvent(EventType.DEATH));
	}

	public void populate_timeline() {

		// Add events to timeline
		addEligibleToMarryEvent();

	}

	private void addEligibleToMarryEvent() {
		// Add ELIGIBLE_TO_MARRY event
		int date;
		if (sex == 'M') {
			// time in days to birth from 1/1/1600 + marriage age in days
			date = getBirthDay() + maleAgeAtMarriageDistribution.getSample();
			timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
		} else {
			// time in days to birth from 1/1/1600 + marriage age in days
			date = getBirthDay() + femaleAgeAtMarriageDistribution.getSample();
			timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
		}
	}

    public void addPartnership(final int id) {
        partnerships.add(id);
    }
    
    public int getBirthDay() {
    	return timeline.getStartDate();
    }
    
    public int getDeathDay() {
    	return timeline.getEndDate();
    }

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
        return DateManipulation.daysToDate(this.getTimeline().getStartDate());
    }

    @Override
    public Date getDeathDate() {
        return DateManipulation.daysToDate(this.getTimeline().getEndDate());
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
