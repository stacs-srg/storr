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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
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
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class GeneralPopulationStructureTests {

    protected IPopulation population;
    private boolean consistent_across_iterations = false;

    public GeneralPopulationStructureTests(IPopulation population, final boolean consistent_across_iterations) {

        this.population = population;
        this.consistent_across_iterations = consistent_across_iterations;

        population.setConsistentAcrossIterations(consistent_across_iterations);
    }

    public GeneralPopulationStructureTests() {

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

        Set<Integer> people = new HashSet<>();
        for (IPerson person : population.getPeople()) {
            assertFalse(people.contains(person.getId()));
            people.add(person.getId());
        }
        assertEquals(population.getNumberOfPeople(), people.size());

        Set<Integer> partnerships = new HashSet<>();
        for (IPartnership partnership : population.getPartnerships()) {
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

        Iterator<IPartnership> iterator = population.getPartnerships().iterator();
        int numberOfPartnerships = population.getNumberOfPartnerships();

        doTooManyIterations(iterator, numberOfPartnerships);
    }

    @Test
    public void peopleRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPeople() > 40) {
            Iterator<IPerson> person_iterator = population.getPeople().iterator();

            for (int i = 0; i < 30; i++) {
                person_iterator.next();
            }

            IPerson[] sample = new IPerson[]{person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next(), person_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (IPerson person : population.getPeople()) {
            assertRetrievedConsistently(person);
        }
    }

    @Test
    public void partnershipsRetrievedConsistently() throws Exception {

        // Check consistency after iteration, if the population is large enough to take a sample from the middle.

        if (population.getNumberOfPartnerships() > 20) {

            Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            // Check consistency after iteration.
            for (int i = 0; i < 10; i++) {
                partnership_iterator.next();
            }

            IPartnership[] sample = new IPartnership[]{partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next(), partnership_iterator.next()};
            assertRetrievedConsistently(sample);
        }

        // Check consistency during iteration.

        for (IPartnership partnership : population.getPartnerships()) {
            assertRetrievedConsistently(partnership);
        }
    }

    @Test
    public void deathsAfterBirths() throws Exception {

        for (IPerson person : population.getPeople()) {

            assertDeathAfterBirth(person);
        }
    }

    @Test
    public void surnamesInheritedOnMaleLine() throws Exception {

        for (IPerson person : population.getPeople()) {

            assertSurnameInheritedOnMaleLine(person);
        }
    }

    @Test
    public void noSiblingPartners() throws Exception {

        for (IPerson person : population.getPeople()) {

            assertNoneOfChildrenAreSiblingPartners(person);
        }
    }

    @Test
    public void noParentPartnerOfChild() {

        for (IPartnership partnership : population.getPartnerships()) {

            assertParentNotPartnerOfChild(partnership);
        }
    }

    @Test
    public void noSameSexPartnerships() throws Exception {

        for (IPartnership partnership : population.getPartnerships()) {

            assertPartnersDifferentSex(partnership);
        }
    }

    @Test
    public void parentsHaveSensibleAgesAtBirths() throws Exception {

        for (IPartnership partnership : population.getPartnerships()) {

            assertParentsHaveSensibleAgesAtBirth(partnership);
        }
    }

    private void assertParentsHaveSensibleAgesAtBirth(IPartnership partnership) throws Exception {

        IPerson partner1 = population.findPerson(partnership.getPartner1Id());
        IPerson partner2 = population.findPerson(partnership.getPartner2Id());

        IPerson father = partner1.getSex() == IPerson.MALE ? partner1 : partner2;
        IPerson mother = partner1.getSex() == IPerson.FEMALE ? partner1 : partner2;

        for (final int child_id : partnership.getChildIds()) {

            IPerson child = population.findPerson(child_id);
            assertTrue(PopulationLogic.parentsHaveSensibleAgesAtChildBirth(father, mother, child));
        }
    }

    private void assertParentNotPartnerOfChild(IPartnership partnership) {

        List<Integer> child_ids = partnership.getChildIds();
        if (child_ids != null) {

            for (final int child_id : child_ids) {

                assertFalse(child_id == partnership.getPartner1Id());
                assertFalse(child_id == partnership.getPartner2Id());
            }
        }
    }

    private void assertPartnersDifferentSex(IPartnership partnership) throws Exception {

        IPerson partner1 = population.findPerson(partnership.getPartner1Id());
        IPerson partner2 = population.findPerson(partnership.getPartner2Id());

        assertFalse(partner1.getSex() == partner2.getSex());
    }

    private void assertNoneOfChildrenAreSiblingPartners(IPerson person) throws Exception {

        // Include half-siblings.
        final Set<Integer> sibling_ids = new HashSet<>();

        if (person.getPartnerships() != null) {
            for (final int partnership_id : person.getPartnerships()) {
                IPartnership partnership = population.findPartnership(partnership_id);

                for (final int child_id : partnership.getChildIds()) {

                    assertNotPartnerOfAny(child_id, sibling_ids);
                    sibling_ids.add(child_id);
                }
            }
        }
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
                IPartnership partnership = population.findPartnership(partnership_id);
                if (partnership.getPartnerOf(p1_id) == p2_id) {
                    return true;
                }
            }
        }
        return false;
    }

    private void assertSurnameInheritedOnMaleLine(IPerson person) throws Exception {

        if (person.getSex() == IPerson.MALE) {

            List<Integer> partnership_ids = person.getPartnerships();
            if (partnership_ids != null) {
                for (int partnership_id : partnership_ids) {

                    IPartnership partnership = population.findPartnership(partnership_id);
                    List<Integer> child_ids = partnership.getChildIds();

                    if (child_ids != null) {

                        for (int child_id : child_ids) {
                            IPerson child = population.findPerson(child_id);
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

    private void assertDeathAfterBirth(IPerson person) {

        Date death_date = person.getDeathDate();

        if (death_date != null) {

            Date birth_date = person.getBirthDate();
            assertTrue(DateManipulation.differenceInYears(birth_date, death_date) >= 0);
        }
    }

    private void assertRetrievedConsistently(IPerson[] sample) throws Exception {

        for (IPerson person : sample) {
            assertRetrievedConsistently(person);
        }
    }

    private void assertRetrievedConsistently(IPerson person) throws Exception {

        int id = person.getId();
        IPerson retrieved_person = population.findPerson(id);

        if (retrieved_person == null) {
            System.out.println("null retrieved for id: " + id);
        }

        assertEquals(id, retrieved_person.getId());

        if (consistent_across_iterations) {

            assertEquals(person.getFirstName(), retrieved_person.getFirstName());
            assertEquals(person.getSurname(), retrieved_person.getSurname());
            assertEquals(person.getSex(), retrieved_person.getSex());
            assertEquals(person.getAddress(), retrieved_person.getAddress());
            assertEquals(person.getBirthDate(), retrieved_person.getBirthDate());
            assertEquals(person.getDeathDate(), retrieved_person.getDeathDate());
            assertEquals(person.getOccupation(), retrieved_person.getOccupation());
            assertEquals(person.getCauseOfDeath(), retrieved_person.getCauseOfDeath());
        }
    }

    private void assertRetrievedConsistently(IPartnership[] sample) throws Exception {

        for (IPartnership partnership : sample) {
            assertRetrievedConsistently(partnership);
        }
    }

    private void assertRetrievedConsistently(IPartnership partnership) throws Exception {

        int id = partnership.getId();
        IPartnership retrieved_person = population.findPartnership(id);

        assertEquals(id, retrieved_person.getId());

        if (consistent_across_iterations) {

            assertEquals(partnership.getPartner1Id(), retrieved_person.getPartner1Id());
            assertEquals(partnership.getPartner2Id(), retrieved_person.getPartner2Id());
            assertEquals(partnership.getMarriageDate(), retrieved_person.getMarriageDate());
            assertChildrenEqual(partnership.getChildIds(), retrieved_person.getChildIds());
        }
    }

    private void assertChildrenEqual(List<Integer> children1, List<Integer> children2) {

        assertArrayEquals(children1.toArray(new Integer[children1.size()]), children2.toArray(new Integer[children2.size()]));
    }

    private void doTooManyIterations(Iterator<?> iterator, int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }
}
