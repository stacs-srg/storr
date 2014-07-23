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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.*;
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
    private ArrayList<Integer> partnerships = new ArrayList<Integer>();
    private OrganicTimeline timeline = null;

    private static Random random = RandomFactory.getRandom();
    private static final int MAX_AGE = 100;
    private static Distribution<Integer> uniformDistribution;
    private static RemarriageDistribution remmariageDist = new RemarriageDistribution(random);
    private static Distribution<Integer> seed_age_distribution = new UniformDistribution(0, MAX_AGE, random);
    private static Distribution<Integer> seed_death_distribution = new UniformDistribution(0, MAX_AGE, random);
    private static Distribution<Integer> maleAgeAtMarriageDistribution = new MaleAgeAtMarriageDistribution(random);
    private static Distribution<Integer> femaleAgeAtMarriageDistribution = new FemaleAgeAtMarriageDistribution(random);
    private static UniformSexDistribution sex_distribution = new UniformSexDistribution(random);


    /**
     * Creates an OrganicPerson object given the stated id, birthday and a boolean flag to identify in the creation is for the seed population.
     *
     * @param id             The unique id for the new person.
     * @param birthDay       The day of birth in days since the 1/1/1600.
     * @param seedGeneration Flag indicating is the simulation is still creating the seed population.
     */
    public OrganicPerson(final int id, final int birthDay, final boolean seedGeneration) {
        this.id = id;
        if (sex_distribution.getSample()) {
            sex = 'M';
            setPersonsBirthAndDeathDates(birthDay, seedGeneration);
        } else {
            sex = 'F';
            setPersonsBirthAndDeathDates(birthDay, seedGeneration);
        }
    }

    /**
     * Gets age in days at the specified date.
     *
     * @param date Date at which age should be calculated.
     * @return The number of days since birth on the given date.
     */
    public int getDayOfLife(final Date date) {
        int day = DateManipulation.dateToDays(date) - DateManipulation.dateToDays(getBirthDate());
        return day;
    }

    /**
     * Sets timeline to specified timeline.
     *
     * @param timeline The new timeline.
     */
    public void setTimeline(final OrganicTimeline timeline) {
        this.timeline = timeline;
    }

    /**
     * Returns the timeline of the person.
     *
     * @return The persons timeline.
     */
    public OrganicTimeline getTimeline() {
        return timeline;
    }

    //    public OrganicEvent getCurrentEvent() {
    //        timeline = getTimeline();
    //        
    //        return null;    
    //    }

    private void setPersonsBirthAndDeathDates(final int birthDay, final boolean seedGeneration) {
        final UniformDistribution days_of_year_distribution = new UniformDistribution(1, (int) OrganicPopulation.DAYS_PER_YEAR, random);
        // Find an age for person
        int ageOfDeathInYears = seed_age_distribution.getSample();
        int ageOfDeathInDays = (int) ((ageOfDeathInYears - 1) * (OrganicPopulation.DAYS_PER_YEAR)) + days_of_year_distribution.getSample();
        int dayOfBirth = birthDay;


        if (seedGeneration) {
            dayOfBirth = DateManipulation.dateToDays(OrganicPopulation.START_YEAR, 0, 0) + seed_death_distribution.getSample() * 365 - ageOfDeathInDays;

            if (dayOfBirth < OrganicPopulation.getEarliestDate()) {
                OrganicPopulation.setEarliestDate(dayOfBirth);
            }
        }

        // Calculate and set death date
        int dayOfDeath = dayOfBirth + ageOfDeathInDays;

        // Create timeline
        timeline = new OrganicTimeline(dayOfBirth, dayOfDeath);
        timeline.addEvent(dayOfDeath, new OrganicEvent(EventType.DEATH));
    }

    /**
     * Populates timeline with events.
     */
    public void populateTimeline() {

        // Add events to timeline
        addEligibleToMarryEvent();

    }

    /**
     * Changes, adds or removes events on timeline.
     */
    public void updateTimeline(EventType trigger) {

        //Change events on timeline as needed.

        switch (trigger) {
            case DIVORCE: {
                if (remmariageDist.getSample()) {
                    addEligibleToReMarryEvent(this.getTimeline().getEndDate());
                }
            }
            case PARTNERSHIP_ENDED_BY_DEATH:

            default:
                break;
        }

    }

    private void addEligibleToMarryEvent() {
        // Add ELIGIBLE_TO_MARRY event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            date = getBirthDay() + maleAgeAtMarriageDistribution.getSample();
            if (date < getDeathDay())
                timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            date = getBirthDay() + femaleAgeAtMarriageDistribution.getSample();
            if (date < getDeathDay())
                timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
        }
    }

    /**
     * To be called in the case of remarriage after ended partnership.
     *
     * @param minimumDate the new marriage event will be placed AFTER this date
     */
    private void addEligibleToReMarryEvent(int minimumDate) {

        // Add ELIGIBLE_TO_MARRY event
        int date;
        uniformDistribution = new UniformDistribution(minimumDate, getDeathDay(), random);
        date = minimumDate + uniformDistribution.getSample();
        if (date < getDeathDay())
            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
    }

    /**
     * Adds a partnership id to the persons list of partnerships.
     *
     * @param id The id of a new partnership of which the person is part of.
     */
    public void addPartnership(final int id) {
        partnerships.add(id);
    }

    /**
     * Returns the persons birth day in days since 1/1/1600.
     *
     * @return The persons birth day in days since 1/1/1600.
     */
    public int getBirthDay() {
        return timeline.getStartDate();
    }

    /**
     * Returns the persons death day in days since 1/1/1600.
     *
     * @return The persons death day in days since 1/1/1600.
     */
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
