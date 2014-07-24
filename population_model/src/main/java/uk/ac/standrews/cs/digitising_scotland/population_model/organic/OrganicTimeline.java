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

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by victor on 08/07/14.
 */
public class OrganicTimeline {

    private int startDay;
    private int endDay;

    private HashMap<Integer, OrganicEvent> events = new HashMap<Integer, OrganicEvent>();

    /**
     * Creates a timeline beginning at the specified start day.
     *
     * @param startDay The start day of the timeline in days since 1/1/1600.
     */
    public OrganicTimeline(final int startDay) {
        this.startDay = startDay;
    }

    /**
     * Creates a timeline beginning and ending at the specified start day and end day.
     *
     * @param startDay The start day of the timeline in days since 1/1/1600.
     * @param endDay   The end day of the timeline in days since 1/1/1600.
     */
    public OrganicTimeline(final int startDay, final int endDay) {
        this.startDay = startDay;
        this.endDay = endDay;
    }

    /**
     * Adds an event to the timeline on the specified day.
     *
     * @param day   Day of event in days since 1/1/1600.
     * @param event The given event.
     */
    public void addEvent(final int day, final OrganicEvent event) {
        events.put(day, event);
    }

    /**
     * Removes event occurring on the given day.
     *
     * @param day The day to clear of events.
     */
    public void removeEvent(final int day) {
        events.remove(day);
    }

    /**
     * Removes event from timeline.
     *
     * @param event The event to be removed.
     */
    public void removeEvent(final OrganicEvent event) {
        int removalKey = -1;
        for (int key : events.keySet()) {
            if (events.get(key).equals(event)) {
                removalKey = key;
                break;
            }
        }

        if (removalKey != -1) {
            events.remove(removalKey);
        }
    }

    /**
     * Checks if a given date is free in the timeline (that no events exist on the given day).
     *
     * @param date The given date.
     * @return Boolean indicating day availability.
     */
    public boolean isDateAvailable(final Date date) {
        int day = DateManipulation.dateToDays(date) - startDay;
        return events.containsKey(day);
    }

    /**
     * Checks if a given day is free in the timeline (that no events exist on the given day).
     *
     * @param date The given day in days since the 1/1/1600.
     * @return Boolean indicating day availability.
     */
    public boolean isDateAvailable(final int days) {
        return events.containsKey(days);
    }

    /**
     * Returns the timelines start date.
     *
     * @return The timelines start date in days since the 1/1/1600.
     */
    public int getStartDate() {
        return startDay;
    }

    /**
     * Returns the event occurring on the specified date.
     *
     * @param date The date to be checked.
     * @return The event occurring on the given date.
     */
    public OrganicEvent getEvent(final Date date) {
        int day = DateManipulation.dateToDays(date) - startDay;
        return events.get(day);
    }

    /**
     * Returns the event occurring on the specified day.
     *
     * @param date The day in days since the 1/1/1600 to be checked.
     * @return The event occurring on the given day.
     */
    public OrganicEvent getEvent(final int days) {
        return events.get(days);
    }

    /**
     * Sets the start date.
     *
     * @param startDay The start date in days since the 1/1/1600.
     */
    public void setStartDay(final int startDay) {
        this.startDay = startDay;
    }

    /**
     * Returns the end date of the timeline in days since the 1/1/1600.
     *
     * @return The end date of the timeline in days since the 1/1/1600.
     */
    public int getEndDate() {
        return endDay;
    }

    /**
     * Sets the end date.
     *
     * @param startDay The end date in days since the 1/1/1600.
     */
    public void setEndDate(final int endDay) {
        this.endDay = endDay;
    }

}
