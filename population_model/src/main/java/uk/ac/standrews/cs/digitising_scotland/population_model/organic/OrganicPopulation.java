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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.LoggingControl;
import uk.ac.standrews.cs.digitising_scotland.population_model.tools.MemoryMonitor;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * The OrganicPopulation class models and handles the population as a whole.
 * 
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPopulation implements IPopulation {

    /**
     * By running the class the population model is run, the arguments allow for the size of the seed population to be set.
     * 
     * @param args The size of the seed population.
     */
    public static void main(final String[] args) {
        System.out.println("--------MAIN HERE---------");

        if (args.length == 0) {
            runPopulationModel(DEFAULT_SEED_SIZE, true, true, true);
        } else if (args.length == 1) {
            runPopulationModel(Integer.valueOf(args[0]), true, true, true);
        }
    }

    /*
     * -------------------------------- Logging and output --------------------------------
     */
    
    private static MemoryMonitor mm;
    private static PrintWriter writer = null;
    private static boolean logging = true;

    /*
     * -------------------------------- Universal population variables --------------------------------
     */
    
    private static PriorityQueue<OrganicEvent> globalEventsQueue = new PriorityQueue<OrganicEvent>();
    
    private String description;
    private static final int DEFAULT_SEED_SIZE = 10000;
    private static final float DAYS_PER_YEAR = 365.25f;
    private static final int START_YEAR = 1780;
    private static final int END_YEAR = 2013 ;
    private static final int EPOCH_YEAR = 1600;
    private static Random random = RandomFactory.getRandom();
    private int earliestDate = DateManipulation.dateToDays(getStartYear(), 0, 0);
    private static int currentDay;
    private boolean seedGeneration = true;
    private int maximumNumberOfChildrenInFamily;
    
    /*
     * -------------------------------- People and partnership lists --------------------------------
     */
    private static List<OrganicPerson> livingPeople = new ArrayList<OrganicPerson>();
    
    private static List<OrganicPartnership> partnerships = new ArrayList<OrganicPartnership>();
    
    /**
     * Adds given person to the list of people.
     * @param person The given person.
     */
    public static void addLivingPerson(OrganicPerson person) {
        livingPeople.add(person);
    }
    
    /**
     * Adds given partnership to the list of partnerships.
     * @param person The given partnership.
     */
    public static void addPartnership(OrganicPartnership partnership) {
        partnerships.add(partnership);
    }

    /*
     * Constructors
     */

    /**
     * Constructs a new OrganicPopulation.
     * 
     * @param description The population descriptor string.
     */
    public OrganicPopulation(final String description) {
        this.description = description;
    }

    /*
     * High level methods
     */

    /**
     * Calls the makeSeed method with the default specified seed size.
     */
    public void makeSeed() {
        makeSeed(getDefaultSeedSize());
    }

    /**
     * Creates a seed population of the specified size.
     * @param size The number of individuals to be created in the seed population.
     */
    public void makeSeed(final int size) {
        for (int i = 0; i < size; i++) {
            OrganicPerson person = new OrganicPerson(IDFactory.getNextID(), 0, -1, this, seedGeneration, null);
            livingPeople.add(person);
        }
        this.seedGeneration = false;
    }

    /**
     * Called to begin the event iteration which commences the simulation.
     * 
     * @param print Specifies whether to print year end information to console.
     */
    public void newEventIteration(final boolean print, final boolean memoryMonitor) {
        while (getCurrentDay() <= DateManipulation.dateToDays(getEndYear(), 0, 0)) {
            OrganicEvent event = globalEventsQueue.poll();

            if (event == null) {
                break;
            } else {
                // While the next event isn't in the same year as the next event.
                while ((int) (getCurrentDay() / getDaysPerYear()) != (int) (event.getDay() / getDaysPerYear())) {
                    // Log population as if the years end - as we know no more events will occur before the year is out and thus the population is the same now as at the year end
                    if (logging) {
                        LoggingControl.yearEndLog(currentDay);
                    }
                    if (print && logging) {
                        writer.println(EPOCH_YEAR + 1 + (int) (getCurrentDay() / getDaysPerYear()));
                        writer.println("Population: " + LoggingControl.getPopulationLogger().getCount());
                        writer.flush();
                    }
                    if (memoryMonitor && logging) {
                        mm.log(currentDay, LoggingControl.getPopulationLogger().getCount(), livingPeople.size());
                    }
                    // Moves date to year end - can't move direct to next event date as there may be another year or more before that and thus that year end needs to be logged also.
                    double r = getCurrentDay() % DAYS_PER_YEAR;
                    setCurrentDay((int) (getCurrentDay() + Math.round(r)));
                }

                setCurrentDay(event.getDay());
                if (currentDay % 10.0f == 0) {
                    event.movePeopleFromAffarirsWaitingQueueToAppropriateQueue();
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.MALE_SINGLE_AFFAIR);
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.FEMALE_SINGLE_AFFAIR);
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.MALE_MARITAL_AFFAIR);
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION);
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.COHABITATION_THEN_MARRIAGE);
                    event.partnerTogetherPeopleInPartnershipQueue(FamilyType.MARRIAGE);
                }
                
                event.run();
            }

        }
    }

    /**
     * Adds event to global events queue.
     * 
     * @param event The event to be added to the global events queue.
     */
    public static void addEventToGlobalQueue(final OrganicEvent event) {
        globalEventsQueue.add(event);
    }

    /*
     * Getters and setters
     */

    /**
     * Returns the epoch year.
     * 
     * @return The epoch year.
     */
    public static int getEpochYear() {
        return EPOCH_YEAR;
    }

    /**
     * Size of initially generated seed population.
     * 
     * @return the defaultSeedSize
     */
    public static int getDefaultSeedSize() {
        return DEFAULT_SEED_SIZE;
    }

    /**
     * The approximate average number of days per year.
     * 
     * @return the daysPerYear
     */
    public static float getDaysPerYear() {
        return DAYS_PER_YEAR;
    }

    /**
     * The start year of the simulation.
     * 
     * @return the startYear
     */
    public static int getStartYear() {
        return START_YEAR;
    }

    /**
     * The end year of the simulation.
     * 
     * @return the endYear
     */
    public static int getEndYear() {
        return END_YEAR;
    }

    /**
     * Returns the earliestDate in days since the 1/1/1600.
     * 
     * @return The earliestDate in days since the 1/1/1600.
     */
    public int getEarliestDate() {
        return earliestDate;
    }

    /**
     * Sets the earliest date field to the specified date.
     * 
     * @param earlyDate The value which earliestDate is to be set to.
     */
    public void setEarliestDate(final int earlyDate) {
        earliestDate = earlyDate;
    }

    /**
     * Returns the population description string.
     * 
     * @return The population description string.
     */
    public String getDescription() {
        return description;
    }

    /*
     * Interface methods
     */

    @Override
    public Iterable<IPerson> getPeople() {
        return new Iterable<IPerson>() {
            @Override
            public Iterator<IPerson> iterator() {


                final Iterator<OrganicPerson> iterator = livingPeople.iterator();

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

        };
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return new Iterable<IPartnership>() {
            @Override
            public Iterator<IPartnership> iterator() {
                final Iterator<OrganicPartnership> iterator = partnerships.iterator();

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
        };
    }

    public static OrganicPerson findOrganicPerson(final int id) {

        for (OrganicPerson person : livingPeople) {
            if (person.getId() == id) {
                return (OrganicPerson) person;
            }
        }
        return null;
    }

    @Override
    public IPerson findPerson(final int id) {

        for (OrganicPerson person : livingPeople) {
            if (person.getId() == id) {
                return person;

            }
        }
        return null;
    }

    /**
     * Returns partnership with given id. Works by calling findPartnership but then casts to an OrganicPartenrship.
     * 
     * @param id The id of the partnership.
     * @return The partnership.
     */
    public static OrganicPartnership findOrganicPartnership(final int id) {

        final int index = ArrayManipulation.binarySplit(partnerships, new ArrayManipulation.SplitComparator<OrganicPartnership>() {

            @Override
            public int check(final OrganicPartnership partnership) {
                return id - partnership.getId();
            }
        });

        return index >= 0 ? partnerships.get(index) : null;

    }

    @Override
    public IPartnership findPartnership(final int id) {

        final int index = ArrayManipulation.binarySplit(partnerships, new ArrayManipulation.SplitComparator<OrganicPartnership>() {

            @Override
            public int check(final OrganicPartnership partnership) {
                return id - partnership.getId();
            }
        });

        return index >= 0 ? partnerships.get(index) : null;
    }

    @Override
    public int getNumberOfPeople() {
        return livingPeople.size();
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

    /**
     * By calling the run population model method, the simulation is run using the given parameters and output to various files, these being:
     * Output file: output_{time in ns}.txt in the directory src/main/resources/output/
     * Memory log file: memory_usage{time in ns}.dat in directory src/main/resources/output/
     * GnuPlot script file: log_output_script.p in directory src/main/resources/output/gnu/
     * GnuPlot dat files: in directory src/main/resources/output/gnu/
     * 
     * @param seedSize The size of the initial seed population.
     * @param print If the program should print results to file.
     * @param memoryMonitor If the program should record memory footprint.
     * @param logging If the program should log population statistics - note: Must be enabled to use either print or memory monitoring options.
     * @return The created organic population.
     */
    public static OrganicPopulation runPopulationModel(int seedSize, boolean print, final boolean memoryMonitor, final boolean logging) {
        OrganicPopulation.logging = logging;
        if (memoryMonitor) {
            mm = new MemoryMonitor();
        }

        long startTime = System.nanoTime();
        OrganicPopulation op = new OrganicPopulation("Test Population");
        OrganicPartnership.setupTemporalDistributionsInOrganicPartnershipClass(op);
        OrganicPerson.initializeDistributions(op);
        AffairWaitingQueueMember.initialiseAffairWithMarrieadOrSingleDistribution(op, "affair_with_single_or_married_distributions_data_filename", random);

        if (logging) {
            LoggingControl.setUpLogger();
        }
        
        if (livingPeople.size() == 0) {
            op.makeSeed(seedSize);
            op.setCurrentDay(op.getEarliestDate() - 1);
        }

        if (print) {
            try {
                writer = new PrintWriter("src/main/resources/output/output_" + System.nanoTime() + ".txt", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                System.err.println("Output file could not be created. Model will now terminate.");
                System.exit(1);
            }

            writer.println(op.getDescription());
        }
        op.newEventIteration(print, memoryMonitor);
        long timeTaken = System.nanoTime() - startTime;
        System.out.print(livingPeople.size() + "    ");
        System.out.println(timeTaken / 1000000);

        if (print) {
            writer.println();

            writer.println("Run time " + timeTaken / 1000000 + "ms");
            writer.println();
            if (logging) {
                LoggingControl.createGnuPlotOutputFilesAndScript();
            }
            writer.close();
        }
        if (memoryMonitor) {
            mm.close();
        }
        return op;
    }

    /**
     * Returns current day of simulation.
     * 
     * @return the currentDay
     */
    public static int getCurrentDay() {
        return currentDay;
    }

    /**
     * Sets current day for simulation.
     * 
     * @param currentDay The currentDay to set
     */
    public static void setCurrentDay(final int currentDay) {
        OrganicPopulation.currentDay = currentDay;
    }

    /**
     * returns the maximum number of children in a family.
     * 
     * @return the maximumNumberOfChildrenInFamily
     */
    public int getMaximumNumberOfChildrenInFamily() {
        return maximumNumberOfChildrenInFamily;
    }

    /**
     * Sets the maximum number of children in a family.
     * 
     * @param maximumNumberOfChildrenInFamily The maximumNumberOfChildrenInFamily to set
     */
    public void setMaximumNumberOfChildrenInFamily(final int maxNumberOfChildrenInFamily) {
        maximumNumberOfChildrenInFamily = maxNumberOfChildrenInFamily;
    }
    
    /**
     * Checks logging flag.
     * @return Logging flag value.
     */
    public static boolean isLogging() {
        return logging;
    }

    /**
     * Sets logging flag value.
     * @param logging The boolean value to be set to.
     */
    public static void setLogging(boolean logging) {
        OrganicPopulation.logging = logging;
    }

}
