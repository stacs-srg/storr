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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AgeAtDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FemaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.MaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.*;

/**
 * Created by victor on 11/06/14.
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPopulation implements IPopulation {

    String description;

    /**
     * Seed parameters.
     */
    CompactPopulation seedPopulation;
    public static final int DEFAULT_SEED_SIZE = 1000;

    /**
     * The approximate average number of days per year.
     */
    public static final float DAYS_PER_YEAR = 365.25f;

    /**
     * The start year of the simulation.
     */
    public static final int START_YEAR = 1780;
    private int currentDay = 0;

    /**
     * The end year of the simulation.
     */
    public static final int END_YEAR = 2013;
    public int totalNumberOfDays = DateManipulation.dateToDays(END_YEAR, 1, 1) - DateManipulation.dateToDays(START_YEAR, 1, 1);

    /**
     * Additional parameters
     */
    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;

    private static int DEFAULT_STEP_SIZE = 1;

    Random random = RandomFactory.getRandom();
    private Distribution<Integer> seed_age_distribution = new UniformDistribution(0, 70, random);
    private Distribution<Boolean> sex_distribution = new UniformSexDistribution(random);
    private Distribution<Integer> age_at_death_distribution = new AgeAtDeathDistribution(random);
    private Distribution<Integer> maleAgeAtMarriageDistrobution = new MaleAgeAtMarriageDistribution(random);
    private Distribution<Integer> femaleAgeAtMarriageDistrobution = new FemaleAgeAtMarriageDistribution(random);


    private List<OrganicPerson> people = new ArrayList<OrganicPerson>();
    private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();
    
    private LinkedList<OrganicPerson> maleParnershipQueue = new LinkedList<OrganicPerson>();
    private LinkedList<OrganicPerson> femaleParnershipQueue = new LinkedList<OrganicPerson>();
    
    private LinkedList<Integer> maleInitialPartnershipOrderer = new LinkedList<Integer>();
    private LinkedList<Integer> femaleInitialPartnershipOrderer = new LinkedList<Integer>();

    public void makeSeed(int size) {

        for (int i = 0; i < size; i++) {

            if (sex_distribution.getSample())
                people.add(IDFactory.getNextID(), new OrganicPerson('M'));
            else
                people.add(IDFactory.getNextID(), new OrganicPerson('F'));
        }
    }

    public void makeSeed() {
        makeSeed(DEFAULT_SEED_SIZE);
    }

    public void generate_timelines() {

        UniformDistribution days_of_year_distribution = new UniformDistribution(1, (int) DAYS_PER_YEAR, random);

        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getTimeline() == null) {

                OrganicPerson currentPerson = people.get(i);
                OrganicTimeline currentTimeline;

                //Math for dates of birth and death
                Date currentDateOfBirth;
                int age = seed_age_distribution.getSample();
                int auxiliary = (int) ((age - 1) * (DAYS_PER_YEAR)) + days_of_year_distribution.getSample();
                auxiliary = DateManipulation.dateToDays(START_YEAR, 1, 1) - auxiliary;

                currentDateOfBirth = DateManipulation.daysToDate(auxiliary);

                Distribution<Integer> seed_death_distribution = new UniformDistribution(age, 100, random);
                auxiliary = DateManipulation.dateToDays(START_YEAR,1,1) + (seed_death_distribution.getSample() - age)*(int)DAYS_PER_YEAR;
                Date currentDateOfDeath = DateManipulation.daysToDate(auxiliary);


                currentTimeline = new OrganicTimeline(currentDateOfBirth, currentDateOfDeath);
                
                // Add ELIGIBLE_TO_MARRY event
                int date;
                if(currentPerson.getSex() == 'M') {
                	// time in days to birth from 1/1/1600 + marriage age in days
                	date = DateManipulation.dateToDays(currentTimeline.getStartDate()) + maleAgeAtMarriageDistrobution.getSample();
                	currentTimeline.addEvent(date , new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
                } else {
                	// time in days to birth from 1/1/1600 + marriage age in days
                	date = DateManipulation.dateToDays(currentTimeline.getStartDate()) + maleAgeAtMarriageDistrobution.getSample();
                	currentTimeline.addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
                }
                
                // If marriage date is before simulation start date then add to respective partnership queue
                //  Must be added to the partnership queue in the order that would be expected if simulation had occurred naturally.
                if(date <= DateManipulation.dateToDays(START_YEAR, 0, 0)) {
                	if(currentPerson.getSex() == 'M') {
                		Iterator iter = maleInitialPartnershipOrderer.iterator();
                		int count = 0;
                		while (iter.hasNext()) {
							if(date < (Integer)iter.next()) {
								maleInitialPartnershipOrderer.add(count, date);
								maleParnershipQueue.add(count, currentPerson);
								break;
							}
							count++;
						}
                	} else if(currentPerson.getSex() == 'F') {
                		Iterator iter = femaleInitialPartnershipOrderer.iterator();
                		int count = 0;
                		while (iter.hasNext()) {
							if(date < (Integer)iter.next()) {
								femaleInitialPartnershipOrderer.add(count, date);
								femaleParnershipQueue.add(count, currentPerson);
								break;
							}
							count++;
						}
                	} 
                }
                maleInitialPartnershipOrderer.clear();
                femaleInitialPartnershipOrderer.clear();
                
                currentPerson.setTimeline(currentTimeline);               
            }
        }
        marryUpPeople();
    }
    
    public void marryUpPeople() {
    	Iterator iter = maleParnershipQueue.iterator();
    	while (iter.hasNext()) {
			if(!femaleParnershipQueue.isEmpty()) {
				marry((OrganicPerson)iter.next(),femaleParnershipQueue.getFirst());
				// remove people from queues
				
			}
			
		}
    }
    
    public void marry(OrganicPerson husband, OrganicPerson wife) {
    	// Create partnership
    	// Add both parties
    	// Create timeline
    	// Place events on timeline - births and divorces
    	//     	
    }

    public void mainIteration() {
        mainIteration(DEFAULT_STEP_SIZE);
    }

    public void mainIteration(int timeStepSizeInDays) {

    }

    /**
     * Methods from interface.
     */

    @Override
    public Iterable<IPerson> getPeople() {
        return new Iterable<IPerson>() {
            @Override
            public Iterator<IPerson> iterator() {

                final Iterator iterator = people.iterator();

                return new Iterator<IPerson>(){

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IPerson next() {
                        return (IPerson)iterator.next();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            };
        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return new Iterable<IPartnership>() {
            @Override
            public Iterator<IPartnership> iterator() {

                final Iterator iterator = partnerships.iterator();

                return new Iterator<IPartnership>(){

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IPartnership next() {
                        return (IPartnership)iterator.next();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            };
        };
    }

    @Override
    public IPerson findPerson(int id) {
        int index, binaryStep;
        for(binaryStep = 1 ; binaryStep<people.size() ; binaryStep <<= 1);
        for(index = 0 ; binaryStep != 0 ; binaryStep >>=1) {
            if (index + binaryStep < people.size() && people.get(index + binaryStep).getId() <= id) {
                index += binaryStep;
            }
        }
        if(people.get(index).getId() == id) { return people.get(index); }

        return null;
    }

    @Override
    public IPartnership findPartnership(int id){
        int index, binaryStep;
        for(binaryStep = 1 ; binaryStep<partnerships.size() ; binaryStep <<= 1);
        for(index = 0 ; binaryStep != 0 ; binaryStep >>=1) {
            if (index + binaryStep < partnerships.size() && partnerships.get(index + binaryStep).getId() <= id) {
                index += binaryStep;
            }
        }
        if(partnerships.get(index).getId() == id) { return partnerships.get(index); }

        return null;
    }

    @Override
    public int getNumberOfPeople() {
        return people.size();
    }

    @Override
    public int getNumberOfPartnerships() {
        return partnerships.size();
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setConsistentAcrossIterations(boolean consistent_across_iterations) {

    }

    //Testing purposes
    public static void main(String[] args) {
        System.out.println("--------MAIN HERE---------");
        OrganicPopulation op = new OrganicPopulation();
        op.makeSeed();
        op.generate_timelines();
        for (int i = 0; i < op.getNumberOfPeople(); i++) {
        	System.out.println();
            System.out.println("BORN: " + op.people.get(i).getBirthDate());
            System.out.println("DIED: " + op.people.get(i).getDeathDate());
            int x = (DateManipulation.dateToDays(op.people.get(i).getDeathDate()) - DateManipulation.dateToDays(op.people.get(i).getBirthDate()))/365;
            System.out.println("ALIVE FOR: " + x);
            System.out.println();
        }
    }
}
