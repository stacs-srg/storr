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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.CauseOfDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FirstNameForFemalesDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FirstNameForMalesDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.OccupationDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.SurnameDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NoPermissableValueException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalEnumDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.LoggingControl;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * The OrganicPerson class models the variables pertaining to people in the model.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPerson implements IPerson {

    // Universal person variables
    private static Random random = RandomFactory.getRandom();
    private static final int COMING_OF_AGE_AGE = 15;

    // Universal person distributions

    private static UniformSexDistribution sexDistribution = new UniformSexDistribution(random);
    private static FirstNameForMalesDistribution maleFirstNamesDistribution;
    private static FirstNameForFemalesDistribution femaleFirstNamesDistribution;
    private static SurnameDistribution surnamesDistribution;
    private static OccupationDistribution occupationDistribution;
    private static CauseOfDeathDistribution deathCauseOfDistribution;

    private static TemporalEnumDistribution<FamilyType> temporalPartnershipCharacteristicDistribution;
    private static TemporalEnumDistribution<FamilyType> temporalRemarriagePartnershipCharacteristicDistribution;

    private static TemporalIntegerDistribution seedAgeForMalesDistribution;
    private static TemporalIntegerDistribution seedAgeForFemalesDistribution;
    private static TemporalIntegerDistribution deathAgeAtDistribution;
    private static TemporalIntegerDistribution temporalMarriageAgeForMalesDistribution;
    private static TemporalIntegerDistribution temporalMarriageAgeForFemalesDistribution;
    private static TemporalIntegerDistribution temporalCohabitationAgeForMalesDistribution;
    private static TemporalIntegerDistribution temporalCohabitationAgeForFemalesDistribution;

    // Person instance required variables
    private int id;
    private String firstName;
    private String lastName = null;
    private String occupation;
    private String causeOfDeath;
    private char sex;
    private ArrayList<Integer> partnerships = new ArrayList<Integer>();
    private int parentPartnershipId;
    private int startDay;
    private int endDay;

    // Person instance helper variables
    private OrganicPopulation population;
    private boolean seedPerson;
    private int[] plannedBirthDays = { -1, -1 };

    /**
     * Initialises the distributions at runtime which pertain to the OrganicPerson class.
     * 
     * @param population The population to which the distributions pertain to.
     */
    public static void initializeDistributions(final OrganicPopulation population) {
        try {
            maleFirstNamesDistribution = new FirstNameForMalesDistribution(random);
            femaleFirstNamesDistribution = new FirstNameForFemalesDistribution(random);
            surnamesDistribution = new SurnameDistribution(random);
            occupationDistribution = new OccupationDistribution(random);
            deathCauseOfDistribution = new CauseOfDeathDistribution(random);
            seedAgeForMalesDistribution = new TemporalIntegerDistribution(population, "seed_age_for_males_distribution_data_filename", random, false);
            seedAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "seed_age_for_females_distribution_data_filename", random, false);
            deathAgeAtDistribution = new TemporalIntegerDistribution(population, "death_age_at_distributions_data_filename", random, false);
            temporalMarriageAgeForMalesDistribution = new TemporalIntegerDistribution(population, "marriage_age_for_males_distributions_data_filename", random, false);
            temporalMarriageAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "marriage_age_for_females_distributions_data_filename", random, false);

            Enum<?>[] partnershipCharacteristicsArray = { FamilyType.SINGLE, FamilyType.COHABITATION, FamilyType.COHABITATION_THEN_MARRIAGE, FamilyType.MARRIAGE };
            temporalPartnershipCharacteristicDistribution = new TemporalEnumDistribution<FamilyType>(population, "partnership_characteristic_distributions_data_filename", random, partnershipCharacteristicsArray);
            temporalRemarriagePartnershipCharacteristicDistribution = new TemporalEnumDistribution<FamilyType>(population, "partnership_remarriage_characteristic_distributions_data_filename", random, partnershipCharacteristicsArray);

            temporalCohabitationAgeForMalesDistribution = new TemporalIntegerDistribution(population, "cohabitation_age_for_males_distributions_data_filename", random, false);
            temporalCohabitationAgeForFemalesDistribution = new TemporalIntegerDistribution(population, "cohabitation_age_for_females_distributions_data_filename", random, false);
        } catch (InconsistentWeightException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Constructor
     */

    /**
     * Creates an OrganicPerson object given the stated id, birthday and a boolean flag to identify in the creation is for the seed population.
     *
     * @param id The unique id for the new person.
     * @param birthDay The day of birth in days since the 1/1/1600.
     * @param parentPartnershipId The id of the parents partnership.
     * @param population The population which the person is a part of.
     * @param seedGeneration Flag indicating is the simulation is still creating the seed population.
     * @param spawningPartership The instance of the person's parent's partnership.
     */
    public OrganicPerson(final int id, final int birthDay, final int parentPartnershipId, final OrganicPopulation population, final boolean seedGeneration, final OrganicPartnership spawningPartership) {
        this.id = id;
        this.population = population;
        this.parentPartnershipId = parentPartnershipId;
        setSeedPerson(seedGeneration);

        try {
            lastName = null;
            if (parentPartnershipId > 0) {
                lastName = spawningPartership.getFamilyName();
            }

            // in case no name was found or it's the seed
            if (lastName == null) {
                lastName = surnamesDistribution.getSample();
            }

            setOccupation(occupationDistribution.getSample());
            setCauseOfDeath(deathCauseOfDistribution.getSample());
        } catch (NullPointerException e) {
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

        startDay = dayOfBirth;
        endDay = dayOfDeath;
        new OrganicEvent(EventType.BORN, this, dayOfBirth);
        new OrganicEvent(EventType.DEATH, this, dayOfDeath);
    }

    /**
     * Populates timeline with events.
     * 
     * @param previousMarriage Boolean value indicates if the individual has previously been married.
     */
    public void populateTimeline(final boolean previousMarriage) {
        // Decide family type
        FamilyType partnershipCharacteristic = decideFuturePartnershipCharacteristics(previousMarriage);
//        if (OrganicPopulation.logging) {
//            LoggingControl.remarriageFamilyCharacteristicDistributionLogger.log(OrganicPopulation.getCurrentDay(), partnershipCharacteristic);
//        }
        switch (partnershipCharacteristic) {
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

    private FamilyType decideFuturePartnershipCharacteristics(final boolean previousMarriage) {
        if (previousMarriage) {
            return temporalRemarriagePartnershipCharacteristicDistribution.getSample(population.getCurrentDay());
        } else {
            return temporalPartnershipCharacteristicDistribution.getSample(population.getCurrentDay());
        }
    }

    private void addSingleComingOfAgeEvent() {
        int setAge = (int) (COMING_OF_AGE_AGE * OrganicPopulation.getDaysPerYear());
        if (OrganicPopulation.getCurrentDay() - getBirthDay() > setAge) {
            setAge = OrganicPopulation.getCurrentDay() - getBirthDay() + 1;
//            if (OrganicPopulation.logging) {
//                LoggingControl.remarriageFamilyCharacteristicDistributionLogger.log(OrganicPopulation.getCurrentDay(), FamilyType.SINGLE);
//            }
        } else {
            if (OrganicPopulation.logging) {
                LoggingControl.familyCharacteristicDistributionLogger.log(OrganicPopulation.getCurrentDay(), FamilyType.SINGLE);
            }
        }
        new OrganicEvent(EventType.COMING_OF_AGE, this, getBirthDay() + setAge);
    }

    private void addEligibleToCohabitEvent() {
        // Add ELIGIBLE_TO_COHABIT event
        try {
            int date;
            if (sex == 'M') {
                date = temporalCohabitationAgeForMalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            } else {
                date = temporalCohabitationAgeForFemalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            }
            new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT, this, date);
        } catch (NoPermissableValueException e) {
            addSingleComingOfAgeEvent();
        } catch (NotSetUpAtClassInitilisationException e) {
            System.err.println("Non restrited distribution called with restricted values");
        }
    }

    private void addEligableToCohabitThenMarryEvent() {
        // Add ELIGIBLE_TO_COHABIT_THEN_MARRY event
        try {
            int date;
            if (sex == 'M') {
                date = temporalCohabitationAgeForMalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            } else {
                date = temporalCohabitationAgeForFemalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            }
            new OrganicEvent(EventType.ELIGIBLE_TO_COHABIT_THEN_MARRY, this, date);
        } catch (NoPermissableValueException e) {
            addSingleComingOfAgeEvent();
        } catch (NotSetUpAtClassInitilisationException e) {
            System.err.println("Non restrited distribution called with restricted values");
        }
    }

    private void addEligibleToMarryEvent() {
        // Add ELIGIBLE_TO_MARRY event
        try {
            int date;
            if (sex == 'M') {
                date = temporalMarriageAgeForMalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            } else {
                date = temporalMarriageAgeForFemalesDistribution.getSample(population.getCurrentDay(), population.getCurrentDay() - getBirthDay(), getDeathDay() - getBirthDay()) + getBirthDay();
            }
            new OrganicEvent(EventType.ELIGIBLE_TO_MARRY, this, date);
        } catch (NoPermissableValueException e) {
            addSingleComingOfAgeEvent();
        } catch (NotSetUpAtClassInitilisationException e) {
            System.err.println("Non restrited distribution called with restricted values");
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
     * Returns the persons birth day in days since 1/1/1600.
     *
     * @return The persons birth day in days since 1/1/1600.
     */
    public int getBirthDay() {
        return startDay;
    }

    /**
     * Returns the persons death day in days since 1/1/1600.
     *
     * @return The persons death day in days since 1/1/1600.
     */
    public int getDeathDay() {
        return endDay;
    }

    /**
     * Returns the boolean indicating if the person is a member of the original seed population.
     * 
     * @return the seedPerson
     */
    public boolean isSeedPerson() {
        return seedPerson;
    }

    /**
     * Sets boolean to indicate if the person is a member of the original seed population.
     * 
     * @param seedPerson
     *            the seedPerson to set
     */
    private void setSeedPerson(final boolean seedPerson) {
        this.seedPerson = seedPerson;
    }

    public void addDayToRecordOfBirths(int day) {
        if (plannedBirthDays[0] == -1) {
            plannedBirthDays[0] = day;
        } else if (plannedBirthDays[1] == -1) {
            plannedBirthDays[1] = day;
        } else {
            plannedBirthDays[0] = plannedBirthDays[1];
            plannedBirthDays[1] = day;
        }
    }

    public boolean permissibleBirthDay(int day) {

        int interval = (int) (PopulationLogic.getInterChildInterval() * OrganicPopulation.getDaysPerYear());
        for (int i : plannedBirthDays) {
            if (i == -1) {
                return true;
            } else if (i - interval < day && day < i + interval) {
                return false;
            }
        }
        return true;
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
        return DateManipulation.daysToDate(getBirthDay());
    }

    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public Date getDeathDate() {
        return DateManipulation.daysToDate(getDeathDay());
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

    /**
     * Sets the persons occupation to the given occupation.
     * 
     * @param occupation The persons occupation.
     */
    public void setOccupation(final String occupation) {
        this.occupation = occupation;
    }

    /**
     * Returns the cause of death.
     * 
     * @return The cause of death.
     */
    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    /**
     * Sets the persons cause of death to the given reason.
     * 
     * @param causeOfDeath The persons cause of death.
     */
    public void setCauseOfDeath(final String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public int getLifeLengthInDays() {
        return endDay - startDay;
    }

    public static UniformSexDistribution getSexDistribution() {
        return sexDistribution;
    }

    public static FirstNameForMalesDistribution getMaleFirstNamesDistribution() {
        return maleFirstNamesDistribution;
    }

    public static FirstNameForFemalesDistribution getFemaleFirstNamesDistribution() {
        return femaleFirstNamesDistribution;
    }

    public static SurnameDistribution getSurnamesDistribution() {
        return surnamesDistribution;
    }

    public static OccupationDistribution getOccupationDistribution() {
        return occupationDistribution;
    }

    public static CauseOfDeathDistribution getDeathCauseOfDistribution() {
        return deathCauseOfDistribution;
    }

    public static TemporalEnumDistribution<FamilyType> getTemporalPartnershipCharacteristicDistribution() {
        return temporalPartnershipCharacteristicDistribution;
    }

    public static TemporalEnumDistribution<FamilyType> getTemporalRemarriagePartnershipCharacteristicDistribution() {
        return temporalRemarriagePartnershipCharacteristicDistribution;
    }

    public static TemporalIntegerDistribution getSeedAgeForMalesDistribution() {
        return seedAgeForMalesDistribution;
    }

    public static TemporalIntegerDistribution getSeedAgeForFemalesDistribution() {
        return seedAgeForFemalesDistribution;
    }

    public static TemporalIntegerDistribution getDeathAgeAtDistribution() {
        return deathAgeAtDistribution;
    }

    public static TemporalIntegerDistribution getTemporalMarriageAgeForMalesDistribution() {
        return temporalMarriageAgeForMalesDistribution;
    }

    public static TemporalIntegerDistribution getTemporalMarriageAgeForFemalesDistribution() {
        return temporalMarriageAgeForFemalesDistribution;
    }

    public static TemporalIntegerDistribution getTemporalCohabitationAgeForMalesDistribution() {
        return temporalCohabitationAgeForMalesDistribution;
    }

    public static TemporalIntegerDistribution getTemporalCohabitationAgeForFemalesDistribution() {
        return temporalCohabitationAgeForFemalesDistribution;
    }

}
