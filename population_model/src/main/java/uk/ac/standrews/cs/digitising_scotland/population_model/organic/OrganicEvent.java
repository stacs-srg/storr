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

/**
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicEvent implements Comparable<OrganicEvent> {

    private EventType eventType;
    private OrganicPerson person = null;

    private OrganicPartnership partnership = null;
    private OrganicPerson male = null;
    private OrganicPerson female = null;

    private int day;

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
        person.getPopulation().addEventToGlobalQueue(this);
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
        partnership.getPopulation().addEventToGlobalQueue(this);
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

}
