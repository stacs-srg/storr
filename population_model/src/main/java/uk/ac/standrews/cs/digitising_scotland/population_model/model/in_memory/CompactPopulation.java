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
package uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AgeAtDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.IncomersDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;
import uk.ac.standrews.cs.nds.util.QuickSort;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Model of synthetic population.
 *
 * This class is not thread-safe.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
@NotThreadSafe
class CompactPopulation {

    // TODO provide a way to configure the parameters dynamically

    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;
    private static final double PROBABILITY_OF_BEING_INCOMER = 0.125;

    private static final int NUMBER_OF_STAGES_IN_POPULATION_GENERATION = 5;

    private final int earliest_date;
    private final int latest_date;

    private Distribution<Integer> date_of_birth_distribution;
    private Distribution<Boolean> sex_distribution;
    private Distribution<Integer> age_at_death_distribution;
    private Distribution<Boolean> incomers_distribution;

    private ProgressIndicator progress_indicator;

    private final CompactPerson[] people;
    private List<CompactPerson> people_as_list;
    private int number_of_partnerships = 0;

    /**
     * Creates a synthetic population.
     *
     * @param population_size    the number of people in the population
     * @param earliest_date      the earliest date of any event (birth, death, marriage, parenthood), represented as days since {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#START_YEAR}
     * @param latest_date        the latest date of any event, represented as days since {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#START_YEAR}
     * @param progress_indicator a progress indicator
     * @throws NegativeWeightException    if one of the underlying distributions cannot be initialised
     * @throws NegativeDeviationException if one of the underlying distributions cannot be initialised
     */
    protected CompactPopulation(final int population_size, final int earliest_date, final int latest_date, final ProgressIndicator progress_indicator) throws NegativeWeightException, NegativeDeviationException {

        this.earliest_date = earliest_date;
        this.latest_date = latest_date;
        this.progress_indicator = progress_indicator;

        people = new CompactPerson[population_size];

        initialiseDistributions();
        initialiseProgressIndicator();

        createPeople();
        createDeaths();
        createPartnerships();
        createIncomers();
        createChildren();
    }

    /**
     * Creates a synthetic population with default start and end dates.
     *
     * @param population_size    the number of people in the population
     * @param progress_indicator a progress indicator
     * @throws NegativeWeightException    if one of the underlying distributions cannot be initialised
     * @throws NegativeDeviationException if one of the underlying distributions cannot be initialised
     */
    protected CompactPopulation(final int population_size, final ProgressIndicator progress_indicator) throws NegativeDeviationException, NegativeWeightException {

        this(population_size, DateManipulation.dateToDays(PopulationLogic.START_YEAR, 0, 1), DateManipulation.dateToDays(PopulationLogic.END_YEAR, DECEMBER_INDEX, DAYS_IN_DECEMBER), progress_indicator); // 1st January of start year to 31st December of end year.
    }

