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
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPerson implements IPerson {

    // Univeral person variables
    private static Random random = RandomFactory.getRandom();
    private static final int MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT = 20;
    private static final int COMING_OF_AGE = 15;

    // Universal person ditributions
    private static Distribution<Integer> uniformDistribution;
    private static RemarriageDistribution remmariageDist = new RemarriageDistribution(random);
    private static MaleAgeAtSeedDistribution maleSeedAgeDistribution = new MaleAgeAtSeedDistribution(random);
    private static FemaleAgeAtSeedDistribution femaleSeedAgeDistribution = new FemaleAgeAtSeedDistribution(random);
    private static AgeAtDeathDistribution seed_death_distribution = new AgeAtDeathDistribution(random);
    private static MaleAgeAtMarriageDistribution maleAgeAtMarriageDistribution = new MaleAgeAtMarriageDistribution(random);
    private static FemaleAgeAtMarriageDistribution femaleAgeAtMarriageDistribution = new FemaleAgeAtMarriageDistribution(random);
    private static UniformSexDistribution sex_distribution = new UniformSexDistribution(random);
    private static MaleFirstNameDistribution maleFirstNames;
    private static FemaleFirstNameDistribution femaleFirstNames;
    private static SurnameDistribution surnames;
    private static PartnershipCharacteristicDistribution partnershipCharacteristicDistribution = new PartnershipCharacteristicDistribution(random);
    private static MaleAgeAtCohabitationDistribution maleAgeAtCohabitationDistribution = new MaleAgeAtCohabitationDistribution(random);
    private static FemaleAgeAtCohabitationDistribution femaleAgeAtCohabitationDistribution = new FemaleAgeAtCohabitationDistribution(random);
    
    // Person instance required variables
    private int id;
    private String firstName;
    private String lastName;
    private char sex;
    private ArrayList<Integer> partnerships = new ArrayList<Integer>();
    private int parentPartnershipId;

    // Person instance helper variables
    private OrganicTimeline timeline = null;
    private OrganicPopulation population;
    private boolean seedPerson;

    /*
     * Constructor
     */

    /**
     * 
     * Creates an OrganicPerson object given the stated id, birthday and a boolean flag to identify in the creation is for the seed population.
     *
     * @param id                   The unique id for the new person.
     * @param birthDay             The day of birth in days since the 1/1/1600.
     * @param parentPartnershipId  The id of the parents partnership.
     * @param population           The population which the person is a part of.
     * @param seedGeneration       Flag indicating is the simulation is still creating the seed population.

     */
    public OrganicPerson(final int id, final int birthDay, final int parentPartnershipId, final OrganicPopulation population, final boolean seedGeneration) {
        this.id = id;
        this.population = population;
        this.parentPartnershipId = parentPartnershipId;
        setSeedPerson(seedGeneration);

        try{
            maleFirstNames = new MaleFirstNameDistribution(random);
            femaleFirstNames = new FemaleFirstNameDistribution(random);
            surnames = new SurnameDistribution(random);

        } catch (InconsistentWeightException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        lastName = surnames.getSample();

        if (sex_distribution.getSample()) {
            sex = 'M';
            firstName = maleFirstNames.getSample();
            setPersonsBirthAndDeathDates(birthDay, seedGeneration, population);
        } else {
            sex = 'F';
            firstName = femaleFirstNames.getSample();
            setPersonsBirthAndDeathDates(birthDay, seedGeneration, population);
        }
    }

    /*
     * High level methods
     */

    private void setPersonsBirthAndDeathDates(final int birthDay, final boolean seedGeneration, final OrganicPopulation population) {
        // Find an age for person
        int ageOfDeathInDays = seed_death_distribution.getSample();
        int dayOfBirth = birthDay;

        if (seedGeneration) {
            if (sex == 'M') {
                dayOfBirth = DateManipulation.dateToDays(OrganicPopulation.getStartYear(), 0, 0) + maleSeedAgeDistribution.getSample() - ageOfDeathInDays;
            } else {
                dayOfBirth = DateManipulation.dateToDays(OrganicPopulation.getStartYear(), 0, 0) + femaleSeedAgeDistribution.getSample() - ageOfDeathInDays;
            }

            if (dayOfBirth < population.getEarliestDate()) {
                population.setEarliestDate(dayOfBirth);
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
    	// Decide family type
    	addEligibleToMarryEvent();
//    	FamilyType partnershipCharacteristic = decideFuturePartnershipCharacteristics();
//    	switch(partnershipCharacteristic) {
//    	    case SINGLE:
//    	    	addSingleComingOfAgeEvent();
//    	    	break;
//    	    case COHABITATION:
//    	    	addEligibleToCohabitEvent();
//    	    	break;
//    	    case COHABITATION_THEN_MARRIAGE:
//    	    	addEligableToCohabitThenMarryEvent();
//    	    	break;
//    	    case MARRIAGE:
//    	    	addEligibleToMarryEvent();
//    	    	break;
//    	    default:
//    	    	break;
//    	}
    	
    	
    	// hanlde family type
    	
    	
        // Add events to timeline
//        addEligibleToMarryEvent();

    }

    /**
     * Adds a partnership id to the persons list of partnerships.
     *
     * @param id The id of a new partnership of which the person is part of.
     */
    public void addPartnership(final int id) {
        partnerships.add(id);
    }

    /*
     * Timeline event handling methods
     */

    /**
     * Changes, adds or removes events on timeline.
     * 
     * @param trigger The EventType to be handled
     */
    public void updateTimeline(final EventType trigger) {

        //Change events on timeline as needed.
        switch (trigger) {
        case DIVORCE:
            if (remmariageDist.getSample()) {
                OrganicPartnership partnership = (OrganicPartnership) this.getPopulation().findPartnership(this.getPartnerships().get(this.getPartnerships().size() - 1));
                addEligibleToReMarryEvent(partnership.getTimeline().getEndDate());
            }
            break;
        case PARTNERSHIP_ENDED_BY_DEATH:
            break;
        default:
            break;
        }
    }
    
    private FamilyType decideFuturePartnershipCharacteristics() {
    	return partnershipCharacteristicDistribution.getSample();
    }
    
    private void addSingleComingOfAgeEvent() {
    	timeline.addEvent((int) (COMING_OF_AGE * OrganicPopulation.getDaysPerYear()), new OrganicEvent(EventType.COMING_OF_AGE));
    }
    
    private void addEligibleToCohabitEvent() {
    	// Add ELIGIBLE_TO_COHABIT event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + maleAgeAtCohabitationDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + femaleAgeAtCohabitationDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT));

        }
    }
    
    private void addEligableToCohabitThenMarryEvent() {
    	// Add ELIGIBLE_TO_COHABIT_THEN_MARRY event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + maleAgeAtCohabitationDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT_THEN_MARRY));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + femaleAgeAtCohabitationDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT_THEN_MARRY));

        }
    }

    private void addEligibleToMarryEvent() {
        // Add ELIGIBLE_TO_MARRY event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + maleAgeAtMarriageDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + femaleAgeAtMarriageDistribution.getSample();
            } while (date > getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));

        }
    }

    /**
     * To be called in the case of remarriage after ended partnership.
     *
     * @param minimumDate the new marriage event will be placed AFTER this date
     */
    private void addEligibleToReMarryEvent(final int minimumDate) {

        // Add ELIGIBLE_TO_MARRY event
        int date;

        uniformDistribution = new UniformIntegerDistribution(minimumDate, getDeathDay(), random);
        date = uniformDistribution.getSample();

        if (date < getDeathDay()) {
            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
            OrganicPopulationLogger.incRemarriages();
        }
    }

    /*
     * Date helper methods
     */

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

    private int getDeathAgeInDays() {
        int lengthInDays = timeline.getEndDate() - timeline.getStartDay();
        return lengthInDays;
    }

    /*
     * Getters and setters
     */

    /**
     * Returns the population the person is a member of.
     * 
     * @return The population the person is a member of.
     */
    public OrganicPopulation getPopulation() {
        return population;
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

    /**
     * Returns the day in days since 1/1/1600 of the specified event.
     * 
     * @param event The event to be searched for.
     * @return The day in days since 1/1/1600 of the specified event. If event does not exist returns null.
     * @throws NoSuchEventException Thown when the specified event is not found in the timeline.
     */
    public int getEvent(final EventType event) throws NoSuchEventException {
        return timeline.getDay(event);
    }

    /**
     * Returns the persons birth day in days since 1/1/1600.
     *
     * @return The persons birth day in days since 1/1/1600.
     */
    public int getBirthDay() {
        return timeline.getStartDay();
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
     * Returns the boolean indicating if the person is a member of the origonal seed population.
     * 
     * @return the seedPerson
     */
    public boolean isSeedPerson() {
        return seedPerson;
    }

    /**
     * Sets boolean to indicate if the person is a member of the origonal seed population.
     * 
     * @param seedPerson the seedPerson to set
     */
    private void setSeedPerson(final boolean seedPerson) {
        this.seedPerson = seedPerson;
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
        return lastName;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public Date getBirthDate() {
        return DateManipulation.daysToDate(this.getTimeline().getStartDay());
    }

    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public Date getDeathDate() {
        return DateManipulation.daysToDate(this.getTimeline().getEndDate());
    }

    @Override
    public String getDeathPlace() {
        return null;
    }

    @Override
    public String getDeathCause() {
        return null;
    }

    @Override
    public String getOccupation() {
        return null;
    }

    @Override
    public List<Integer> getPartnerships() {
        return partnerships;
    }

    @Override
    public int getParentsPartnership() {
        return parentPartnershipId;
    }

}
