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
	
    private static int earliestDate = DateManipulation.dateToDays(START_YEAR, 0, 0);
    
	private static int currentDay;

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
	
	static boolean seedGeneration = true;

	Random random = RandomFactory.getRandom();
	private Distribution<Integer> age_at_death_distribution = new AgeAtDeathDistribution(random);

	private List<OrganicPerson> people = new ArrayList<OrganicPerson>();
	private List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();

	private LinkedList<OrganicPerson> malePartnershipQueue = new LinkedList<OrganicPerson>();
	private LinkedList<OrganicPerson> femalePartnershipQueue = new LinkedList<OrganicPerson>();

	private LinkedList<Integer> maleInitialPartnershipOrderer = new LinkedList<Integer>();
	private LinkedList<Integer> femaleInitialPartnershipOrderer = new LinkedList<Integer>();

	public void makeSeed(final int size) {

		for (int i = 0; i < size; i++) {
			OrganicPerson person = new OrganicPerson(IDFactory.getNextID(), 0);
			people.add(person);
		}
		seedGeneration = false;
	}

	public void makeSeed() {
		makeSeed(DEFAULT_SEED_SIZE);
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

//					System.out.println("Partnership " + malePartnershipQueue.getFirst().getId() + " & " + femalePartnershipQueue.getFirst().getId());
					
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
		Object[] partnershipObjects = OrganicPartnership.createOrganicPartnership(IDFactory.getNextID(), husband, wife, days);
		partnerships.add((OrganicPartnership)partnershipObjects[0]);
		if(partnershipObjects[1] != null)
			people.add((OrganicPerson)partnershipObjects[1]);
		OrganicPopulationLogger.logMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), days), DateManipulation.differenceInDays(wife.getBirthDay(), days));
		husband.addPartnership(((OrganicPartnership)partnershipObjects[0]).getId());
		wife.addPartnership(((OrganicPartnership)partnershipObjects[0]).getId());
	}

	public void mainIteration() {
		mainIteration(DEFAULT_STEP_SIZE);
	}

	public void mainIteration(final int timeStepSizeInDays) {
		
		while (currentDay < DateManipulation.dateToDays(END_YEAR, 0, 0)) {
			if(currentDay%365 == 0) {
				System.out.println(1600 + currentDay/365);
				System.out.println("Population: " + OrganicPopulationLogger.getPopulation());
			}
			int previousDate = currentDay;
			currentDay += timeStepSizeInDays;

			//Checking all time-lines for new events

			//People
			for (int i = 0; i < people.size(); i++) {
				if(DateManipulation.differenceInDays(currentDay, DateManipulation.dateToDays(people.get(i).getBirthDate())) == 0) {
//					System.out.println("Person " + people.get(i).getId() + " born");
					OrganicPopulationLogger.incPopulation();
					OrganicPopulationLogger.incBirths();
					people.get(i).populate_timeline();
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
							case DEATH: // Everyone ends up here eventually
								removePersonFromSystem(people.get(i));
								OrganicPopulationLogger.decPopulation();
								break;
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
	
	// FIXME This isn't achieving what I want it to - some people are still in the marriage queues after death
	private void removePersonFromSystem(OrganicPerson person) {
//		if(person.getPartnerships().size() == 0) {
//			System.out.println("Check me");
//		}
		if(person.getSex() == 'M') {
			int index = -1;
			for(int i = 0; i < malePartnershipQueue.size(); i++) {
				if(person.getId() == malePartnershipQueue.get(i).getId())
					index = i;
			}
//			int index = malePartnershipQueue.indexOf(person);
			if(index != -1) {
				malePartnershipQueue.remove(index);
				maleInitialPartnershipOrderer.remove(index);
			}
		} else {
			int index = -1;
			for(int i = 0; i < femalePartnershipQueue.size(); i++) {
				if(person.getId() == femalePartnershipQueue.get(i).getId())
					index = i;
			}
//			int index = femalePartnershipQueue.indexOf(person);
			if(index != -1) {
				femalePartnershipQueue.remove(index);
				femaleInitialPartnershipOrderer.remove(index);
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
		op.currentDay = op.getEarliestDate() - 1;
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

	public static int getEarliestDate() {
		return earliestDate;
	}

	public static void setEarliestDate(int earlyDate) {
		earliestDate = earlyDate;
	}
	
	public static int getCurrentDay() {
		return currentDay;
	}
}
