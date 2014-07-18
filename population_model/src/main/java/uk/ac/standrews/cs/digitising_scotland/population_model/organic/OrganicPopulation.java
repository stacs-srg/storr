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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AgeAtDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.FemaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.MaleAgeAtMarriageDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by victor on 11/06/14.
 *
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
	public static int seedStartYear;
	
    private int earliestDate = DateManipulation.dateToDays(START_YEAR,1,1);
    
	private int currentDay;

	/**
	 * The end year of the simulation.
	 */
	public static final int END_YEAR = 2013;

	/**
	 * Additional parameters
	 */
	private static final int DAYS_IN_DECEMBER = 31;
	private static final int DECEMBER_INDEX = 11;

	private static int DEFAULT_STEP_SIZE = 1;

	boolean firstMale = true;
	boolean firstFemale = true;

	Random random = RandomFactory.getRandom();
	private Distribution<Integer> seed_age_distribution = new UniformDistribution(0, 70, random);
	private Distribution<Boolean> sex_distribution = new UniformSexDistribution(random);
	private Distribution<Integer> age_at_death_distribution = new AgeAtDeathDistribution(random);
	private Distribution<Integer> maleAgeAtMarriageDistribution = new MaleAgeAtMarriageDistribution(random);
	private Distribution<Integer> femaleAgeAtMarriageDistribution = new FemaleAgeAtMarriageDistribution(random);

	private List<OrganicPerson> people = new ArrayList<OrganicPerson>();
	private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();

	private LinkedList<OrganicPerson> malePartnershipQueue = new LinkedList<OrganicPerson>();
	private LinkedList<OrganicPerson> femalePartnershipQueue = new LinkedList<OrganicPerson>();

	private LinkedList<Integer> maleInitialPartnershipOrderer = new LinkedList<Integer>();
	private LinkedList<Integer> femaleInitialPartnershipOrderer = new LinkedList<Integer>();

	public void makeSeed(final int size) {

		for (int i = 0; i < size; i++) {
			OrganicPopulationLogger.incPopulation();
			OrganicPerson person;
			if (sex_distribution.getSample()) {
				person = new OrganicPerson(IDFactory.getNextID(), 'M');
				person = setPersonsBirthAndDeathDates(person);
				people.add(person);
			} else {
				person = new OrganicPerson(IDFactory.getNextID(), 'F');
				person = setPersonsBirthAndDeathDates(person);
				people.add(person);
			} 
		}
	}

	public void makeSeed() {
		makeSeed(DEFAULT_SEED_SIZE);
	}



	private OrganicPerson setPersonsBirthAndDeathDates(OrganicPerson currentPerson) {
		final UniformDistribution days_of_year_distribution = new UniformDistribution(1, (int) DAYS_PER_YEAR, random);

		// Find an age for person
		int age = seed_age_distribution.getSample();
		int auxiliary = (int) ((age - 1) * (DAYS_PER_YEAR)) + days_of_year_distribution.getSample();
		int currentDayOfBirth = DateManipulation.dateToDays(START_YEAR, 1, 1) - auxiliary;

		// Set birth date

		if(currentDayOfBirth < earliestDate)
            earliestDate = currentDayOfBirth;


		// Calculate and set death date
        // TODO allow death before start age
		Distribution<Integer> seed_death_distribution = new UniformDistribution(age, 100, random);
		int currentDayOfDeath = DateManipulation.dateToDays(START_YEAR, 1, 1) + (seed_death_distribution.getSample() - age) * (int) DAYS_PER_YEAR;
		
		// Create timeline
		OrganicTimeline currentTimeline = new OrganicTimeline(currentDayOfBirth, currentDayOfDeath);
		currentPerson.setTimeline(currentTimeline);
		return currentPerson;
	}

	public void populate_timeline(OrganicPerson currentPerson) {

		// Add events to timeline
		addEligibleToMarryEvent(currentPerson);

	}

	private void addEligibleToMarryEvent(OrganicPerson currentPerson) {
		// Add ELIGIBLE_TO_MARRY event
		int date;
		if (currentPerson.getSex() == 'M') {
			// time in days to birth from 1/1/1600 + marriage age in days
			date = DateManipulation.dateToDays(currentPerson.getBirthDate()) + maleAgeAtMarriageDistribution.getSample();
			currentPerson.getTimeline().addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
		} else {
			// time in days to birth from 1/1/1600 + marriage age in days
			date = DateManipulation.dateToDays(currentPerson.getBirthDate()) + femaleAgeAtMarriageDistribution.getSample();
			currentPerson.getTimeline().addEvent(date, new OrganicEvent(EventType.ELIGIBLE_TO_MARRY));
		}
		//		// If marriage date is before simulation start date then add to respective partnership queue
		//		//  Must be added to the partnership queue in the order that would be expected if simulation had occurred naturally.
		//		if (date <= DateManipulation.dateToDays(START_YEAR, 0, 0)) {
		//			if (currentPerson.getSex() == 'M') {
		//				if (firstMale) {
		//					maleInitialPartnershipOrderer.add(date);
		//					malePartnershipQueue.add(currentPerson);
		//					firstMale = false;
		//				} else {
		//					Iterator iter = maleInitialPartnershipOrderer.iterator();
		//					int count = 0;
		//					while (iter.hasNext()) {
		//						if (date < (Integer) iter.next()) {
		//							maleInitialPartnershipOrderer.add(count, date);
		//							malePartnershipQueue.add(count, currentPerson);
		//							break;
		//						}
		//						count++;
		//					}
		//				}
		//			} else if (currentPerson.getSex() == 'F') {
		//				if (firstFemale) {
		//					femaleInitialPartnershipOrderer.add(date);
		//					femalePartnershipQueue.add(currentPerson);
		//					firstFemale = false;
		//				} else {
		//					Iterator iter = femaleInitialPartnershipOrderer.iterator();
		//					int count = 0;
		//					while (iter.hasNext()) {
		//						if (date < (Integer) iter.next()) {
		//							femaleInitialPartnershipOrderer.add(count, date);
		//							femalePartnershipQueue.add(count, currentPerson);
		//							break;
		//						}
		//						count++;
		//					}
		//				}
		//			}
	}


	public void divorceSeedPeople() {

	}

	public void marryUpPeople() {
		Integer firstMaleId = (Integer) null;
		Integer firstFemaleId = (Integer) null;
		while (!malePartnershipQueue.isEmpty()) {
			if (firstMaleId == (Integer) null) {
				firstMaleId = malePartnershipQueue.getFirst().getId();
			} else if (malePartnershipQueue.getFirst().getId() == firstMaleId) {
				firstMaleId = (Integer) null;
				// reset female list
				if (!femalePartnershipQueue.isEmpty()) {
					if(firstFemaleId == (Integer) null)
						break;
					while (femalePartnershipQueue.getFirst().getId() != firstFemaleId) {
						femalePartnershipQueue.add(femalePartnershipQueue.removeFirst());
						femaleInitialPartnershipOrderer.add(femaleInitialPartnershipOrderer.removeFirst());
					}
				}
				firstFemaleId = (Integer) null;
				break;
			}
			while (!femalePartnershipQueue.isEmpty()) {
				if (firstFemaleId == null) {
					firstFemaleId = femalePartnershipQueue.getFirst().getId();
				} else if (femalePartnershipQueue.getFirst().getId() == firstFemaleId) {
					firstFemaleId = (Integer) null;
					malePartnershipQueue.add(malePartnershipQueue.removeFirst());
					maleInitialPartnershipOrderer.add(maleInitialPartnershipOrderer.removeFirst());
					break;
				}
				if (eligableToMarry(malePartnershipQueue.getFirst(), femalePartnershipQueue.getFirst())) {
					// Calculate marriage date
					// Finds first day both were eligible to marry
					int firstDay = maleInitialPartnershipOrderer.getFirst();
					if (femaleInitialPartnershipOrderer.getFirst() > firstDay)
						firstDay = femaleInitialPartnershipOrderer.getFirst();

					System.out.println("Partnership " + malePartnershipQueue.getFirst().getId() + " & " + femalePartnershipQueue.getFirst().getId());
					marry(malePartnershipQueue.getFirst(), femalePartnershipQueue.getFirst(), currentDay);
					
					int maleId = malePartnershipQueue.getFirst().getId();
					int femaleId = femalePartnershipQueue.getFirst().getId();

					// remove people from queues
					malePartnershipQueue.removeFirst();
					maleInitialPartnershipOrderer.removeFirst();
					femalePartnershipQueue.removeFirst();
					femaleInitialPartnershipOrderer.removeFirst();

					// Resets queues
					if (maleId != firstMaleId)
						while (malePartnershipQueue.getFirst().getId() != firstMaleId) {
							malePartnershipQueue.add(malePartnershipQueue.removeFirst());
							maleInitialPartnershipOrderer.add(maleInitialPartnershipOrderer.removeFirst());
						}

					if (femaleId != firstFemaleId)
						while (femalePartnershipQueue.getFirst().getId() != firstFemaleId) {
							femalePartnershipQueue.add(femalePartnershipQueue.removeFirst());
							femaleInitialPartnershipOrderer.add(femaleInitialPartnershipOrderer.removeFirst());
						}

					firstMaleId = (Integer) null;
					firstFemaleId = (Integer) null;
					break;
				} else {
					femalePartnershipQueue.add(femalePartnershipQueue.removeFirst());
					femaleInitialPartnershipOrderer.add(femaleInitialPartnershipOrderer.removeFirst());
				}
			}

		}
	}

	public boolean eligableToMarry(final OrganicPerson male, final OrganicPerson female) {
		return PopulationLogic.partnerAgeDifferenceIsReasonable(DateManipulation.dateToDays(male.getBirthDate()), DateManipulation.dateToDays(female.getBirthDate()));
	}

	public void marry(final OrganicPerson husband, final OrganicPerson wife, int days) {
		// Create partnership
		OrganicPartnership newPartnership = new OrganicPartnership(IDFactory.getNextID(), husband, wife, days);
		partnerships.add(newPartnership);
		OrganicPopulationLogger.logMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), days), DateManipulation.differenceInDays(wife.getBirthDay(), days));
		husband.addPartnership(newPartnership.getId());
		wife.addPartnership(newPartnership.getId());
	}

	public void mainIteration() {
		mainIteration(DEFAULT_STEP_SIZE);
	}

	public void mainIteration(final int timeStepSizeInDays) {

		while (currentDay < DateManipulation.dateToDays(END_YEAR, 1, 1)) {
			if(currentDay%365 == 0)
				System.out.println(1600 + currentDay/365);
			int previousDate = currentDay;
			currentDay += timeStepSizeInDays;

			//Checking all time-lines for new events

			//People
			for (int i = 0; i < people.size(); i++) {
				if(DateManipulation.differenceInDays(currentDay, DateManipulation.dateToDays(people.get(i).getBirthDate())) == 0) {
					System.out.println("Person " + people.get(i).getId() + " born");
					populate_timeline(people.get(i));
				}
				// TODO make more efficient
				if (people.get(i).getTimeline() != null) {

					//Check all dates between the previous and current date after taking the time step
					for (int j = previousDate; j < currentDay; j++) {
						EventType event;
						if (people.get(i).getTimeline().isDateAvailable(currentDay)) {
							event = people.get(i).getTimeline().getEvent(currentDay).getEventType();
							//deal with event
							switch (event) {
							case ELIGIBLE_TO_MARRY:
								if(people.get(i).getSex() == 'M') {
									malePartnershipQueue.add(people.get(i));
									maleInitialPartnershipOrderer.add(currentDay);
								}
								else {
									femalePartnershipQueue.add(people.get(i));
									femaleInitialPartnershipOrderer.add(currentDay);
								}
								break;
							case DEATH:

							default:
								break;
							}
						}


					}

				}
			}
			marryUpPeople();
			// Partnerships
			for (int i = 0; i < partnerships.size(); i++) {
				if (partnerships.get(i).getTimeline() != null) {

					// Check all dates between the previous and current date after taking the time step
					for (int j = previousDate; j < currentDay; j++) {
						EventType event;
						if (partnerships.get(i).getTimeline().isDateAvailable(currentDay)) {
							event = partnerships.get(i).getTimeline().getEvent(currentDay).getEventType();
							// deal with event
							switch (event) {
							case BIRTH:

								break;
							case DIVORCE:

								break;
							case PARTNERSHIP_ENDED_BY_DEATH:

							default:
								break;
							}
						}
					}

				}
			}

		}
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

			;
		};
	}

	@Override
	public Iterable<IPartnership> getPartnerships() {
		return new Iterable<IPartnership>() {
			@Override
			public Iterator<IPartnership> iterator() {

				final Iterator iterator = partnerships.iterator();

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

			;
		};
	}

	@Override
	public IPerson findPerson(final int id) {
		int index, binaryStep;
		for (binaryStep = 1; binaryStep < people.size(); binaryStep <<= 1) ;
		for (index = 0; binaryStep != 0; binaryStep >>= 1) {
			if (index + binaryStep < people.size() && people.get(index + binaryStep).getId() <= id) {
				index += binaryStep;
			}
		}
		if (people.get(index).getId() == id) {
			return people.get(index);
		}

		return null;
	}

	@Override
	public IPartnership findPartnership(final int id) {
		int index, binaryStep;
		for (binaryStep = 1; binaryStep < partnerships.size(); binaryStep <<= 1) ;
		for (index = 0; binaryStep != 0; binaryStep >>= 1) {
			if (index + binaryStep < partnerships.size() && partnerships.get(index + binaryStep).getId() <= id) {
				index += binaryStep;
			}
		}
		if (partnerships.get(index).getId() == id) {
			return partnerships.get(index);
		}

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
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setConsistentAcrossIterations(final boolean consistent_across_iterations) {

	}

	//Testing purposes
	public static void main(final String[] args) {
		System.out.println("--------MAIN HERE---------");
		OrganicPopulation op = new OrganicPopulation();
		op.makeSeed();
		op.currentDay = op.earliestDate;
		op.mainIteration();

		//        System.out.println("--------PEOPLE--------");
		//        for (int i = 0; i < op.getNumberOfPeople(); i++) {
		//        	System.out.println();
		//            System.out.println("BORN: " + op.people.get(i).getBirthDate());
		//            System.out.println("DIED: " + op.people.get(i).getDeathDate());
		//            int x = (DateManipulation.dateToDays(op.people.get(i).getDeathDate()) - DateManipulation.dateToDays(op.people.get(i).getBirthDate()))/365;
		//            System.out.println("ALIVE FOR: " + x);
		//            System.out.println();
		//        }
		//        
		//        System.out.println("--------PARTNERSHIPS--------");
		//        for(int i = 0; i < op.getNumberOfPartnerships(); i++) {
		//        	System.out.println();
		//        	System.out.println("Husband: " + op.partnerships.get(i).getMalePartnerId());
		//        	System.out.println("Wife: " + op.partnerships.get(i).getFemalePartnerId());
		//        	System.out.println("Date: " + op.partnerships.get(i).getMarriageDate());
		//        }

		OrganicPopulationLogger.printLogData();
		System.out.println("Female Marriage Queue Size: " + op.femalePartnershipQueue.size());
		System.out.println("Male Marriage Queue Size: " + op.malePartnershipQueue.size());

	}
}
