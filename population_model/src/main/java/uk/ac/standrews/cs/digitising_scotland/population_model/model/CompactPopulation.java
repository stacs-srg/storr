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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.AgeAtDeathDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.IncomersDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.UniformSexDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;
import uk.ac.standrews.cs.nds.util.QuickSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Model of synthetic population.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public class CompactPopulation {

    // TODO define a general interface to be implemented by this, the db and a GEDCOM reader.

    // TODO provide a way to configure the parameters dynamically
    // TODO consider a different implementation growing from a seed population.

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
    public static final int NUMBER_OF_STAGES_IN_POPULATION_GENERATION = 5;

    private static final int DAYS_IN_DECEMBER = 31;
    private static final int DECEMBER_INDEX = 11;

    private static final int AGE_AT_FIRST_MARRIAGE_STD_DEV = 3;
    private static final int AGE_AT_FIRST_MARRIAGE_MEAN = 18;
    private static final int MARRIAGE_SEPARATION_MEAN = 7;
    private static final int MARRIAGE_SEPARATION_STD_DEV = 2;
    private static final int INTER_CHILD_INTERVAL = 3;
    private static final int TIME_BEFORE_FIRST_CHILD = 1;
    private static final int MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS = 7;
    private static final int PARTNERSHIP_AGE_DIFFERENCE_LIMIT = 5;
    private static final int MAX_CHILDREN = 6;
    private static final int MAX_MARRIAGES = 3;

    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 50;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_FATHER_AGE_AT_CHILDBIRTH = 70;

    private static final int[] NUMBER_OF_CHILDREN_DISTRIBUTION = new int[]{2, 3, 2, 1, 1, 1, 1};
    private static final int[] NUMBER_OF_MARRIAGES_DISTRIBUTION = new int[]{4, 20, 2, 1};

    private static final double PROBABILITY_OF_BEING_INCOMER = 0.125;

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

    // TODO make into enum
    public interface Condition {
        int POSITIVE = 1;
        int NEGATIVE_STOP = 2;
        int NEGATIVE_CONTINUE = 3;

        int check(int index);
    }

    /**
     * Creates a synthetic population.
     *
     * @param population_size the number of people in the population
     * @param earliest_date   the earliest date of any event (birth, death, marriage, parenthood), coded DateConversion format - using e.g. DateConversion.dateToDays(START_YEAR, 0, 1)
     * @param latest_date     the latest date of any event, coded DateConversion format
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
     * @param population_size the number of people in the population
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

    public CompactPopulation(CompactPerson[] people, final int earliest_date, final int latest_date) {

        this.people = people;
        this.earliest_date = earliest_date;
        this.latest_date = latest_date;
    }

    /**
     * Get the size of the population.
     *
     * @return the size.
     */
    public int size() {

        return people.length;
    }

    /**
     * Get the number of males in the population.
     *
     * @return the number of males.
     */
    public int numberMales() {

        int count = 0;

        for (final CompactPerson p : people) {
            if (p.isMale()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the number of males in the population.
     *
     * @return the number of females.
     */
    public int numberFemales() {

        int count = 0;

        for (final CompactPerson p : people) {
            if (!p.isMale()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the first date of the population.
     *
     * @return the first date.
     */
    public int getFirstDate() {

        return earliest_date;
    }

    /**
     * Get the last date of the population.
     *
     * @return the last date.
     */
    public int getLastDate() {

        return latest_date;
    }

    public CompactPerson getPerson(final int index) {

        return people[index];
    }

    public CompactPerson findPerson(final int id) {

        // TODO use binary split since ids are in ascending order in array

        int index = findPerson(-1, new Condition() {
            @Override
            public int check(int index) {
                return people[index].getId() == id ? Condition.POSITIVE : Condition.NEGATIVE_CONTINUE;
            }
        });

        return index > -1 ? people[index] : null;
    }

    public CompactPartnership findPartnership(final int id) {

        int population_size = people.length;
        for (CompactPerson person : people) {
            for (CompactPartnership partnership : person.getPartnerships()) {
                if (partnership.getId() == id) return partnership;
            }
        }
        return null;
    }

    public int findPersonIndex(final CompactPerson person) {

        final Condition match = new Condition() {

            @Override
            public int check(final int person_index) {
                return (person == people[person_index]) ? Condition.POSITIVE : Condition.NEGATIVE_CONTINUE;
            }
        };

        return findPerson(0, match);
    }

    public int findPerson(final int start_index, final Condition condition) {

        int population_size = people.length;
        for (int i = start_index + 1; i < population_size; i++) {

            switch (condition.check(i)) {

                case Condition.POSITIVE:
                    return i;
                case Condition.NEGATIVE_STOP:
                    return -1;
                case Condition.NEGATIVE_CONTINUE: {
                    continue;
                }
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

    /**
     * Gets the index of the male member of this partnership.
     *
     * @return the male member of this partnership.
     * * Assumes that the two partners are of different sexes.
     */
    public int husband(final CompactPartnership partnership) {

        return people[partnership.partner1].isMale() ? partnership.partner1 : partnership.partner2;
    }

    /**
     * Gets the index of female member of this partnership.
     *
     * @return the female member of this partnership.
     * Assumes that the two partners are of different sexes.
     */
    public int wife(final CompactPartnership partnership) {

        return !people[partnership.partner1].isMale() ? partnership.partner1 : partnership.partner2;
    }

    public boolean parentsHaveSensibleAgesAtChildBirth(final CompactPartnership partnership, final int child_index) {

        return parentsHaveSensibleAgesAtChildBirth(husband(partnership), wife(partnership), child_index);
    }

    public boolean parentsHaveSensibleAgesAtChildBirth(final int father_index, final int mother_index, final int child_index) {

        final CompactPerson father = people[father_index];
        final CompactPerson mother = people[mother_index];
        final CompactPerson child = people[child_index];

        return parentsHaveSensibleAgesAtChildBirth(father, mother, child);
    }

    public boolean parentsHaveSensibleAgesAtChildBirth(final CompactPerson father, final CompactPerson mother, final CompactPerson child) {

        return motherAliveAtBirth(mother, child) && motherNotTooYoungAtBirth(mother, child) && motherNotTooOldAtBirth(mother, child) && fatherAliveAtConception(father, child) && fatherNotTooYoungAtBirth(father, child) && fatherNotTooOldAtBirth(father, child);
    }

    /**
     * Generate people with random birthdays drawn from distribution, then sort into date of birth order.
     */
    private void createPeople() {

        for (int i = 0; i < people.length; i++) {
            people[i] = new CompactPerson(date_of_birth_distribution.getSample(), sex_distribution.getSample());
            progressStep();
        }

        sortPeopleByAge();
    }

    private void createDeaths() {

        for (final CompactPerson person : people) {

            final int age_at_death_in_days = age_at_death_distribution.getSample();
            final int date_of_death = person.date_of_birth + age_at_death_in_days;

            if (DateManipulation.daysToYear(date_of_death) <= END_YEAR) {
                person.date_of_death = date_of_death;
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
        int marriage_date = (int) (person.date_of_birth + age_at_first_marriage_distribution.getSample());

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
                CompactPartnership.createPartnership(people[first_partner_index], first_partner_index, people[partner_index], partner_index, marriage_date);
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

        int start_index = Math.max(partnership.partner1, partnership.partner2) + 1; // the index at which we start to search for possible children.

        for (int i = 0; i < number_of_children; i++) {

            final int earliest_birth_date = earliestAcceptableBirthDate(partnership, previous_child_birth_date);

            final int child_index = findSuitableChild(partnership, start_index, earliest_birth_date, latest_acceptable_birth_date);

            // May not be able to find suitable child if time has run out due to a subsequent marriage, or the whole simulation ending.
            if (child_index == -1) {
                break;
            }

            final CompactPerson child = people[child_index];

            children.add(child_index);
            previous_child_birth_date = child.date_of_birth;
            child.setHasParents();
            start_index = child_index + 1; // start looking beyond the last child already chosen.
        }
    }

    private int earliestAcceptableBirthDate(final CompactPartnership partnership, final int previous_child_birth_date) {

        return previous_child_birth_date == 0 ? DateManipulation.addYears(partnership.getMarriageDate(), TIME_BEFORE_FIRST_CHILD) : DateManipulation.addYears(previous_child_birth_date, INTER_CHILD_INTERVAL);
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

        final int husband_index = husband(partnership);
        final int wife_index = wife(partnership);

        final Condition conditions = new Condition() {

            @Override
            public int check(final int child_index) {

                final CompactPerson p = people[child_index];

                if (p.date_of_birth >= latest_birth_date)
                    return Condition.NEGATIVE_STOP;

                return !p.isIncomer() && p.date_of_birth > earliest_birth_date && p.date_of_birth < latest_birth_date && !p.hasParents() && parentsHaveSensibleAgesAtChildBirth(husband_index, wife_index, child_index) && !marriedToAnyChildrenOf(child_index, partnership) ? Condition.POSITIVE : Condition.NEGATIVE_CONTINUE;
            }
        };

        return findPerson(start_search_index, conditions);
    }

    private static boolean motherAliveAtBirth(final CompactPerson mother, final CompactPerson child) {

        return dateNotAfter(child.date_of_birth, mother.date_of_death);
    }

    private static boolean motherNotTooYoungAtBirth(final CompactPerson mother, final CompactPerson child) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother, child);

        return notLessThan(mothers_age_at_birth, MINIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean motherNotTooOldAtBirth(final CompactPerson mother, final CompactPerson child) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother, child);

        return notGreaterThan(mothers_age_at_birth, MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherAliveAtConception(final CompactPerson father, final CompactPerson child) {

        return dateNotAfter(child.date_of_birth, father.date_of_death + MAX_GESTATION_IN_DAYS);
    }

    private static boolean fatherNotTooYoungAtBirth(final CompactPerson father, final CompactPerson child) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father, child);

        return notLessThan(fathers_age_at_birth, MINIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherNotTooOldAtBirth(final CompactPerson father, final CompactPerson child) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father, child);

        return notGreaterThan(fathers_age_at_birth, MAXIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static int parentsAgeAtChildBirth(final CompactPerson parent, final CompactPerson child) {

        return DateManipulation.differenceInYears(parent.date_of_birth, child.date_of_birth);
    }

    private static boolean notLessThan(final int i1, final int i2) {

        return i1 >= i2;
    }

    private static boolean notGreaterThan(final int i1, final int i2) {

        return i1 <= i2;
    }

    private static boolean dateNotAfter(final int date1, final int date2) {

        return notGreaterThan(date1, date2);
    }

    private boolean marriedToAnyChildrenOf(final int person_index, final CompactPartnership partnership) {

        // Iterate over all partnerships of the people in the given partnership.
        for (final int partner : new Integer[]{partnership.partner1, partnership.partner2}) {
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

        final Condition partner_compatible = new Condition() {

            @Override
            public int check(final int person_index) {

                return partnersAreCompatible(first_partner_index, person_index, marriage_date) ? Condition.POSITIVE : Condition.NEGATIVE_CONTINUE;
            }
        };

        return findPerson(start_index, partner_compatible);
    }

    private boolean partnersAreCompatible(final int partner_index, final int candidate_partner_index, final int marriage_date) {

        final CompactPerson person = people[partner_index];
        final CompactPerson candidate_partner = people[candidate_partner_index];

        return CompactPerson.oppositeSex(person, candidate_partner) && ageDifferenceNotTooGreat(person, candidate_partner) && notPreviouslyPartners(person, partner_index, candidate_partner) && notTooManyPartnerships(candidate_partner) && notTooRecentPartnership(candidate_partner, marriage_date);
    }

    private boolean ageDifferenceNotTooGreat(final CompactPerson person, final CompactPerson candidate_partner) {

        return Math.abs(DateManipulation.differenceInYears(person.date_of_birth, candidate_partner.date_of_birth)) <= PARTNERSHIP_AGE_DIFFERENCE_LIMIT;
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

        return candidate_partner.getPartnerships() == null || DateManipulation.differenceInYears(candidate_partner.mostRecentPartnership().getMarriageDate(), marriage_date) > MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS;
    }

    private void sortPeopleByAge() {

        final List<CompactPerson> people_list = Arrays.asList(getPeopleArray());
        final QuickSort<CompactPerson> sorter = new QuickSort<>(people_list, new Comparator<CompactPerson>() {

            @Override
            public int compare(final CompactPerson p1, final CompactPerson p2) {

                return p1.date_of_birth - p2.date_of_birth;
            }
        });
        sorter.sort();
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "too expensive...")
    public CompactPerson[] getPeopleArray() {

        return people;
    }
}
