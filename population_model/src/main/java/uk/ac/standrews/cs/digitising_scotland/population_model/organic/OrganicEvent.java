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

/**
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicEvent implements Comparable<OrganicEvent> {

    private EventType eventType;
    private OrganicPerson person = null;
    private OrganicPartnership partnership = null;
    private int day;

    /**
     * Initialises an OrganicEvent given the eventType which is an Enum of possible events.
     *
     * @param eventType Specifies the event type
     */
    public OrganicEvent(final EventType eventType, OrganicPerson person, int day) {
        this.eventType = eventType;
        this.person = person;
        this.day = day;
//        System.out.println("Adding Person Event - " + eventType.toString());
        person.getPopulation().addEventToGlobalQueue(this);
    }
    
    public OrganicEvent(final EventType eventType, OrganicPartnership partnership, int day) {
        this.eventType = eventType;
        this.partnership = partnership;
        this.day = day;
//        System.out.println("Adding Partnership Event - " + eventType.toString());
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
	public int compareTo(OrganicEvent o) {
		if (day < o.day) {
			return -1;
		} else if (day == o.day) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * @return the person
	 */
	public OrganicPerson getPerson() {
		return person;
	}

	/**
	 * @return the partnership
	 */
	public OrganicPartnership getPartnership() {
		return partnership;
	}
	
	public int getDay() {
		return day;
	}


}
