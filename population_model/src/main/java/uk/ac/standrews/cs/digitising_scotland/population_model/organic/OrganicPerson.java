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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.CauseOfDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FirstNameForFemalesDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FirstNameForMalesDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.OccupationDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.SurnameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDivorceRemarriageBooleanDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalPartnershipCharacteristicDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
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

    // Universal person variables
    private static Random random = RandomFactory.getRandom();
    private static final int MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT = 20;
    private static final int COMING_OF_AGE = 15;

    // Universal person distributions
    
    private static UniformSexDistribution sexDistribution = new UniformSexDistribution(random);
    private static FirstNameForMalesDistribution maleFirstNamesDistribution;
    private static FirstNameForFemalesDistribution femaleFirstNamesDistribution;
    private static SurnameDistribution surnamesDistribution;
    private static OccupationDistribution occupationDistribution;
    private static CauseOfDeathDistribution deathCauseOfDistribution;
    
    private static TemporalDivorceRemarriageBooleanDistribution temporalDivorceRemarriageBooleanDeistribution;
    private static TemporalPartnershipCharacteristicDistribution temporalPartnershipCharacteristicDistribution;
    
    private static TemporalIntegerDistribution seedAgeForMalesDistribution;
    private static TemporalIntegerDistribution seedAgeForFemalesDistribution;
    private static TemporalIntegerDistribution deathAgeAtDistribution;
    private static TemporalIntegerDistribution temporalMarriageAgeForMalesDistribution;
    private static TemporalIntegerDistribution temporalMarriageAgeForFemalesDistribution;
    private static TemporalIntegerDistribution temporalCohabitationAgeForMalesDistribution;
  	private static TemporalIntegerDistribution temporalCohabitationAgeForFemalesDistribution;
  	private static TemporalIntegerDistribution temporalRemarriageTimeToDistribution; 	
  	
    
    // Person instance required variables
    private int id;
    private String firstName;
    private String lastName = null;
    private String occupation;
    private String causeOfDeath;
    private char sex;
    private ArrayList<Integer> partnerships = new ArrayList<Integer>();
    private int parentPartnershipId;

    // Person instance helper variables
    private OrganicTimeline timeline = null;
    private OrganicPopulation population;
    private boolean seedPerson;

    /**
     * Distribution initialization 
     */
    public static void initializeDistributions (OrganicPopulation population) {
    	try{
            maleFirstNamesDistribution = new FirstNameForMalesDistribution(random);
            femaleFirstNamesDistribution = new FirstNameForFemalesDistribution(random);
            surnamesDistribution = new SurnameDistribution(random);
            occupationDistribution = new OccupationDistribution(random);
            deathCauseOfDistribution = new CauseOfDeathDistribution(random);
            temporalDivorceRemarriageBooleanDeistribution = new TemporalDivorceRemarriageBooleanDistribution(population, "divorce_remarriage_boolean_distributions_data_filename", random);
            seedAgeForMalesDistribution = new TemporalIntegerDistribution(population, "seed_age_for_males_distribution_data_filename", random);
            seedAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "seed_age_for_females_distribution_data_filename", random);
            deathAgeAtDistribution = new TemporalIntegerDistribution(population, "death_age_at_distributions_data_filename", random);
            temporalMarriageAgeForMalesDistribution = new TemporalIntegerDistribution(population, "marriage_age_for_males_distributions_data_filename", random);
            temporalMarriageAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "marriage_age_for_females_distributions_data_filename", random);
            temporalPartnershipCharacteristicDistribution = new TemporalPartnershipCharacteristicDistribution(population, "partnership_characteristic_distributions_data_filename", random);
            temporalCohabitationAgeForMalesDistribution = new TemporalIntegerDistribution(population, "cohabitation_age_for_males_distributions_data_filename", random);
            temporalCohabitationAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "cohabitation_age_for_females_distributions_data_filename", random);            
            temporalRemarriageTimeToDistribution = new TemporalIntegerDistribution(population, "remarraige_time_to_distributions_data_filename", random);
    	} catch (InconsistentWeightException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
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
    public OrganicPerson(final int id, final int birthDay, final int parentPartnershipId, final OrganicPopulation population, final boolean seedGeneration, final OrganicPartnership spawningPartership) {
        this.id = id;
        this.population = population;
        this.parentPartnershipId = parentPartnershipId;
        setSeedPerson(seedGeneration);

        try{
        	lastName = null;
            if(parentPartnershipId > 0){
                lastName = spawningPartership.getFamilyName();
            }
            
            // in case no name was found or it's the seed
            if(lastName == null)
                lastName = surnamesDistribution.getSample();

        	setOccupation(occupationDistribution.getSample());
        	setCauseOfDeath(deathCauseOfDistribution.getSample());
        }
        catch(NullPointerException e){
        	initializeDistributions(population);
        }
        
        if (sexDistribution.getSample()) {
            sex = 'M';
            firstName = maleFirstNamesDistribution.getSample();
            setPersonsBirthAndDeathDates(birthDay, seedGeneration, population);
        } else {
            sex = 'F';
            firstName = femaleFirstNamesDistribution.getSample();
            setPersonsBirthAndDeathDates(birthDay, seedGeneration, population);
        }
    }

    /*
     * High level methods
     */

    private void setPersonsBirthAndDeathDates(final int birthDay, final boolean seedGeneration, final OrganicPopulation population) {
        // Find an age for person
        int ageOfDeathInDays = deathAgeAtDistribution.getSample(population.getCurrentDay());
        int dayOfBirth = birthDay;

        if (seedGeneration) {
            if (sex == 'M') {
                dayOfBirth = DateManipulation.dateToDays(OrganicPopulation.getStartYear(), 0, 0) + seedAgeForMalesDistribution.getSample(population.getCurrentDay()) - ageOfDeathInDays;
            } else {
                dayOfBirth = DateManipulation.dateToDays(OrganicPopulation.getStartYear(), 0, 0) + seedAgeForFemalesDistribution.getSample(population.getCurrentDay()) - ageOfDeathInDays;
            }

            if (dayOfBirth < population.getEarliestDate()) {
                population.setEarliestDate(dayOfBirth);
            }
        }

        // Calculate and set death date
        int dayOfDeath = dayOfBirth + ageOfDeathInDays;

        // Create timeline
        timeline = new OrganicTimeline(dayOfBirth, dayOfDeath);
        timeline.addEvent(dayOfBirth, new OrganicEvent(EventType.BORN, this, dayOfBirth));
        timeline.addEvent(dayOfDeath, new OrganicEvent(EventType.DEATH, this, dayOfDeath));
    }

    /**
     * Populates timeline with events.
     */
    public void populateTimeline() {
    	// Decide family type
//    	addEligibleToMarryEvent();
    	FamilyType partnershipCharacteristic = decideFuturePartnershipCharacteristics();
    	switch(partnershipCharacteristic) {
    	    case SINGLE:
    	    	addSingleComingOfAgeEvent();
    	    	break;
    	    case COHABITATION:
    	    	addEligibleToCohabitEvent();
    	    	break;
    	    case COHABITATION_THEN_MARRIAGE:
    	    	addEligableToCohabitThenMarryEvent();
    	    	break;
    	    case MARRIAGE:
    	    	addEligibleToMarryEvent();
    	    	break;
    	    default:
    	    	break;
    	}
    	
    	
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
    
    public void addRemarriageEventIfApplicable(int earlistRemarriageDay) {
    	 if (temporalDivorceRemarriageBooleanDeistribution.getSample(population.getCurrentDay())) {
             addEligibleToReMarryEvent(earlistRemarriageDay);
         }
    }
    
    private FamilyType decideFuturePartnershipCharacteristics() {
    	return temporalPartnershipCharacteristicDistribution.getSample(population.getCurrentDay());
    }
    
    private void addSingleComingOfAgeEvent() {
    	timeline.addEvent(getBirthDay() + (int) (COMING_OF_AGE * OrganicPopulation.getDaysPerYear()), new OrganicEvent(EventType.COMING_OF_AGE, this, getBirthDay() + (int) (COMING_OF_AGE * OrganicPopulation.getDaysPerYear())));
    }
    
    private void addEligibleToCohabitEvent() {
    	// Add ELIGIBLE_TO_COHABIT event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalCohabitationAgeForMalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT, this, date));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalCohabitationAgeForFemalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT, this, date));

        }
    }
    
    private void addEligableToCohabitThenMarryEvent() {
    	// Add ELIGIBLE_TO_COHABIT_THEN_MARRY event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalCohabitationAgeForMalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT_THEN_MARRY, this, date));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalCohabitationAgeForFemalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT_THEN_MARRY, this, date));

        }
    }

    private void addEligibleToMarryEvent() {
        // Add ELIGIBLE_TO_MARRY event
        int date;
        if (sex == 'M') {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalMarriageAgeForMalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY, this, date));

        } else {
            // time in days to birth from 1/1/1600 + marriage age in days
            do {
                date = getBirthDay() + temporalMarriageAgeForFemalesDistribution.getSample(population.getCurrentDay());
            } while (date >= getDeathDay() && getDeathAgeInDays() > MIN_DEATH_AGE_FOR_NO_PARTNERSHIP_EVENT * OrganicPopulation.getDaysPerYear());

            timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY, this, date));

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
        do {
        	date = temporalRemarriageTimeToDistribution.getSample(population.getCurrentDay()) + minimumDate;
        } while (!PopulationLogic.dateBeforeDeath(date, getDeathDay()));

        timeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY, this, date));
        OrganicPopulationLogger.incRemarriages();
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

    public Integer[] getListOfAffairStartDays() {
    	return timeline.getAllDaysOfEventType(EventType.AFFAIR);
    }
    
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
        return causeOfDeath;
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

    @Override
    public List<Integer> getPartnerships() {
        return partnerships;
    }

    @Override
    public int getParentsPartnership() {
        return parentPartnershipId;
    }

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(String causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

}
