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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * The OrganicPopulation class models and handles the population as a whole.
 * 
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPopulation implements IPopulation {

    private static boolean debug = false;
    private static final int START_DEBUG_YEAR = 2999;
    private static final int END_DEBUG_YEAR = 3000;

    // Universal population variables
    private static final int DEFAULT_SEED_SIZE = 1000;
    private static final float DAYS_PER_YEAR = 365.25f;
    private static final int START_YEAR = 1780;
    private static final int END_YEAR = 2013;
    private static final int EPOCH_YEAR = 1600;
    private static Random random = RandomFactory.getRandom();

    private int earliestDate = DateManipulation.dateToDays(getStartYear(), 0, 0);
    private int currentDay;

    private PriorityQueue<OrganicEvent> globalEventsQueue = new PriorityQueue<OrganicEvent>();

    // Population instance required variables
    private String description;
    private List<OrganicPerson> livingPeople = new ArrayList<OrganicPerson>();
    private List<OrganicPerson> deadPeople = new ArrayList<OrganicPerson>();
    private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();

    // Population instance helper variables
    private boolean seedGeneration = true;
    private List<OrganicPerson> maleMarriageQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleMarriageQueue = new LinkedList<OrganicPerson>();

    private List<OrganicPerson> maleSingleQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleSingleQueue = new LinkedList<OrganicPerson>();

    private List<OrganicPerson> maleCohabitationQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleCohabitationQueue = new LinkedList<OrganicPerson>();

    private List<OrganicPerson> maleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();

    private List<OrganicPerson> maleSingleAffairsQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleSingleAffairsQueue = new LinkedList<OrganicPerson>();

    private List<OrganicPerson> maleMaritalAffairsQueue = new LinkedList<OrganicPerson>();
    private List<OrganicPerson> femaleMaritalAffairsQueue = new LinkedList<OrganicPerson>();

    private PriorityQueue<AffairWaitingQueueMember> maleAffairsWaitingQueue = new PriorityQueue<AffairWaitingQueueMember>();
    private PriorityQueue<AffairWaitingQueueMember> femaleAffairsWaitingQueue = new PriorityQueue<AffairWaitingQueueMember>();

    private static int maximumNumberOfChildrenInFamily;

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

        OrganicPerson.initializeDistributions(this);
        AffairWaitingQueueMember.initialiseAffairWithMarrieadOrSingleDistribution(this, "affair_with_single_or_married_distributions_data_filename", random);

        for (int i = 0; i < size; i++) {
            OrganicPerson person = new OrganicPerson(IDFactory.getNextID(), 0, -1, this, seedGeneration, null);
            livingPeople.add(person);
        }
        seedGeneration = false;
        OrganicPopulationLogger.initPopulationAtYearEndsArray((int) (getEarliestDate() / DAYS_PER_YEAR) + EPOCH_YEAR, END_YEAR);
    }

    /**
     * Called to begin the event iteration which commences the simulation.
     * 
     * @param print Specifies whether to print year end information to console.
     */
    public void newEventIteration(final boolean print) {
        while (getCurrentDay() < DateManipulation.dateToDays(getEndYear(), 0, 0)) {
            OrganicEvent event = globalEventsQueue.poll();
            if (event == null) {
                break;
            }
            while ((int) (getCurrentDay() / getDaysPerYear()) != (int) (event.getDay() / getDaysPerYear())) {
                OrganicPopulationLogger.addPopulationForYear((int) (getCurrentDay() / DAYS_PER_YEAR) + 1 + EPOCH_YEAR, OrganicPopulationLogger.getPopulation());
                if ((int) (getCurrentDay() / getDaysPerYear()) == START_DEBUG_YEAR - EPOCH_YEAR) {
                    debug = true;
                }
                if ((int) (getCurrentDay() / getDaysPerYear()) == END_DEBUG_YEAR - EPOCH_YEAR) {
                    debug = false;
                }
                if (print) {
                    System.out.println(EPOCH_YEAR + 1 + (int) (getCurrentDay() / getDaysPerYear()));
                    System.out.println("Population: " + OrganicPopulationLogger.getPopulation());
                }
                // FIXME The fudge near the end of the line.
                setCurrentDay(DateManipulation.dateToDays((int) (getCurrentDay() / DAYS_PER_YEAR) + 1 + EPOCH_YEAR, 0, (int) (2 + (getCurrentDay() / DAYS_PER_YEAR) / 100)));
            }
            setCurrentDay(event.getDay());
            if (debug) {
                System.out.println("TO HANDLE EVENT - " + event.getEventType().toString() + " - On day: " + getCurrentDay());
            }
            handleEvent(event);

            partnerTogetherPeopleInRegularPartnershipQueues();
        }
    }

    @Deprecated
    public void eventIteration(final boolean print, final int timeStepSizeInDays) {
        while (getCurrentDay() < DateManipulation.dateToDays(getEndYear(), 0, 0)) {
            if (print) {
                handleYearEndData(print);
            }

            while (globalEventsQueue.peek() != null && globalEventsQueue.peek().getDay() == getCurrentDay()) {
                if (debug) {
                    System.out.println("TO HANDLE EVENT - " + globalEventsQueue.peek().getEventType().toString() + " - On day: " + getCurrentDay());
                }
                handleEvent(globalEventsQueue.remove());
            }

            if (debug && globalEventsQueue.peek().getDay() < currentDay) {
                System.out.println("Current Day: " + getCurrentDay());
                System.out.println("Event Day: " + globalEventsQueue.peek().getDay());
                System.out.println("Event Type: " + globalEventsQueue.peek().getEventType().toString());
            }

            setCurrentDay(getCurrentDay() + timeStepSizeInDays);
            partnerTogetherPeopleInRegularPartnershipQueues();
        }
    }

    private void handleEvent(final OrganicEvent event) {
        if (event.getPartnership() != null) {
            switch (event.getEventType()) {
                case BIRTH:
                    handleBirthEvent(event.getPartnership());
                    break;
                case DIVORCE:
                    handleDivorceEvent(event.getPartnership(), event.getMale(), event.getFemale());
                    break;
                case END_OF_COHABITATION:
                    handleEndOfCohabitation(event.getPartnership(), event.getMale(), event.getFemale());
                    break;
                case PARTNERSHIP_ENDED_BY_DEATH:
                    handlePartnershipEndedByDeathEvent(event.getPartnership());
                    break;
                default:
                    break;
            }
        } else if (event.getPerson() != null) {
            switch (event.getEventType()) {
                case BORN:
                    OrganicPopulationLogger.incPopulation();
                    OrganicPopulationLogger.incBirths();
                    event.getPerson().populateTimeline(false);
                    break;
                case COMING_OF_AGE:
                    handleComingOfAgeEvent(event.getPerson());
                    break;
                case ELIGIBLE_TO_COHABIT:
                    handleEligableToCohabitEvent(event.getPerson());
                    break;
                case ELIGIBLE_TO_COHABIT_THEN_MARRY:
                    handleEligableToCohabitThenMarriageEvent(event.getPerson());
                    break;
                case ELIGIBLE_TO_MARRY:
                    handleEligibleToMarryEvent(event.getPerson());
                    break;
                case DEATH: // Everyone ends up here eventually
                    handleDeathEvent(event.getPerson());
                    break;
                default:
                    break;
            }
        }
    }

    /*
     * Event handle methods
     */

    private void handlePartnershipEndedByDeathEvent(final OrganicPartnership partnership) {
        OrganicPopulationLogger.addNumberOfChildren(partnership.getChildIds().size());
    }

    private void handleDivorceEvent(final OrganicPartnership partnership, final OrganicPerson husband, final OrganicPerson wife) {
        partnership.divorce(husband, wife);
        OrganicPopulationLogger.addNumberOfChildren(partnership.getChildIds().size());
    }

    private void handleEndOfCohabitation(final OrganicPartnership partnership, final OrganicPerson husband, final OrganicPerson wife) {
        partnership.endCohabitation(husband, wife);
        OrganicPopulationLogger.addNumberOfChildren(partnership.getChildIds().size());
    }

    private void handleBirthEvent(final OrganicPartnership partnership) {
        OrganicPerson[] children = partnership.setUpBirthEvent(findOrganicPerson(partnership.getMalePartnerId()), findOrganicPerson(partnership.getFemalePartnerId()), getCurrentDay());
        for (OrganicPerson child : children) {
            livingPeople.add(child);
        }
    }

    private void handleComingOfAgeEvent(final OrganicPerson person) {
        if (person.getSex() == 'M') {
            maleSingleQueue.add(person);
        } else {
            femaleSingleQueue.add(person);
        }
    }

    private void handleEligableToCohabitEvent(final OrganicPerson person) {
        if (person.getSex() == 'M') {
            maleCohabitationQueue.add(person);
        } else {
            femaleCohabitationQueue.add(person);
        }
    }

    private void handleEligableToCohabitThenMarriageEvent(final OrganicPerson person) {
        if (person.getSex() == 'M') {
            maleCohabitationThenMarriageQueue.add(person);
        } else {
            femaleCohabitationThenMarriageQueue.add(person);
        }
    }

    private void handleEligibleToMarryEvent(final OrganicPerson person) {
        if (person.getSex() == 'M') {
            maleMarriageQueue.add(person);
        } else {
            femaleMarriageQueue.add(person);
        }
    }

    private void handleDeathEvent(final OrganicPerson person) {
        deadPeople.add(livingPeople.remove(livingPeople.indexOf(person)));
        OrganicPopulationLogger.decPopulation();
    }

    /*
     * Queue methods
     */

    /**
     * Adds the given person to the AffairsWaitingQueue with the specified date.
     * 
     * @param person The person waiting to have an affair.
     * @param affairDay The day on which the affair begins in days since 1/1/1600.
     */
    public void addPersonToAffairsWaitingQueue(final OrganicPerson person, final int affairDay) {
        if (person.getSex() == 'M') {
            maleAffairsWaitingQueue.add(new AffairWaitingQueueMember(person, affairDay));
        } else {
            femaleAffairsWaitingQueue.add(new AffairWaitingQueueMember(person, affairDay));
        }
    }

    /**
     * Adds event to global events queue.
     * 
     * @param event The event to be added to the global events queue.
     */
    public void addEventToGlobalQueue(final OrganicEvent event) {
        globalEventsQueue.add(event);
    }

    /*
     * Helper methods
     */

    private void handleYearEndData(final boolean print) {
        if ((int) (getCurrentDay() % getDaysPerYear()) == 0) {
            if ((int) (getCurrentDay() / getDaysPerYear()) == START_DEBUG_YEAR - EPOCH_YEAR) {
                debug = true;
            }
            if ((int) (getCurrentDay() / getDaysPerYear()) == END_DEBUG_YEAR - EPOCH_YEAR) {
                debug = false;
            }

            OrganicPopulationLogger.addPopulationForYear((int) (getCurrentDay() / DAYS_PER_YEAR) + EPOCH_YEAR, OrganicPopulationLogger.getPopulation());
            if (print) {
                System.out.println(EPOCH_YEAR + (int) (getCurrentDay() / getDaysPerYear()));
                System.out.println("Population: " + OrganicPopulationLogger.getPopulation());
            }
        }
    }

    private List<OrganicPerson> getMaleQueueOf(final FamilyType type) {
        switch (type) {
            case SINGLE:
            case FEMALE_SINGLE_AFFAIR:
                return maleSingleQueue;
            case COHABITATION:
                return maleCohabitationQueue;
            case COHABITATION_THEN_MARRIAGE:
                return maleCohabitationThenMarriageQueue;
            case MARRIAGE:
                return maleMarriageQueue;
            case MALE_SINGLE_AFFAIR:
                return maleSingleAffairsQueue;
            case MALE_MARITAL_AFFAIR:
            case FEMALE_MARITAL_AFFAIR:
                return maleMaritalAffairsQueue;
            default:
                return null;
        }
    }

    private List<OrganicPerson> getFemaleQueueOf(final FamilyType type) {
        switch (type) {
            case SINGLE:
            case MALE_SINGLE_AFFAIR:
                return femaleSingleQueue;
            case COHABITATION:
                return femaleCohabitationQueue;
            case COHABITATION_THEN_MARRIAGE:
                return femaleCohabitationThenMarriageQueue;
            case MARRIAGE:
                return femaleMarriageQueue;
            case FEMALE_SINGLE_AFFAIR:
                return femaleSingleAffairsQueue;
            case FEMALE_MARITAL_AFFAIR:
            case MALE_MARITAL_AFFAIR:
                return femaleMaritalAffairsQueue;
            default:
                return null;
        }
    }

    private void partnerUpMembersOfAffairsQueue() {
        // check the waiting queue for people ready to have affair
        while (maleAffairsWaitingQueue.peek() != null) {
            if (maleAffairsWaitingQueue.peek().getAffairDay() <= currentDay) {
                if (maleAffairsWaitingQueue.peek().isInterMarital()) {
                    maleMaritalAffairsQueue.add(maleAffairsWaitingQueue.poll().getPerson());
                } else {
                    maleSingleAffairsQueue.add(maleAffairsWaitingQueue.poll().getPerson());
                }
            } else {
                break;
            }
        }
        while (femaleAffairsWaitingQueue.peek() != null) {
            if (femaleAffairsWaitingQueue.peek().getAffairDay() <= currentDay) {
                if (femaleAffairsWaitingQueue.peek().isInterMarital()) {
                    femaleMaritalAffairsQueue.add(femaleAffairsWaitingQueue.poll().getPerson());
                } else {
                    femaleSingleAffairsQueue.add(femaleAffairsWaitingQueue.poll().getPerson());
                }
            } else {
                break;
            }
        }
        // Decide if affair between married people or with single person
        partnerTogetherPeopleInPartnershipQueue(FamilyType.MALE_SINGLE_AFFAIR);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.MALE_MARITAL_AFFAIR);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.FEMALE_SINGLE_AFFAIR);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.FEMALE_MARITAL_AFFAIR);
    }

    private void partnerTogetherPeopleInRegularPartnershipQueues() {
        partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION_THEN_MARRIAGE);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.MARRIAGE);
        partnerUpMembersOfAffairsQueue();
    }

    private void partnerTogetherPeopleInPartnershipQueue(final FamilyType type) {
        // if (DEBUG) {
        // System.out.println("PARTNERING UP QUEUE - " + type.toString());
        // }
        LinkedList<OrganicPerson> maleQueue = (LinkedList<OrganicPerson>) getMaleQueueOf(type);
        LinkedList<OrganicPerson> femaleQueue = (LinkedList<OrganicPerson>) getFemaleQueueOf(type);

        // Sets the IDs for the first individuals in each marriage list to null
        Integer firstMaleId = (Integer) null;
        Integer firstFemaleId = (Integer) null;
        // While males exist to be married
        while (!maleQueue.isEmpty()) {
            // This is an optimisation
            if (maleQueue.getFirst().getDeathDay() < currentDay) {
                maleQueue.removeFirst();
                OrganicPopulationLogger.incNeverMarried();
                break;
            }
            // Sets first male ID value to that of the first male
            if (firstMaleId == (Integer) null) {
                firstMaleId = maleQueue.getFirst().getId();
                // If the ID of the next male matches that of the first male
                // then none of the remaining males are suitable to be married to
                // any of the available females
            } else if (maleQueue.getFirst().getId() == firstMaleId) {
                break;
            }
            // While there are female in the marriage queue
            while (!femaleQueue.isEmpty()) {
                if (femaleQueue.getFirst().getDeathDay() < currentDay) {
                    femaleQueue.removeFirst();
                    OrganicPopulationLogger.incNeverMarried();
                    break;
                }
                // Sets first female ID value to that the first female
                if (firstFemaleId == null) {
                    firstFemaleId = femaleQueue.getFirst().getId();
                    // If the ID of the next female matches that of the first
                    // female then all females have been considered in relation
                    // to the
                    // currently considered female and none have been suitable.
                } else if (femaleQueue.getFirst().getId() == firstFemaleId) {
                    // Move next male to head of queue for consideration
                    maleQueue.add(maleQueue.removeFirst());
                    // If new lead male is same as first male then no man
                    // eligible to marry any female - thus break
                    if (maleQueue.getFirst().getId() == firstMaleId) {
                        break;
                    }
                }
                if (eligableToPartner(maleQueue.getFirst(), femaleQueue.getFirst())) {
                    try {
                        partner(type, maleQueue.getFirst(), femaleQueue.getFirst(), getCurrentDay());

                    } catch (NoSuchEventException e) {
                        break;
                    }
                } else {
                    // Else if couple not eligible to marry move onto consider
                    // male with next female
                    femaleQueue.add(femaleQueue.removeFirst());
                    break;
                }
                removeFromQueue(maleQueue, firstMaleId);
                removeFromQueue(femaleQueue, firstFemaleId);
                firstMaleId = (Integer) null;
                firstFemaleId = (Integer) null;
                break;
            }
        }
    }

    private void removeFromQueue(final LinkedList<OrganicPerson> queue, final Integer firstId) {
        int maleId = queue.getFirst().getId();
        queue.removeFirst();
        if (maleId != firstId) {
            while (queue.getFirst().getId() != firstId) {
                queue.add(queue.removeFirst());
            }
        }
    }

    private boolean eligableToPartner(final OrganicPerson male, final OrganicPerson female) {
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
     * @param wife The female to be married.
     * @param days The day of the marriage in days since the 1/1/1600.
     */
    private void partner(final FamilyType familyType, final OrganicPerson husband, final OrganicPerson wife, final int days) throws NoSuchEventException {
        // Create partnership
        Object[] partnershipObjects = OrganicPartnership.createOrganicPartnership(IDFactory.getNextID(), husband, wife, days, getCurrentDay(), familyType, this);
        partnerships.add((OrganicPartnership) partnershipObjects[0]);
        if (partnershipObjects.length > 1) {
            for (int i = 1; i < partnershipObjects.length; i++) {
                livingPeople.add((OrganicPerson) partnershipObjects[i]);
            }
        }
        // TODO adapt logging methods
        OrganicPopulationLogger.logMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), days), DateManipulation.differenceInDays(wife.getBirthDay(), days));
        husband.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
        wife.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
    }

    /*
     * Getters and setters
     */

    /**
     * Returns the epoch year.
     * 
     * @return The epoch year.
     */
    public static int getEpochYear() {
        return EPOCH_YEAR;
    }

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

    /**
     * Returns partnership with given id. Works by calling findPartnership but then casts to an OrganicPartenrship.
     * 
     * @param id The id of the partnership.
     * @return The partnership.
     */
    public OrganicPartnership findOrganicPartnership(final int id) {
        return (OrganicPartnership) findPartnership(id);
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
     * @param args
     *            String Arguments.
     */
    @SuppressWarnings("magic numbers")
    public static void main(final String[] args) {
        long startTime = System.nanoTime();
        System.out.println("--------MAIN HERE---------");
        OrganicPopulation op = new OrganicPopulation("Test Population");
        OrganicPartnership.setupTemporalDistributionsInOrganicPartnershipClass(op);
        System.out.println(op.getDescription());
        op.makeSeed();
        op.setCurrentDay(op.getEarliestDate() - 1);
        // op.eventIteration(true, DEFAULT_STEP_SIZE);
        op.newEventIteration(true);

        OrganicPopulationLogger.printLogData();

        System.out.println();
        long timeTaken = System.nanoTime() - startTime;
        System.out.println("Run time " + timeTaken / 1000000 + "ms");

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
     * @param currentDay The currentDay to set
     */
    public void setCurrentDay(final int currentDay) {
        this.currentDay = currentDay;
    }

    /**
     * returns the maximum number of children in a family.
     * 
     * @return the maximumNumberOfChildrenInFamily
     */
    public static int getMaximumNumberOfChildrenInFamily() {
        return maximumNumberOfChildrenInFamily;
    }

    /**
     * Sets the maximum number of children in a family.
     * 
     * @param maximumNumberOfChildrenInFamily The maximumNumberOfChildrenInFamily to set
     */
    public void setMaximumNumberOfChildrenInFamily(final int maximumNumberOfChildrenInFamily) {
        OrganicPopulation.maximumNumberOfChildrenInFamily = maximumNumberOfChildrenInFamily;
    }

    /**
     * Returns the state of the debug flag.
     * 
     * @return The boolean value of the debug flag.
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug flag as specified.
     * 
     * @param debug The boolean value to set the debug flag to.
     */
    public static void setDebug(boolean debug) {
        OrganicPopulation.debug = debug;
    }
}
