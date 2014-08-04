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

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPerson;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests of properties of abstract population interface that should hold for all populations.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class GeneralPopulationStructureTests {

    public static final int PEOPLE_ITERATION_SAMPLE_THRESHOLD = 40;
    public static final int PEOPLE_ITERATION_SAMPLE_START = 30;
    public static final int PARTNERSHIP_ITERATION_SAMPLE_THRESHOLD = 20;
    public static final int PARTNERSHIP_ITERATION_SAMPLE_START = 10;
    protected IPopulation population;
    private boolean consistent_across_iterations = false;

    public GeneralPopulationStructureTests(final IPopulation population, final boolean consistent_across_iterations) {

        this(consistent_across_iterations);

        this.population = population;
        population.setConsistentAcrossIterations(consistent_across_iterations);
    }

    public GeneralPopulationStructureTests(final boolean consistent_across_iterations) {

        this.consistent_across_iterations = consistent_across_iterations;
    }

    @Test
    public void findNonExistentPerson() throws Exception {

        assertNull(population.findPerson(-1));
    }

    @Test
    public void findNonExistentPartnership() throws Exception {

        assertNull(population.findPartnership(-1));
    }

    @Test
    public void iterateOverPopulation() throws Exception {

        final Set<Integer> people = new HashSet<>();
        for (final IPerson person : population.getPeople()) {
            assertFalse(people.contains(person.getId()));
            people.add(person.getId());
        }
        assertEquals(population.getNumberOfPeople(), people.size());

        final Set<Integer> partnerships = new HashSet<>();
        for (final IPartnership partnership : population.getPartnerships()) {
            assertFalse(partnerships.contains(partnership.getId()));
            partnerships.add(partnership.getId());
        }
        assertEquals(population.getNumberOfPartnerships(), partnerships.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPersonIterations() throws Exception {

        doTooManyIterations(population.getPeople().iterator(), population.getNumberOfPeople());
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPartnershipIterations() throws Exception {

        final Iterator<IPartnership> iterator = population.getPartnerships().iterator();
        final int numberOfPartnerships = population.getNumberOfPartnerships();

        doTooManyIterations(iterator, numberOfPartnerships);
    }

    @Test
    public void peopleRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPeople() > PEOPLE_ITERATION_SAMPLE_THRESHOLD) {
            final Iterator<IPerson> person_iterator = population.getPeople().iterator();

            for (int i = 0; i < PEOPLE_ITERATION_SAMPLE_START; i++) {
                person_iterator.next();
            }

            final IPerson[] sample = new IPerson[]{person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (final IPerson person : population.getPeople()) {
            assertRetrievedConsistently(person);
        }
    }

    @Test
    public void partnershipsRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPartnerships() > PARTNERSHIP_ITERATION_SAMPLE_THRESHOLD) {

            final Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            // Check consistency after iteration.
            for (int i = 0; i < PARTNERSHIP_ITERATION_SAMPLE_START; i++) {
                partnership_iterator.next();
            }

            final IPartnership[] sample = new IPartnership[]{partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (final IPartnership partnership : population.getPartnerships()) {
            assertRetrievedConsistently(partnership);
        }
    }

    @Test
    public void birthsBeforeDeaths() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertBirthBeforeDeath(person);
        }
    }

    @Test
    public void birthInfoConsistent() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertBirthInfoConsistent(person);
        }
    }

    private static void assertBirthInfoConsistent(final IPerson person) {

        assertFalse(person.getBirthDate() == null && person.getBirthPlace() != null);
    }

    @Test
    public void deathInfoConsistent() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertDeathInfoConsistent(person);
        }
    }

    private static void assertDeathInfoConsistent(final IPerson person) {

        assertFalse(person.getDeathDate() == null && (person.getDeathPlace() != null || person.getDeathCause() != null));
    }

    @Test
    public void birthsBeforeMarriages() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertBirthBeforeMarriages(person);
        }
    }

    @Test
    public void marriagesBeforeDeaths() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertMarriagesBeforeDeath(person);
        }
    }

    @Test
    public void sexesConsistent() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertSexesConsistent(partnership);
        }
    }

    @Test
    public void surnamesInheritedOnMaleLine() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertSurnameInheritedOnMaleLine(person);
        }
    }

    @Test
    public void noSiblingPartners() throws Exception {

        for (final IPerson person : population.getPeople()) {

            assertNoneOfChildrenAreSiblingPartners(person);
        }
    }

    @Test
    public void noParentPartnerOfChild() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentNotPartnerOfChild(partnership);
        }
    }

    @Test
    public void parentsHaveSensibleAgesAtBirths() throws Exception {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentsHaveSensibleAgesAtBirth(partnership);
        }
    }

    @Test
    public void parentsAndChildrenConsistent() {

        for (final IPartnership partnership : population.getPartnerships()) {

            assertParentsAndChildrenConsistent(partnership);
        }
    }

    private void assertParentsAndChildrenConsistent(final IPartnership partnership) {

        final List<Integer> child_ids = partnership.getChildIds();

        if (child_ids != null) {

            for (final int child_id : child_ids) {

                // TODO anything specific to OrganicPopulation should be in a subclass.
                // TODO why is this logic necessary anyway - why would children be seed people?

                final IPerson child = population.findPerson(child_id);
//                System.out.println(child.getClass().getName());
                if (child == null) {
                    System.out.println("...");
                }
                if (child.getClass().getName() == OrganicPerson.class.getName()) {
                    if (((OrganicPerson) child).isSeedPerson()) {
                        assertEquals(child.getParentsPartnership(), -1);
                    } else {
                        assertEquals(child.getParentsPartnership(), partnership.getId());
                    }
                }
            }
        }
    }

    private void assertParentsHaveSensibleAgesAtBirth(final IPartnership partnership) throws Exception {

        final IPerson mother = population.findPerson(partnership.getFemalePartnerId());
        final IPerson father = population.findPerson(partnership.getMalePartnerId());

        for (final int child_id : partnership.getChildIds()) {

            final IPerson child = population.findPerson(child_id);
            assertTrue(PopulationLogic.parentsHaveSensibleAgesAtChildBirth(father, mother, child));
        }
    }

    private static void assertParentNotPartnerOfChild(final IPartnership partnership) {

        final List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {

            for (final int child_id : child_ids) {

                assertFalse(child_id == partnership.getFemalePartnerId());
                assertFalse(child_id == partnership.getMalePartnerId());
            }
        }
    }

    private void assertNoneOfChildrenAreSiblingPartners(final IPerson person) throws Exception {

        // Include half-siblings.
        final Set<Integer> sibling_ids = new HashSet<>();

        if (person.getPartnerships() != null) {
            for (final int partnership_id : person.getPartnerships()) {
                final IPartnership partnership = population.findPartnership(partnership_id);

                for (final int child_id : partnership.getChildIds()) {

                    assertNotPartnerOfAny(child_id, sibling_ids);
                    sibling_ids.add(child_id);
                }
            }
        }
    }

    private void assertSexesConsistent(final IPartnership partnership) {

        assertEquals(population.findPerson(partnership.getFemalePartnerId()).getSex(), IPerson.FEMALE);
        assertEquals(population.findPerson(partnership.getMalePartnerId()).getSex(), IPerson.MALE);
    }

    private void assertNotPartnerOfAny(final int person_id, final Set<Integer> people_ids) throws Exception {

        for (final int another_person_id : people_ids) {
            assertFalse(partnerOf(person_id, another_person_id));
        }
    }

    public boolean partnerOf(final int p1_id, final int p2_id) throws Exception {

        final List<Integer> partnership_ids = population.findPerson(p1_id).getPartnerships();
        if (partnership_ids != null) {
            for (final int partnership_id : partnership_ids) {
                final IPartnership partnership = population.findPartnership(partnership_id);
                if (partnership.getPartnerOf(p1_id) == p2_id) {
                    return true;
                }
            }
        }
        return false;
    }

    private void assertSurnameInheritedOnMaleLine(final IPerson person) throws Exception {

        if (person.getSex() == IPerson.MALE) {

            final List<Integer> partnership_ids = person.getPartnerships();
            if (partnership_ids != null) {
                for (final int partnership_id : partnership_ids) {

                    final IPartnership partnership = population.findPartnership(partnership_id);
                    final List<Integer> child_ids = partnership.getChildIds();

                    if (child_ids != null) {

                        for (final int child_id : child_ids) {
                            final IPerson child = population.findPerson(child_id);
                            assertEquals(person.getSurname(), child.getSurname());

                            if (child.getSex() == IPerson.MALE) {
                                assertSurnameInheritedOnMaleLine(child);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void assertBirthBeforeDeath(final IPerson person) {

        final Date death_date = person.getDeathDate();

        if (death_date != null) {

            final Date birth_date = person.getBirthDate();
            assertTrue(DateManipulation.differenceInYears(birth_date, death_date) >= 0);
        }
    }

    private void assertBirthBeforeMarriages(final IPerson person) {

        final Date birth_date = person.getBirthDate();

        if (birth_date != null) {
            final List<Integer> partnership_ids = person.getPartnerships();
            if (partnership_ids != null) {
                for (final int partnership_id : partnership_ids) {

                    final IPartnership partnership = population.findPartnership(partnership_id);
                    final Date marriage_date = partnership.getMarriageDate();
                    if (marriage_date != null) {
                        int difference = DateManipulation.differenceInYears(birth_date, marriage_date);
                        if (difference < 0) {
                            System.out.println("birth: " + birth_date);
                            System.out.println("marriage: " + marriage_date);
                        }
                        assertTrue(difference >= 0);
                    }
                }
            }
        }
    }

    private void assertMarriagesBeforeDeath(final IPerson person) {

        final Date death_date = person.getDeathDate();

        if (death_date != null) {
            final List<Integer> partnership_ids = person.getPartnerships();
            if (partnership_ids != null) {
                for (final int partnership_id : partnership_ids) {

                    final IPartnership partnership = population.findPartnership(partnership_id);
                    final Date marriage_date = partnership.getMarriageDate();

                    assertTrue(DateManipulation.differenceInDays(marriage_date, death_date) >= 0);
                }
            }
        }
    }

    private void assertRetrievedConsistently(final IPerson[] sample) throws Exception {

        for (final IPerson person : sample) {
            assertRetrievedConsistently(person);
        }
    }

    private void assertRetrievedConsistently(final IPerson person) throws Exception {

        final int id = person.getId();
        final IPerson retrieved_person = population.findPerson(id);

        assertEquals(id, retrieved_person.getId());

        if (consistent_across_iterations) {

            assertEquals(person.getFirstName(), retrieved_person.getFirstName());
            assertEquals(person.getSurname(), retrieved_person.getSurname());
            assertEquals(person.getSex(), retrieved_person.getSex());
            assertEquals(person.getBirthDate(), retrieved_person.getBirthDate());
            assertEquals(person.getBirthPlace(), retrieved_person.getBirthPlace());
            assertEquals(person.getDeathDate(), retrieved_person.getDeathDate());
            assertEquals(person.getDeathPlace(), retrieved_person.getDeathPlace());
            assertEquals(person.getDeathCause(), retrieved_person.getDeathCause());
            assertEquals(person.getOccupation(), retrieved_person.getOccupation());
        }
    }

    private void assertRetrievedConsistently(final IPartnership[] sample) throws Exception {

        for (final IPartnership partnership : sample) {
            assertRetrievedConsistently(partnership);
        }
    }

    private void assertRetrievedConsistently(final IPartnership partnership) throws Exception {

        final int id = partnership.getId();
        final IPartnership retrieved_person = population.findPartnership(id);

        assertEquals(id, retrieved_person.getId());

        if (consistent_across_iterations) {

            assertEquals(partnership.getFemalePartnerId(), retrieved_person.getFemalePartnerId());
            assertEquals(partnership.getMalePartnerId(), retrieved_person.getMalePartnerId());
            assertEquals(partnership.getMarriageDate(), retrieved_person.getMarriageDate());
            assertEquals(partnership.getMarriagePlace(), retrieved_person.getMarriagePlace());
            assertChildrenEqual(partnership.getChildIds(), retrieved_person.getChildIds());
        }
    }

    private static void assertChildrenEqual(final List<Integer> children1, final List<Integer> children2) {

        assertArrayEquals(children1.toArray(new Integer[children1.size()]), children2.toArray(new Integer[children2.size()]));
    }

    private static void doTooManyIterations(final Iterator<?> iterator, final int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }
}