    /**
     * Creates a synthetic population with default start and end dates.
     *
     * @param population_size the number of people in the population
     * @throws NegativeWeightException    if one of the underlying distributions cannot be initialised
     * @throws NegativeDeviationException if one of the underlying distributions cannot be initialised
     */
    protected CompactPopulation(final int population_size) throws NegativeDeviationException, NegativeWeightException {

        this(population_size, null);
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    protected CompactPopulation(final CompactPerson[] people, final int earliest_date, final int latest_date) {

        this.people = people;
        this.earliest_date = earliest_date;
        this.latest_date = latest_date;

        setNumberOfPartnerships();
    }

    /**
     * Gets the number of people in the population.
     *
     * @return the number of people in the population
     */
    protected int size() {

        return people.length;
    }

    /**
     * Gets the person at the specified position in the population.
     *
     * @param index the index
     * @return the person at that position in the population
     */
    protected CompactPerson getPerson(final int index) {

        return people[index];
    }

    /**
     * Finds the person with the specified id, or null if it cannot be found.
     *
     * @param id the id
     * @return the corresponding person
     */
    protected synchronized CompactPerson findPerson(final int id) {

        // Avoid initialising list until it's actually needed.
        if (people_as_list == null) {
            people_as_list = Arrays.asList(people);
        }

        final int index = ArrayManipulation.binarySplit(people_as_list, new ArrayManipulation.SplitComparator<CompactPerson>() {

            @Override
            public int check(final CompactPerson element) {
                return id - element.getId();
            }
        });

        return index >= 0 ? people[index] : null;
    }

    /**
     * Finds the partnership with the specified id, or null if it cannot be found.
     *
     * @param id the id
     * @return the corresponding partnership
     */
    protected CompactPartnership findPartnership(final int id) {

        for (final CompactPerson person : people) {
            final List<CompactPartnership> partnerships = person.getPartnerships();
            if (partnerships != null) {
                for (final CompactPartnership partnership : partnerships) {
                    if (partnership.getId() == id) {
                        return partnership;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns the number of people in the population.
     *
     * @return the number of people in the population
     */
    protected int getNumberOfPeople() {
        return people.length;
    }

    /**
     * Returns the number of partnerships in the population.
     *
     * @return the number of partnerships in the population
     */
    protected int getNumberOfPartnerships() {
        return number_of_partnerships;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    protected CompactPerson[] getPeopleArray() {

        return people;
    }

    private void initialiseDistributions() {

        final Random random = RandomFactory.getRandom();

        date_of_birth_distribution = new UniformIntegerDistribution(earliest_date, latest_date, random);
        sex_distribution = new UniformSexDistribution(random);
        age_at_death_distribution = new AgeAtDeathDistribution(random);
        incomers_distribution = new IncomersDistribution(PROBABILITY_OF_BEING_INCOMER, random);
    }

    /**
     * Generates people with random birthdays drawn from distribution, then sorts into date of birth order.
     */
    private void createPeople() {

        for (int i = 0; i < people.length; i++) {
            people[i] = new CompactPerson(date_of_birth_distribution.getSample(), sex_distribution.getSample());
            progressStep();
        }

        sortPeopleByAge();

        // Set the person ids in order so binary split can be used to locate ids.
        int id = IDFactory.getNextID();
        for (final CompactPerson person : people) {
            person.id = id++;
        }
    }

    private void createDeaths() {

        for (final CompactPerson person : people) {

            final int age_at_death_in_days = age_at_death_distribution.getSample();
            final int date_of_death = person.birth_date + age_at_death_in_days;

            if (DateManipulation.daysToYear(date_of_death) <= PopulationLogic.END_YEAR) {
                person.death_date = date_of_death;
            }

            progressStep();
        }
    }

    private void createIncomers() {

        for (final CompactPerson p : people) {

            if (incomers_distribution.getSample()) {
                p.setIsIncomer();
            }

            progressStep();
        }
    }

    private void createPartnerships() throws NegativeWeightException, NegativeDeviationException {

        number_of_partnerships = new CompactPopulationPartnerLinker(people, progress_indicator).linkPartners();
    }

    private void createChildren() throws NegativeWeightException {

        new CompactPopulationChildLinker(people, progress_indicator).linkChildren();
    }

    private void initialiseProgressIndicator() {

        if (progress_indicator != null) {
            progress_indicator.setTotalSteps(people.length * NUMBER_OF_STAGES_IN_POPULATION_GENERATION);
        }
    }

    private void progressStep() {

        if (progress_indicator != null) {
            progress_indicator.progressStep();
        }
    }

    private void sortPeopleByAge() {

        final List<CompactPerson> people_list = Arrays.asList(people);
        final QuickSort<CompactPerson> sorter = new QuickSort<>(people_list, new Comparator<CompactPerson>() {

            @Override
            public int compare(final CompactPerson p1, final CompactPerson p2) {

                if (p1.birth_date < p2.birth_date) {
                    return -1;
                }
                if (p1.birth_date > p2.birth_date) {
                    return 1;
                }
                return 0;
            }
        });
        sorter.sort();
    }

    private void setNumberOfPartnerships() {

        final Set<CompactPartnership> partnership_set = new HashSet<>();
        for (final CompactPerson person : people) {
            final List<CompactPartnership> partnerships = person.getPartnerships();
            if (partnerships != null) {
                for (final CompactPartnership partnership : partnerships) {
                    partnership_set.add(partnership);
                }
            }
        }
        number_of_partnerships = partnership_set.size();
    }
}
