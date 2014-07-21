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
    private int duration;// = DateManipulation.dateToDays(endDate) - DateManipulation.dateToDays(startDate);
    private int currentDay;

    private HashMap<Integer, OrganicEvent> events = new HashMap<Integer, OrganicEvent>();

    public OrganicTimeline(final int startDay) {
        this.startDay = startDay;
    }

    public OrganicTimeline(final int startDay, final int endDay) {
    	this.startDay = startDay;
    	this.endDay = endDay;
    }

    public void addEvent(final int day, final OrganicEvent event) {
        events.put(day, event);
    }

    public void removeEvent(final int day) {
        events.remove(day);
    }

    public void removeEvent(final OrganicEvent event) {
        int removalKey = -1;
        for (int key : events.keySet()) {
            if (events.get(key).equals(event)) {
                removalKey = key;
                break;
            }
        }

        if (removalKey != -1)
            events.remove(removalKey);
    }

    public boolean isDateAvailable(final Date date) {
        int day = DateManipulation.dateToDays(date) - startDay;
        return events.containsKey(day);
    }
    
    public boolean isDateAvailable(final int days) {
        return events.containsKey(days);
    }

    public int getStartDate() {
        return startDay;
    }

    public OrganicEvent getEvent(Date date) {
        int day = DateManipulation.dateToDays(date) - startDay;
        return events.get(day);
    }
    
    public OrganicEvent getEvent(int days) {
        return events.get(days);
    }

    public void setStartDay(final int startDay) {
        this.startDay = startDay;
    }

    public int getEndDate() {
        return endDay;
    }

    public void setEndDate(final int endDay) {
        this.endDay = endDay;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(final int currentDay) {
        this.currentDay = currentDay;
    }
}
