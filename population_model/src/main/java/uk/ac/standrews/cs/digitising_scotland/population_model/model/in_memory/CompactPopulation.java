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
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.SearchCondition;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;
import uk.ac.standrews.cs.nds.util.QuickSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Model of synthetic population.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public class CompactPopulation {

    // TODO provide a way to configure the parameters dynamically

    /**
     * The approximate average number of days per year.
     */
    public static final float DAYS_PER_YEAR = 365.25f;

    /**
     * The start year of the simulation.
     */
    public static final int START_YEAR = 1780;

    /**
     * The end year of the simulation.
     */
    public static final int END_YEAR = 2013;
//    public static final int END_YEAR = 1830;

    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;

    private static final int AGE_AT_FIRST_MARRIAGE_STD_DEV = 3;
    private static final int AGE_AT_FIRST_MARRIAGE_MEAN = 18;
    private static final int MARRIAGE_SEPARATION_MEAN = 7;
    private static final int MARRIAGE_SEPARATION_STD_DEV = 2;
    private static final int MAX_CHILDREN = 6;
    private static final int MAX_MARRIAGES = 3;

    private static final int[] NUMBER_OF_CHILDREN_DISTRIBUTION = new int[]{2, 3, 2, 1, 1, 1, 1};
    private static final int[] NUMBER_OF_MARRIAGES_DISTRIBUTION = new int[]{4, 20, 2, 1};

    private static final double PROBABILITY_OF_BEING_INCOMER = 0.125;

    private static final int NUMBER_OF_STAGES_IN_POPULATION_GENERATION = 5;

    private final int earliest_date;
    private final int latest_date;

    private Distribution<Integer> date_of_birth_distribution;
    private Distribution<Boolean> sex_distribution;
    private Distribution<Integer> age_at_death_distribution;
    private Distribution<Boolean> incomers_distribution;
    private WeightedIntegerDistribution number_of_children_distribution;
    private WeightedIntegerDistribution number_of_marriages_distribution;
    private NormalDistribution age_at_first_marriage_distribution;
    private NormalDistribution marriage_separation_distribution;
    private ProgressIndicator progress_indicator;

    private CompactPerson[] people;
    private int number_of_partnerships = 0;

    /**
     * Creates a synthetic population.
     *
     * @param population_size    the number of people in the population
     * @param earliest_date      the earliest date of any event (birth, death, marriage, parenthood), represented as days since {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#START_YEAR}
     * @param latest_date        the latest date of any event, represented as days since {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#START_YEAR}
     * @param progress_indicator a progress indicator
     */
    public CompactPopulation(final int population_size, final int earliest_date, final int latest_date, final ProgressIndicator progress_indicator) throws NegativeWeightException, NegativeDeviationException {

        this.earliest_date = earliest_date;
        this.latest_date = latest_date;
        this.progress_indicator = progress_indicator;

        Random random = RandomFactory.getRandom();

        date_of_birth_distribution = new UniformDistribution(this.earliest_date, this.latest_date, random);
        sex_distribution = new UniformSexDistribution(random);
        age_at_death_distribution = new AgeAtDeathDistribution(random);
        incomers_distribution = new IncomersDistribution(PROBABILITY_OF_BEING_INCOMER, random);

        number_of_children_distribution = new WeightedIntegerDistribution(0, MAX_CHILDREN, NUMBER_OF_CHILDREN_DISTRIBUTION, random);
        number_of_marriages_distribution = new WeightedIntegerDistribution(0, MAX_MARRIAGES, NUMBER_OF_MARRIAGES_DISTRIBUTION, random);
        age_at_first_marriage_distribution = new NormalDistribution(AGE_AT_FIRST_MARRIAGE_MEAN * DAYS_PER_YEAR, AGE_AT_FIRST_MARRIAGE_STD_DEV * DAYS_PER_YEAR, random);
        marriage_separation_distribution = new NormalDistribution(MARRIAGE_SEPARATION_MEAN * DAYS_PER_YEAR, MARRIAGE_SEPARATION_STD_DEV * DAYS_PER_YEAR, random);

        people = new CompactPerson[population_size];
        initialiseProgressIndicator();

        createPeople();
        createDeaths();
        createMarriages();
        createIncomers();
        linkChildren();
    }

    /**
     * Creates a synthetic population with default start and end dates.
     *
     * @param population_size    the number of people in the population
     * @param progress_indicator a progress indicator
     */
    public CompactPopulation(final int population_size, final ProgressIndicator progress_indicator) throws NegativeDeviationException, NegativeWeightException {

        this(population_size, DateManipulation.dateToDays(START_YEAR, 0, 1), DateManipulation.dateToDays(END_YEAR, DECEMBER_INDEX, DAYS_IN_DECEMBER), progress_indicator); // 1st January of start year to 31st December of end year.
    }

    /**
     * Creates a synthetic population with default start and end dates.
     *
     * @param population_size the number of people in the population
     */
    public CompactPopulation(final int population_size) throws NegativeDeviationException, NegativeWeightException {

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
    public int size() {

        return people.length;
    }

    /**
     * Gets the person at the specified position in the population.
     *
     * @param index the index
     * @return the person at that position in the population
     */
    public CompactPerson getPerson(final int index) {

        return people[index];
    }

    /**
     * Gets the person with the specified identifier.
     *
     * @param id the identifier
     * @return the person with that identifier, or null if none is found
     */
    public CompactPerson findPerson(int id) {

        // Use binary split since the array is sorted by id order.
        int low = 0;
        int high = people.length - 1;

        while (low <= high) {

            int mid = low + (high - low) / 2;

            CompactPerson mid_person = people[mid];

            if (id < mid_person.id) {
                high = mid - 1;

            } else if (id > mid_person.id) {
                low = mid + 1;

            } else {
                return mid_person;
            }
        }
        return null;
    }

    public CompactPartnership findPartnership(final int id) {

        for (CompactPerson person : people) {
            List<CompactPartnership> partnerships = person.getPartnerships();
            if (partnerships != null) {
                for (CompactPartnership partnership : partnerships) {
                    if (partnership.getId() == id) {
                        return partnership;
                    }
                }
            }
        }
        return null;
    }

    public int findPerson(final int start_index, final SearchCondition condition) {

        int population_size = people.length;
        for (int i = start_index + 1; i < population_size; i++) {

            switch (condition.check(i)) {

                case POSITIVE:
                    return i;
                case NEGATIVE_CONTINUE:
                    continue;
                case NEGATIVE_STOP:
                    return -1;
                default:
                    throw new RuntimeException("unexpected condition");
            }
        }
        return -1;
    }

    public boolean married(final int p1_index, final int p2_index) {

        final List<CompactPartnership> partnerships = people[p1_index].getPartnerships();
        if (partnerships != null) {
            for (final CompactPartnership partnership : partnerships) {
                if (partnership.getPartner(p1_index) == p2_index) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getNumberOfPeople() {
        return people.length;
    }

    public int getNumberOfPartnerships() {
        return number_of_partnerships;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    public CompactPerson[] getPeopleArray() {

        return people;
    }

    /**
     * Generates people with random birthdays drawn from distribution, then sorts into date of birth order.
     */
    private void createPeople() {

        int id = IDFactory.getNextID();

        for (int i = 0; i < people.length; i++) {
            people[i] = new CompactPerson(date_of_birth_distribution.getSample(), sex_distribution.getSample());
            progressStep();
        }

        sortPeopleByAge();

        // Set the person ids in order so binary split can be used to locate ids.
        for (int i = 0; i < people.length; i++) {
            people[i].id = id++;
        }
    }

    private void createDeaths() {

        for (final CompactPerson person : people) {

            final int age_at_death_in_days = age_at_death_distribution.getSample();
            final int date_of_death = person.birth_date + age_at_death_in_days;

            if (DateManipulation.daysToYear(date_of_death) <= END_YEAR) {
                person.death_date = date_of_death;
            }

            progressStep();
        }
    }

    private void createMarriages() {

        for (int i = 0; i < people.length; i++) {

            if (getPeopleArray()[i].getPartnerships() == null) { // Skip if marriages have already been set for this person.

                createMarriages(i, number_of_marriages_distribution.getSample());
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

    private void createMarriages(final int index, final int number_of_marriages) {

        final CompactPerson person = getPeopleArray()[index];
        int marriage_date = (int) (person.birth_date + age_at_first_marriage_distribution.getSample());

        int start_index = index + 1;

        for (int i = 0; i < number_of_marriages; i++) {

            final int spouse_index = createMarriage(index, start_index, marriage_date);
            if (spouse_index == -1) { // can't find a spouse
                break;
            }
            start_index = spouse_index + 1;
            marriage_date += marriage_separation_distribution.getSample();
        }
    }

    /**
     * @param first_partner_index the index of the first partner in the partnership
     * @param start_index         the index from which to search for a spouse
     * @param marriage_date       the encoded date of the marriage
     * @return the index of the spouse.
     */
    private int createMarriage(final int first_partner_index, final int start_index, final int marriage_date) {

        if (DateManipulation.daysToYear(marriage_date) <= END_YEAR) {
            final int partner_index = findPartner(first_partner_index, start_index, marriage_date);

            if (partner_index != -1) {
                new CompactPartnership(people[first_partner_index], first_partner_index, people[partner_index], partner_index, marriage_date);
                number_of_partnerships++;
            }

            return partner_index;
        }
        return -1;
    }

    private void linkChildren() {

        for (final CompactPerson person : people) {

            linkChildren(person);
            progressStep();
        }
    }

    private void linkChildren(final CompactPerson person) {

        final List<CompactPartnership> partnerships = person.getPartnerships();
        if (partnerships != null) {

            for (int i = 0; i < partnerships.size(); i++) {
                final int start_of_next_partnership = i < partnerships.size() - 1 ? partnerships.get(i + 1).getMarriageDate() : Integer.MAX_VALUE;
                linkChildren(partnerships.get(i), start_of_next_partnership);
            }
        }
    }

    private void linkChildren(final CompactPartnership partnership, final int start_of_next_partnership) {

        // Ignore if children have already been linked for this partnership.
        if (partnership.getChildren() == null) {
            final int number_of_children = number_of_children_distribution.getSample();
            linkChildren(partnership, number_of_children, start_of_next_partnership);
        }
    }

    private void linkChildren(final CompactPartnership partnership, final int number_of_children, final int latest_acceptable_birth_date) {

        final List<Integer> children = new ArrayList<>(number_of_children);
        partnership.setChildren(children);

        int previous_child_birth_date = 0;

        int start_index = Math.max(partnership.getPartner1(), partnership.getPartner2()) + 1; // the index at which we start to search for possible children.

        for (int i = 0; i < number_of_children; i++) {

            final int earliest_birth_date = PopulationLogic.earliestAcceptableBirthDate(partnership.getMarriageDate(), previous_child_birth_date);

            final int child_index = findSuitableChild(partnership, start_index, earliest_birth_date, latest_acceptable_birth_date);

            // May not be able to find suitable child if time has run out due to a subsequent marriage, or the whole simulation ending.
            if (child_index == -1) {
                break;
            }

            final CompactPerson child = people[child_index];

            children.add(child_index);
            previous_child_birth_date = child.birth_date;
            child.setHasParents();
            start_index = child_index + 1; // start looking beyond the last child already chosen.
        }
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

    private int findSuitableChild(final CompactPartnership partnership, final int start_search_index, final int earliest_birth_date, final int latest_birth_date) {

        final int husband_index = getHusbandIndex(partnership);
        final int wife_index = getWifeIndex(partnership);

        final SearchCondition conditions = new SearchCondition() {

            @Override
            public ConditionResult check(final int child_index) {

                final CompactPerson child = people[child_index];

                if (child.birth_date >= latest_birth_date) {
                    return ConditionResult.NEGATIVE_STOP;
                }

                final CompactPerson father = people[husband_index];
                final CompactPerson mother = people[wife_index];

                boolean not_incomer = !child.isIncomer();
                boolean not_born_too_early = child.birth_date > earliest_birth_date;
                boolean not_born_too_late = child.birth_date < latest_birth_date;
                boolean doesnt_have_parents = !child.hasParents();
                boolean parents_have_sensible_ages = PopulationLogic.parentsHaveSensibleAgesAtChildBirth(father.birth_date, father.death_date, mother.birth_date, mother.death_date, child.birth_date);
                boolean child_not_married_to_existing_child = !marriedToAnyChildrenOf(child_index, partnership);

                return not_incomer &&
                        not_born_too_early &&
                        not_born_too_late &&
                        doesnt_have_parents &&
                        parents_have_sensible_ages &&
                        child_not_married_to_existing_child ?

                        ConditionResult.POSITIVE : ConditionResult.NEGATIVE_CONTINUE;
            }
        };

        return findPerson(start_search_index, conditions);
    }

    private boolean marriedToAnyChildrenOf(final int person_index, final CompactPartnership partnership) {

        // Iterate over all partnerships of the people in the given partnership.
        for (final int partner : new Integer[]{partnership.getPartner1(), partnership.getPartner2()}) {
            for (final CompactPartnership partnership2 : people[partner].getPartnerships()) {

                final List<Integer> children = partnership2.getChildren();
                if (children != null) {
                    for (final int child : children) {
                        if (married(child, person_index)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Linearly search up people starting at @param index looking for a partner who is sufficiently close in age to the first person and who is either unmarried or not married recently.
     *
     * @return the index if a person is found and -1 otherwise.
     */
    private int findPartner(final int first_partner_index, final int start_index, final int marriage_date) {

        final SearchCondition partner_compatible = new SearchCondition() {

            @Override
            public ConditionResult check(final int person_index) {

                return partnersAreCompatible(first_partner_index, person_index, marriage_date) ? ConditionResult.POSITIVE : ConditionResult.NEGATIVE_CONTINUE;
            }
        };

        return findPerson(start_index, partner_compatible);
    }

    private boolean partnersAreCompatible(final int partner_index, final int candidate_partner_index, final int marriage_date) {

        final CompactPerson person = people[partner_index];
        final CompactPerson candidate_partner = people[candidate_partner_index];

        boolean oppositeSex = CompactPerson.oppositeSex(person, candidate_partner);
        boolean ageDifferenceIsReasonable = PopulationLogic.partnerAgeDifferenceIsReasonable(person.birth_date, candidate_partner.birth_date);
        boolean notPreviouslyPartners = notPreviouslyPartners(person, partner_index, candidate_partner);
        boolean notTooManyPartnerships = notTooManyPartnerships(candidate_partner);
        boolean notTooRecentPartnership = notTooRecentPartnership(candidate_partner, marriage_date);

        return oppositeSex &&
                ageDifferenceIsReasonable &&
                notPreviouslyPartners &&
                notTooManyPartnerships &&
                notTooRecentPartnership;
    }

    private boolean notPreviouslyPartners(final CompactPerson person, final int index, final CompactPerson candidate_partner) {

        if (person.getPartnerships() == null || candidate_partner.getPartnerships() == null) {
            return true;
        }

        for (final CompactPartnership partnership : person.getPartnerships()) {
            if (people[partnership.getPartner(index)] == candidate_partner) {
                return false;
            }
        }

        return true;
    }

    private boolean notTooManyPartnerships(final CompactPerson candidate_partner) {

        return candidate_partner.getPartnerships() == null || candidate_partner.getPartnerships().size() < MAX_MARRIAGES;
    }

    private boolean notTooRecentPartnership(final CompactPerson candidate_partner, final int marriage_date) {

        CompactPartnership recent_partnership = candidate_partner.mostRecentPartnership();
        if (recent_partnership == null) {
            return true;
        }

        int most_recent_marriage_date = recent_partnership.getMarriageDate();
        return PopulationLogic.longEnoughBetweenMarriages(marriage_date, most_recent_marriage_date);
    }

    private void sortPeopleByAge() {

        final List<CompactPerson> people_list = Arrays.asList(getPeopleArray());
        final QuickSort<CompactPerson> sorter = new QuickSort<>(people_list, new Comparator<CompactPerson>() {

            @Override
            public int compare(final CompactPerson p1, final CompactPerson p2) {

                return p1.birth_date - p2.birth_date;
            }
        });
        sorter.sort();
    }

    private void setNumberOfPartnerships() {

        Set<CompactPartnership> partnerships = new HashSet<>();
        for (CompactPerson person : people) {
            List<CompactPartnership> partnerships1 = person.getPartnerships();
            if (partnerships1 != null) {
                for (CompactPartnership partnership : partnerships1) {
                    partnerships.add(partnership);
                }
            }
        }
        number_of_partnerships = partnerships.size();
    }

    /**
     * Gets the index of the male member of this partnership.
     *
     * @return the male member of this partnership.
     * * Assumes that the two partners are of different sexes.
     */
    private int getHusbandIndex(final CompactPartnership partnership) {

        int partner1_index = partnership.getPartner1();
        return people[partner1_index].isMale() ? partner1_index : partnership.getPartner2();
    }

    /**
     * Gets the index of female member of this partnership.
     *
     * @return the female member of this partnership.
     * Assumes that the two partners are of different sexes.
     */
    private int getWifeIndex(final CompactPartnership partnership) {

        int partner1_index = partnership.getPartner1();
        return !people[partner1_index].isMale() ? partner1_index : partnership.getPartner2();
    }
}
