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
    private int duration = DateManipulation.dateToDays(endDate) - DateManipulation.dateToDays(startDate);
    private int currentDay;

    private HashMap<Integer, OrganicEvent> events = new HashMap<Integer, OrganicEvent>();

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
