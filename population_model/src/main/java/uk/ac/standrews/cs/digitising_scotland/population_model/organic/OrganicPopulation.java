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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FamilyType;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author  Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPopulation implements IPopulation {

    // Universal population variables
    private static final int DEFAULT_SEED_SIZE = 1000;
    private static final float DAYS_PER_YEAR = 365.25f;
    private static final int START_YEAR = 1780;
    private static final int END_YEAR = 2013;
    private static final int DEFAULT_STEP_SIZE = 1;
    private static final int EPOCH_YEAR = 1600;

    private int earliestDate = DateManipulation.dateToDays(getStartYear(), 0, 0);
    private int currentDay;


    // Population instance required variables
    private String description;
    private List<OrganicPerson> livingPeople = new ArrayList<OrganicPerson>();
    private List<OrganicPerson> deadPeople = new ArrayList<OrganicPerson>();
    private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();


    // Population instance helper variables
    private boolean seedGeneration = true;
    private LinkedList<OrganicPerson> maleMarriageQueue = new LinkedList<OrganicPerson>();
    private LinkedList<OrganicPerson> femaleMarriageQueue = new LinkedList<OrganicPerson>();

    private LinkedList<OrganicPerson> maleSingleQueue = new LinkedList<OrganicPerson>();
    private LinkedList<OrganicPerson> femaleSingleQueue = new LinkedList<OrganicPerson>();
    
    private LinkedList<OrganicPerson> maleCohabitationQueue = new LinkedList<OrganicPerson>();
    private LinkedList<OrganicPerson> femaleCohabitationQueue = new LinkedList<OrganicPerson>();
    
    private LinkedList<OrganicPerson> maleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();
    private LinkedList<OrganicPerson> femaleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();
    

    /*
     * Constructors
     */

    /**
     * Constructs a new OrganicPopulation.
     *     
     * @param description The population descriptor string.
     */
    public OrganicPopulation(final String description) {
        this.description = description;
    }

    /*
     * High level methods
     */

    /**
     * Calls the makeSeed method with the default specified seed size.
     */
    public void makeSeed() {
        makeSeed(getDefaultSeedSize());
    }

    /**
     * Creates a seed population of the specified size.
     * 
     * @param size The number of individuals to be created in the seed population.
     */
    public void makeSeed(final int size) {

    	OrganicPerson.initializeDistributions();
    	
        for (int i = 0; i < size; i++) {
            OrganicPerson person = new OrganicPerson(IDFactory.getNextID(), 0, -1, this, seedGeneration);
            livingPeople.add(person);
        }
        seedGeneration = false;
    }

    /**
     * Calls to the mainIteration method using the specified default time step size.
     * 
     * @param print If set true then prints out year and population information at year new year.
     */
    public void mainIteration(final boolean print) {
        mainIteration(DEFAULT_STEP_SIZE, print);
    }

    /**
     * The events for each time step are progressed and handled by the mainIteration method.
     * 
     * @param timeStepSizeInDays The size of the desired time step in days.
     * @param print If set true then prints out year and population information at year new year.
     */
    public void mainIteration(final int timeStepSizeInDays, final boolean print) {

        while (getCurrentDay() < DateManipulation.dateToDays(getEndYear(), 0, 0)) {
            if (print) {
                printYearEndData();
            }
            int previousDate = getCurrentDay();
            setCurrentDay(getCurrentDay() + timeStepSizeInDays);
            checkAllPeopleForEventsUptoCurrentDayFrom(previousDate);
            partnerTogetherPeopleInPartnershipQueue(FamilyType.MARRIAGE);
            checkAllPartnershipsForEventsUptoCurrentDayFrom(previousDate);
        }

    }

    /*
     * CheckAll methods
     */

    private void checkAllPartnershipsForEventsUptoCurrentDayFrom(final int previousDate) {
        for (int i = 0; i < partnerships.size(); i++) {
            if (partnerships.get(i).getTimeline() != null) {
                // Check all dates between the previous and current date after taking the time step
                for (int j = previousDate; j < getCurrentDay(); j++) {
                    EventType event;
                    if (partnerships.get(i).getTimeline().isDateAvailable(j)) {
                        event = partnerships.get(i).getTimeline().getEvent(j).getEventType();
                        // deal with event
                        switch (event) {
                        case BIRTH:
                            handleBirthEvent(i);
                            break;
                        case DIVORCE:
                            handleDivorceEvent(i);
                            break;
                        case PARTNERSHIP_ENDED_BY_DEATH:
                            handlePartnershipEndedByDeathEvent(i);
                            break;
                        default:
                            break;
                        }
                    }
                }

            }
        }
    }

    private void checkAllPeopleForEventsUptoCurrentDayFrom(final int previousDate) {
        for (int i = 0; i < livingPeople.size(); i++) {
            if (DateManipulation.differenceInDays(getCurrentDay(), DateManipulation.dateToDays(livingPeople.get(i).getBirthDate())) == 0) {
                OrganicPopulationLogger.incPopulation();
                OrganicPopulationLogger.incBirths();
                livingPeople.get(i).populateTimeline();
            }
            if (livingPeople.get(i).getTimeline() != null) {
                // Check all dates between the previous and current date after taking the time step
                for (int j = previousDate; j < getCurrentDay(); j++) {
                    EventType event;
                    if (livingPeople.get(i).getTimeline().isDateAvailable(j)) {
                        event = livingPeople.get(i).getTimeline().getEvent(j).getEventType();
                        // handle with event
                        switch (event) {
                        case COMING_OF_AGE:
                        	handleComingOfAgeEvent(i);
                        	break;
                        case ELIGIBLE_TO_COHABIT:
                        	handleEligableToCohabitEvent(i);
                        	break;
                        case ELIGIBLE_TO_COHABIT_THEN_MARRY:
                        	handleEligableToCohabitThenMarriageEvent(i);
                        	break;
                        case ELIGIBLE_TO_MARRY:
                            handleEligibleToMarryEvent(i);
                            break;
                        case DEATH: // Everyone ends up here eventually
                            handleDeathEvent(i);
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
        }
    }

    /*
     * Event handle methods
     */

    private void handlePartnershipEndedByDeathEvent(final int partnershipListIndex) {
        OrganicPopulationLogger.addNumberOfChildren(partnerships.get(partnershipListIndex).getChildIds().size());
    }

    private void handleDivorceEvent(final int partnershipListIndex) {
        OrganicPerson husband = findOrganicPerson(partnerships.get(partnershipListIndex).getMalePartnerId());
        OrganicPerson wife = findOrganicPerson(partnerships.get(partnershipListIndex).getFemalePartnerId());
        partnerships.get(partnershipListIndex).divorce(husband, wife);
        handlePartnershipEndedByDeathEvent(partnershipListIndex);
    }

    private void handleBirthEvent(final int partnershipListIndex) {
        OrganicPerson[] children = partnerships.get(partnershipListIndex).setUpBirthEvent((OrganicPerson) findPerson(partnerships.get(partnershipListIndex).getMalePartnerId()), (OrganicPerson) findPerson(partnerships.get(partnershipListIndex).getFemalePartnerId()), getCurrentDay());
        for (OrganicPerson child : children) {
            livingPeople.add(child);
        }
    }
    
    private void handleComingOfAgeEvent(final int peopleListIndex) {
    	if (livingPeople.get(peopleListIndex).getSex() == 'M') {
            maleSingleQueue.add(livingPeople.get(peopleListIndex));
        } else {
        	femaleSingleQueue.add(livingPeople.get(peopleListIndex));
        }
    }
    
    private void handleEligableToCohabitEvent(final int peopleListIndex) {
    	if (livingPeople.get(peopleListIndex).getSex() == 'M') {
            maleCohabitationQueue.add(livingPeople.get(peopleListIndex));
        } else {
        	femaleCohabitationQueue.add(livingPeople.get(peopleListIndex));
        }
    }
    
    private void handleEligableToCohabitThenMarriageEvent(final int peopleListIndex) {
    	if (livingPeople.get(peopleListIndex).getSex() == 'M') {
            maleCohabitationThenMarriageQueue.add(livingPeople.get(peopleListIndex));
        } else {
        	femaleCohabitationThenMarriageQueue.add(livingPeople.get(peopleListIndex));
        }
    }

    private void handleEligibleToMarryEvent(final int peopleListIndex) {
        if (livingPeople.get(peopleListIndex).getSex() == 'M') {
            maleMarriageQueue.add(livingPeople.get(peopleListIndex));
        } else {
            femaleMarriageQueue.add(livingPeople.get(peopleListIndex));
        }
    }

    private void handleDeathEvent(final int peopleListIndex) {
        OrganicPerson person = livingPeople.get(peopleListIndex);
        if (person.getSex() == 'M') {
            int index = maleMarriageQueue.indexOf(person);
            if (index != -1) {
                maleMarriageQueue.remove(index);
                OrganicPopulationLogger.incNeverMarried();
            }
        } else {
            int index = femaleMarriageQueue.indexOf(person);
            if (index != -1) {
                femaleMarriageQueue.remove(index);
                OrganicPopulationLogger.incNeverMarried();
            }
        }
        deadPeople.add(livingPeople.remove(peopleListIndex));
        OrganicPopulationLogger.decPopulation();
    }

    /*
     * Helper methods
     */

    private void printYearEndData() {
        if (getCurrentDay() % (int) getDaysPerYear() == 0) {
            System.out.println(EPOCH_YEAR + (int) (getCurrentDay() / getDaysPerYear()));
            System.out.println("Population: " + OrganicPopulationLogger.getPopulation());
        }
    }
    
    private LinkedList<OrganicPerson> getMaleQueueOf(FamilyType type) {
    	switch(type) {
    	    case SINGLE:
    	    	return maleSingleQueue;
    	    case COHABITATION:
    	    	return maleCohabitationQueue;
    	    case COHABITATION_THEN_MARRIAGE:
    	    	return maleCohabitationThenMarriageQueue;
    	    case MARRIAGE:
    	    	return maleMarriageQueue;
    	    default:
    	    	return null;
    	}
    }
    
    private LinkedList<OrganicPerson> getFemaleQueueOf(FamilyType type) {
    	switch(type) {
    	    case SINGLE:
    	    	return femaleSingleQueue;
    	    case COHABITATION:
    	    	return femaleCohabitationQueue;
    	    case COHABITATION_THEN_MARRIAGE:
    	    	return femaleCohabitationThenMarriageQueue;
    	    case MARRIAGE:
    	    	return femaleMarriageQueue;
    	    default:
    	    	return null;
    	}
    }

    private void partnerTogetherPeopleInPartnershipQueue(FamilyType type) {
    	LinkedList<OrganicPerson> maleQueue = getMaleQueueOf(type);
    	LinkedList<OrganicPerson> femaleQueue = getFemaleQueueOf(type);
    	
        // Sets the IDs for the first individuals in each marriage list to null
        Integer firstMaleId = (Integer) null;
        Integer firstFemaleId = (Integer) null;
        // While males exist to be married
        while (!maleQueue.isEmpty()) {
            // Sets first male ID value to that of the first male
            if (firstMaleId == (Integer) null) {
                firstMaleId = maleQueue.getFirst().getId();
                // If the ID of the next male matches that of the first male then none of the remaining males are sutible to be married to any of the aviliable females
            } else if (maleQueue.getFirst().getId() == firstMaleId) {
                break;
            }
            // While there are female in the marriage queue
            while (!femaleQueue.isEmpty()) {
                // Sets first female ID value to that the first female
                if (firstFemaleId == null) {
                    firstFemaleId = femaleQueue.getFirst().getId();
                    // If the ID of the next female matches that of the first female then all females have been considered in relation to the
                    //  currently considered female and none have been sutiable.
                } else if (femaleQueue.getFirst().getId() == firstFemaleId) {
                    // Move next male to head of queue for consideration
                    maleQueue.add(maleQueue.removeFirst());
                    // If new lead male is same as first male then no man eligable to marry any female - thus break
                    if(maleQueue.getFirst().getId() == firstMaleId) {
                        break;
                    }
                }
                // If the two individuals meet the marriage conditions
                if (type == FamilyType.COHABITATION) {
                	if (eligableToCohabit(maleQueue.getFirst(), femaleQueue.getFirst())) {
                		cohabit(maleQueue.getFirst(), femaleQueue.getFirst(), getCurrentDay());
                	} else {
                        // Else if couple not elligable to marry move onto consider male with next female
                        femaleQueue.add(femaleQueue.removeFirst());
                        break;
                    }
                } else if (type == FamilyType.COHABITATION_THEN_MARRIAGE) {
                	if (eligableToCohabitThenMarry(maleQueue.getFirst(), femaleQueue.getFirst())) {
                		cohabitThenMarry(maleQueue.getFirst(), femaleQueue.getFirst(), getCurrentDay());
                	} else {
                        // Else if couple not elligable to marry move onto consider male with next female
                        femaleQueue.add(femaleQueue.removeFirst());
                        break;
                    }
                } else if (type == FamilyType.MARRIAGE) {
                	if (eligableToMarry(maleQueue.getFirst(), femaleQueue.getFirst())) {
                        try {
                        	marry(maleQueue.getFirst(), femaleQueue.getFirst(), getCurrentDay());
                        } catch (NoSuchEventException e) {
                            break;
                        }
                	} else {
                        // Else if couple not elligable to marry move onto consider male with next female
                        femaleQueue.add(femaleQueue.removeFirst());
                        break;
                    }
                }
                    
                removeFromQueue(maleQueue, firstMaleId);
                removeFromQueue(femaleQueue, firstFemaleId);
                firstMaleId = (Integer) null;
                firstFemaleId = (Integer) null;
                break;
                
            }

        }
    }

	private void removeFromQueue(LinkedList<OrganicPerson> queue, Integer firstId) {
		int maleId = queue.getFirst().getId();
		queue.removeFirst();
		if (maleId != firstId) {
		    while (queue.getFirst().getId() != firstId) {
		        queue.add(queue.removeFirst());
		    }
		}
	}
	
	// TODO this is just the same as marriage at the moment - needs some thought/statistics
	private boolean eligableToCohabit(final OrganicPerson male, final OrganicPerson female) {
        boolean resonableAgeDifference = PopulationLogic.partnerAgeDifferenceIsReasonable(DateManipulation.dateToDays(male.getBirthDate()), DateManipulation.dateToDays(female.getBirthDate()));
        boolean notSiblings;
        if (male.getParentsPartnership() == female.getParentsPartnership() && male.getParentsPartnership() != -1) {
            notSiblings = false;
        } else {
            notSiblings = true;
        }
        return resonableAgeDifference && notSiblings;
    }
	
	// TODO this is just the same as marriage at the moment - needs some thought/statistics
	private boolean eligableToCohabitThenMarry(final OrganicPerson male, final OrganicPerson female) {
        boolean resonableAgeDifference = PopulationLogic.partnerAgeDifferenceIsReasonable(DateManipulation.dateToDays(male.getBirthDate()), DateManipulation.dateToDays(female.getBirthDate()));
        boolean notSiblings;
        if (male.getParentsPartnership() == female.getParentsPartnership() && male.getParentsPartnership() != -1) {
            notSiblings = false;
        } else {
            notSiblings = true;
        }
        return resonableAgeDifference && notSiblings;
    }

    private boolean eligableToMarry(final OrganicPerson male, final OrganicPerson female) {
        boolean resonableAgeDifference = PopulationLogic.partnerAgeDifferenceIsReasonable(DateManipulation.dateToDays(male.getBirthDate()), DateManipulation.dateToDays(female.getBirthDate()));
        boolean notSiblings;
        if (male.getParentsPartnership() == female.getParentsPartnership() && male.getParentsPartnership() != -1) {
            notSiblings = false;
        } else {
            notSiblings = true;
        }
        return resonableAgeDifference && notSiblings;
    }

    /**
     * Marries up the given individuals.
     * 
     * @param husband The male to married.
     * @param wife he female to be married.
     * @param days The day of the marriage in days since the 1/1/1600.
     */
    private void marry(final OrganicPerson husband, final OrganicPerson wife, final int days) throws NoSuchEventException {
    	int firstDay = husband.getEvent(EventType.ELIGIBLE_TO_MARRY);
        if (wife.getEvent(EventType.ELIGIBLE_TO_MARRY) > firstDay) {
            firstDay = wife.getEvent(EventType.ELIGIBLE_TO_MARRY);
        }
    	
    	// Create partnership
        Object[] partnershipObjects = OrganicPartnership.createOrganicPartnership(IDFactory.getNextID(), husband, wife, days, getCurrentDay());
        partnerships.add((OrganicPartnership) partnershipObjects[0]);
        if (partnershipObjects.length > 1) {
            for (int i = 1; i < partnershipObjects.length; i++) {
                livingPeople.add((OrganicPerson) partnershipObjects[i]);
            }
        }
        OrganicPopulationLogger.logMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), days), DateManipulation.differenceInDays(wife.getBirthDay(), days));
        husband.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
        wife.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
    }   
    
    // TODO 
    private void cohabit(final OrganicPerson husband, final OrganicPerson wife, final int days) {
    	
    }
    
    // TODO 
    private void cohabitThenMarry(final OrganicPerson husband, final OrganicPerson wife, final int days) {
    	
    }

    /*
     * Getters and setters
     */

    /**
     * Size of initially generated seed population.
     * 
     * @return the defaultSeedSize
     */
    public static int getDefaultSeedSize() {
        return DEFAULT_SEED_SIZE;
    }

    /**
     * The approximate average number of days per year.
     * 
     * @return the daysPerYear
     */
    public static float getDaysPerYear() {
        return DAYS_PER_YEAR;
    }

    /**
     * The start year of the simulation.
     * 
     * @return the startYear
     */
    public static int getStartYear() {
        return START_YEAR;
    }

    /**
     * The end year of the simulation.
     * 
     * @return the endYear
     */
    public static int getEndYear() {
        return END_YEAR;
    }

    /**
     * Returns the earliestDate in days since the 1/1/1600.
     * 
     * @return The earliestDate in days since the 1/1/1600.
     */
    public int getEarliestDate() {
        return earliestDate;
    }

    /**
     * Sets the earliest date field to the specified date.
     * 
     * @param earlyDate The value which earliestDate is to be set to.
     */
    public void setEarliestDate(final int earlyDate) {
        earliestDate = earlyDate;
    }

    /**
     * Returns the population description string.
     * 
     * @return The population description string.
     */
    public String getDescription() {
        return description;
    }

    /*
     * Interface methods
     */

    @Override
    public Iterable<IPerson> getPeople() {
        return new Iterable<IPerson>() {
            @Override
            public Iterator<IPerson> iterator() {

                ArrayList<OrganicPerson> all = new ArrayList<OrganicPerson>();
                all.addAll(livingPeople);
                all.addAll(deadPeople);

                final Iterator<OrganicPerson> iterator = all.iterator();

                return new Iterator<IPerson>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IPerson next() {
                        return (IPerson) iterator.next();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return new Iterable<IPartnership>() {
            @Override
            public Iterator<IPartnership> iterator() {
                final Iterator<OrganicPartnership> iterator = partnerships.iterator();

                return new Iterator<IPartnership>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IPartnership next() {
                        return (IPartnership) iterator.next();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };

            }
        };
    }

    private OrganicPerson findOrganicPerson(final int id) {
        return (OrganicPerson) findPerson(id);
    }

    @Override
    public IPerson findPerson(final int id) {

        ArrayList<OrganicPerson> all = new ArrayList<OrganicPerson>();
        all.addAll(livingPeople);
        all.addAll(deadPeople);

        for (OrganicPerson person : all) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    @Override
    public IPartnership findPartnership(final int id) {

        final int index = ArrayManipulation.binarySplit(partnerships, new ArrayManipulation.SplitComparator<OrganicPartnership>() {

            @Override
            public int check(final OrganicPartnership partnership) {
                return id - partnership.getId();
            }
        });

        return index >= 0 ? partnerships.get(index) : null;
    }

    @Override
    public int getNumberOfPeople() {
        return livingPeople.size() + deadPeople.size();
    }

    @Override
    public int getNumberOfPartnerships() {
        return partnerships.size();
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void setConsistentAcrossIterations(final boolean consistent_across_iterations) {

    }

    /**
     * Temporary testing main method.
     * 
     * @param args Sring Arguments.
     */
    public static void main(final String[] args) {
        System.out.println("--------MAIN HERE---------");
        OrganicPopulation op = new OrganicPopulation("Test Population");
        System.out.println(op.getDescription());
        op.makeSeed();
        op.setCurrentDay(op.getEarliestDate() - 1);
        op.mainIteration(true);

        OrganicPopulationLogger.printLogData();
        int count = 0;
        for (int i = 0; i < OrganicPartnership.getAdjustedNumberOfChildren().length; i++) {
            for (int j = 0; j < OrganicPartnership.getAdjustedNumberOfChildren()[i].size(); j++) {
                count += OrganicPartnership.getAdjustedNumberOfChildren()[i].get(j) - i;
            }
        }
        System.out.println("Left over children: " + count);
        System.out.println("Kids killed by early stop: " + OrganicPopulationLogger.getStopedHavingEarlyDeaths());

    }

    /**
     * Returns current day of simulation.
     * 
     * @return the currentDay
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Sets current day for simulation.
     * 
     * @param currentDay the currentDay to set
     */
    public void setCurrentDay(final int currentDay) {
        this.currentDay = currentDay;
    }
}
