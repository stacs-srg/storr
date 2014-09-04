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

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.LoggingControl;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

/**
 * The OrganicEvent class represents the possible events that can occur within the population.
 * 
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicEvent implements Comparable<OrganicEvent>, Runnable {

    private EventType eventType;
    private OrganicPerson person = null;

    private OrganicPartnership partnership = null;
    private OrganicPerson male = null;
    private OrganicPerson female = null;

    private int day;

    @Override
    public void run() {
        handleEvent(this);
    }

    /**
     * To be used to initialise an OrganicEvent for a person given the eventType which is an Enum of possible events for the given person on the given day.
     *
     * @param eventType Specifies the event type
     * @param person The individual person to which the event pertains.
     * @param day The day on which the given event is to occur.
     */
    public OrganicEvent(final EventType eventType, final OrganicPerson person, final int day) {
        this.eventType = eventType;
        this.person = person;
        this.day = day;
        if (OrganicPopulation.isDebug()) {
            System.out.println("Adding Person Event - " + eventType.toString() + " - On day: " + day);
        }
        OrganicPopulation.addEventToGlobalQueue(this);
    }

    /**
     * To be used to initialise an OrganicEvent for a partnership given the eventType which is an Enum of possible events for the given partnership and OrganicPerson members on the given day.
     *
     * @param eventType Specifies the event type
     * @param partnership The partnership to which the event pertains.
     * @param male The male of the partnership.
     * @param female The female of the partnership.
     * @param day The day on which the given event is to occur.
     */
    public OrganicEvent(final EventType eventType, final OrganicPartnership partnership, final OrganicPerson male, final OrganicPerson female, final int day) {
        this.eventType = eventType;
        this.partnership = partnership;
        this.male = male;
        this.female = female;
        this.day = day;
        if (OrganicPopulation.isDebug()) {
            System.out.println("Adding Partnership Event - " + eventType.toString() + " - On day: " + day);
        }
        OrganicPopulation.addEventToGlobalQueue(this);
    }

    /**
     * Returns the event type.
     *
     * @return The event type
     */
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public int compareTo(final OrganicEvent o) {
        if (day < o.day) {
            return -1;
        } else if (day == o.day) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Returns the stored individual person in the case of a person instance.
     * 
     * @return the person
     */
    public OrganicPerson getPerson() {
        return person;
    }

    /**
     * Returns the stored partnership.
     * 
     * @return the partnership
     */
    public OrganicPartnership getPartnership() {
        return partnership;
    }

    /**
     * Returns the day of the event.
     * 
     * @return The day of the event in days since the 1/1/1600.
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the associated male.
     * 
     * @return The stored male.
     */
    public OrganicPerson getMale() {
        return male;
    }

    /**
     * Returns the associated female.
     * 
     * @return The stored female.
     */
    public OrganicPerson getFemale() {
        return female;
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
                    if (OrganicPopulation.logging) {
                        LoggingControl.populationLogger.incCount();
                    }
                    event.getPerson().populateTimeline(false);
                    break;
                case COMING_OF_AGE:
                    handleComingOfAgeEvent(event.getPerson());
                    //                    partnerTogetherPeopleInRegularPartnershipQueues();
                    break;
                case ELIGIBLE_TO_COHABIT:
                    handleEligableToCohabitEvent(event.getPerson());
                    //                    partnerTogetherPeopleInRegularPartnershipQueues();
                    break;
                case ELIGIBLE_TO_COHABIT_THEN_MARRY:
                    handleEligableToCohabitThenMarriageEvent(event.getPerson());
                    //                    partnerTogetherPeopleInRegularPartnershipQueues();
                    break;
                case ELIGIBLE_TO_MARRY:
                    handleEligibleToMarryEvent(event.getPerson());
                    //                    partnerTogetherPeopleInRegularPartnershipQueues();
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
        if (OrganicPopulation.logging) {
            LoggingControl.logPartnershipEndByDeath(partnership, day);
        }
    }

    private void handleDivorceEvent(final OrganicPartnership partnership, final OrganicPerson husband, final OrganicPerson wife) {
        partnership.divorce(husband, wife);
        if (OrganicPopulation.logging) {
        LoggingControl.logPartnershipEnd(partnership, day, husband, wife);
        }
    }

    private void handleEndOfCohabitation(final OrganicPartnership partnership, final OrganicPerson husband, final OrganicPerson wife) {
        partnership.endCohabitation(husband, wife);
        if (OrganicPopulation.logging) {
        LoggingControl.logPartnershipEnd(partnership, day, husband, wife);
        }
    }

    private void handleBirthEvent(final OrganicPartnership partnership) {
        OrganicPerson[] children = partnership.setUpBirthEvent(OrganicPopulation.findOrganicPerson(partnership.getMalePartnerId()), OrganicPopulation.findOrganicPerson(partnership.getFemalePartnerId()), day);
        for (OrganicPerson child : children) {
            OrganicPopulation.livingPeople.add(child);
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
        try {
            OrganicPopulation.deadPeople.add(OrganicPopulation.livingPeople.remove(OrganicPopulation.livingPeople.indexOf(person)));
            if (OrganicPopulation.logging) {
            LoggingControl.ageAtDeathDistributionLogger.log(day, person.getDeathDay() - person.getBirthDay());
            LoggingControl.populationLogger.decCount();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
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
    public static void addPersonToAffairsWaitingQueue(final OrganicPerson person, final int affairDay) {
        if (person.getSex() == 'M') {
            maleAffairsWaitingQueue.add(new AffairWaitingQueueMember(person, affairDay));
        } else {
            femaleAffairsWaitingQueue.add(new AffairWaitingQueueMember(person, affairDay));
        }
    }

    private volatile static List<OrganicPerson> maleMarriageQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleMarriageQueue = new LinkedList<OrganicPerson>();

    private volatile static List<OrganicPerson> maleSingleQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleSingleQueue = new LinkedList<OrganicPerson>();

    private volatile static List<OrganicPerson> maleCohabitationQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleCohabitationQueue = new LinkedList<OrganicPerson>();

    private volatile static List<OrganicPerson> maleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleCohabitationThenMarriageQueue = new LinkedList<OrganicPerson>();

    private volatile static List<OrganicPerson> maleSingleAffairsQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleSingleAffairsQueue = new LinkedList<OrganicPerson>();

    private volatile static List<OrganicPerson> maleMaritalAffairsQueue = new LinkedList<OrganicPerson>();
    private volatile static List<OrganicPerson> femaleMaritalAffairsQueue = new LinkedList<OrganicPerson>();

    private volatile static PriorityQueue<AffairWaitingQueueMember> maleAffairsWaitingQueue = new PriorityQueue<AffairWaitingQueueMember>();
    private volatile static PriorityQueue<AffairWaitingQueueMember> femaleAffairsWaitingQueue = new PriorityQueue<AffairWaitingQueueMember>();

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
            if (maleAffairsWaitingQueue.peek().getAffairDay() <= day) {
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
            if (femaleAffairsWaitingQueue.peek().getAffairDay() <= day) {
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

    public void partnerTogetherPeopleInRegularPartnershipQueues() {
        partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION_THEN_MARRIAGE);
        partnerTogetherPeopleInPartnershipQueue(FamilyType.MARRIAGE);
        partnerUpMembersOfAffairsQueue();
    }

    private synchronized void partnerTogetherPeopleInPartnershipQueue(final FamilyType type) {
        // if (DEBUG) {
        // writer.println("PARTNERING UP QUEUE - " + type.toString());
        // }
        LinkedList<OrganicPerson> maleQueue = (LinkedList<OrganicPerson>) getMaleQueueOf(type);
        LinkedList<OrganicPerson> femaleQueue = (LinkedList<OrganicPerson>) getFemaleQueueOf(type);

        // Sets the IDs for the first individuals in each marriage list to null
        Integer firstMaleId = (Integer) null;
        Integer firstFemaleId = (Integer) null;
        // While males exist to be married
        while (!maleQueue.isEmpty()) {
            // This is an optimisation
            if (maleQueue.getFirst().getDeathDay() <= day) {
                maleQueue.removeFirst();
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
                if (femaleQueue.getFirst().getDeathDay() <= day) {
                    femaleQueue.removeFirst();
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
                        partner(type, maleQueue.getFirst(), femaleQueue.getFirst(), day);
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

    private void removeFromQueue(final LinkedList<OrganicPerson> queue, final Integer givenId) {
        OrganicPerson first;
        try {
            first = queue.removeFirst();
        } catch (NoSuchElementException e) {
            System.err.println("List Empty");
            return;
        }
        //        int id = queue.getFirst().getId();

        if (first.getId() != givenId) {
            while (queue.getFirst().getId() != givenId) {
                queue.add(queue.removeFirst());
            }
        }
    }

    private boolean eligableToPartner(final OrganicPerson male, final OrganicPerson female) {
        boolean resonableAgeDifference = PopulationLogic.partnerAgeDifferenceIsReasonable(DateManipulation.dateToDays(male.getBirthDate()), DateManipulation.dateToDays(female.getBirthDate()));
        boolean notSiblings;

        if (male.getParentsPartnership() == female.getParentsPartnership() && male.getParentsPartnership() != -1) {
            notSiblings = false;
        } else if (male.getParentsPartnership() != -1 && female.getParentsPartnership() != -1) {
            OrganicPartnership maleParentsPartnership = OrganicPopulation.findOrganicPartnership(male.getParentsPartnership());
            OrganicPartnership femaleParentsPartnership = OrganicPopulation.findOrganicPartnership(female.getParentsPartnership());
            if (maleParentsPartnership.getMalePartnerId() == femaleParentsPartnership.getMalePartnerId() || maleParentsPartnership.getFemalePartnerId() == femaleParentsPartnership.getFemalePartnerId()) {
                notSiblings = false;
            } else {
                notSiblings = true;
            }
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
        Object[] partnershipObjects = OrganicPartnership.createOrganicPartnership(IDFactory.getNextID(), husband, wife, days, day, familyType);
        OrganicPopulation.partnerships.add((OrganicPartnership) partnershipObjects[0]);
        if (partnershipObjects.length > 1) {
            for (int i = 1; i < partnershipObjects.length; i++) {
                OrganicPopulation.livingPeople.add((OrganicPerson) partnershipObjects[i]);
            }
        }
        if (OrganicPopulation.logging) {
            LoggingControl.familyCharacteristicDistributionLogger.log(day, familyType);
            LoggingControl.logPartnership(familyType, day, DateManipulation.differenceInDays(husband.getBirthDay(), days), DateManipulation.differenceInDays(wife.getBirthDay(), days));
        }
        husband.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
        wife.addPartnership(((OrganicPartnership) partnershipObjects[0]).getId());
    }

}
