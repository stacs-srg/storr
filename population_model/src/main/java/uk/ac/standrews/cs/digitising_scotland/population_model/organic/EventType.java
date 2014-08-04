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
 * Specifies the possible types of events that can occur in an OrganicTimeline.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum EventType {
	
	/*
	 * Partnership events
	 */
	/**
	 * Indicates the point in time where and individual who will not cohabit or marry can become part of informal child bearing relationships and thus should be added to the singles list.
	 */
	COMING_OF_AGE,
	/**
	 * Indicates the point in time where an individual should be added to a cohabit queue.
	 */
	ELIGIBLE_TO_COHABIT,
	/**
	 * Indicates the point in time where an individual should be added to a cohabit then marry queue.
	 */
	ELIGIBLE_TO_COHABIT_THEN_MARRY,
    /**
     * Indicates the point in time where an individual should be added to a marriage queue.
     */
    ELIGIBLE_TO_MARRY,
    /**
     * Indicates the point in time on the partnership timeline where the birth will occur.
     */
    BIRTH,
    /**
     * Indicates the point of divorce on relationship timeline.
     */
    DIVORCE,
    /**
     * Indicates where a relationship is ended by death.
     */
    PARTNERSHIP_ENDED_BY_DEATH,
    /**
     * Indicatees the point of the end of a cohabitation on relationship timline.
     */
    END_OF_COHABITATION,
    MALE_BEGINS_AFFAIR,
    MALE_ENDS_AFFAIR,
    FEMALE_BEGINS_AFFAIR,
    FEMALE_ENDS_AFFAIR,
    
     
    /*
     * Geographical Events
     */
    /**
     * Indicates the point in time where an individual should move location.
     */
    MOVE_LOCATION,
    /**
     * Indicates the point in time where an individual will leave the population.
     */
    EMIGRATE,
    
    /*
     * Life events
     */
    /**
     * Indicates th epoint in time where an individual dies.
     */
    DEATH
}
