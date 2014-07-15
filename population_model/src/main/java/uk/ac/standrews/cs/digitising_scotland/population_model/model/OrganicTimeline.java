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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.HashMap;
import java.util.Date;

/**
 * Created by victor on 08/07/14.
 */
public class OrganicTimeline {

    private Date startDate;
    private Date endDate;
    private int duration;// = DateManipulation.dateToDays(endDate) - DateManipulation.dateToDays(startDate);
    private int currentDay;

    private HashMap<Integer, OrganicEvent> events = new HashMap<Integer, OrganicEvent>();

    public OrganicTimeline(Date date){
        startDate = date;
    }

    public OrganicTimeline(Date date1, Date date2){
        startDate = date1;
        endDate = date2;
    }

    public OrganicTimeline(Date date, int days){
        startDate = date;
        duration = days;
        //endDate = DateManipulation.daysToDate(DateManipulation.dateToDays(startDate) + days);
    }

    public OrganicTimeline(){
    }

    public void addEvent(int day, OrganicEvent event){
        events.put(day, event);
    }

    public void removeEvent(int day){
        events.remove(day);
    }

    public void removeEvent(OrganicEvent event){
        int removalKey = -1;
        for(int key : events.keySet()){
            if(events.get(key).equals(event)){
                removalKey = key;
                break;
            }
        }

        if(removalKey != -1)
            events.remove(removalKey);
    }

    public boolean isDateAvailable(Date date){
        int day = DateManipulation.dateToDays(date) - DateManipulation.dateToDays(startDate);
        return events.containsKey(day);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }
}
