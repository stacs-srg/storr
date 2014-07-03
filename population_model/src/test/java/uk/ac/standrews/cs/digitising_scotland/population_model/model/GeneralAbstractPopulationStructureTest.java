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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.io.IOException;
import java.util.Collection;
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
public class GeneralAbstractPopulationStructureTest extends PopulationStructureTest {

    private final IPopulation population;
    private final int[] expected_people_id_order;
    private final int[] expected_partnership_id_order;
    private final boolean consistent_across_iterations;

    @Parameterized.Parameters(name = "{0}, {3}")
    public static Collection<Object[]> generateData() throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        return expandWithBooleanOptions(unconnectedPopulation(0), unconnectedPopulation(1), unconnectedPopulation(3), unconnectedPopulation(100), populationWithOnePartnership(), populationWithThreePartnerships(), populationWithTwoFamilies(), fullPopulation(1000));
    }

    public GeneralAbstractPopulationStructureTest(IPopulation population, int[] expected_people_id_order, int[] expected_partnership_id_order, final boolean consistent_across_iterations) {

        this.population = population;
        this.expected_people_id_order = expected_people_id_order;
        this.expected_partnership_id_order = expected_partnership_id_order;
        this.consistent_across_iterations = consistent_across_iterations;

        population.setConsistentAcrossIterations(consistent_across_iterations);
    }

    @Test
    public void findNonExistentPerson() {

        assertNull(population.findPerson(-1));
    }

    @Test
    public void findNonExistentPartnership() {

        assertNull(population.findPartnership(-1));
    }

    @Test
    public void iterateOverPopulation() {

        assertPersonIterationIsAsExpected();
        assertPartnershipIterationIsAsExpected();
    }

    @Test
    public void iteratorDoesntRepeat() {

        Set<Integer> people = new HashSet<>();
        for (IPerson person : population.getPeople()) {
            assertFalse(people.contains(person.getId()));
            people.add(person.getId());
        }

        Set<Integer> partnerships = new HashSet<>();
        for (IPartnership partnership : population.getPartnerships()) {
            assertFalse(partnerships.contains(partnership.getId()));
            partnerships.add(partnership.getId());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPersonIterations() {

        doTooManyIterations(population.getPeople().iterator(), population.getNumberOfPeople());
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPartnershipIterations() {

        Iterator<IPartnership> iterator = population.getPartnerships().iterator();
        int numberOfPartnerships = population.getNumberOfPartnerships();

        doTooManyIterations(iterator, numberOfPartnerships);
    }

    @Test
    public void peopleRetrievedConsistently() {

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
    public void partnershipsRetrievedConsistently() {

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
    public void deathsAfterBirths() {

        for (IPerson person : population.getPeople()) {

            assertDeathAfterBirth(person);
        }
    }

    @Test
    public void surnamesInheritedOnMaleLine() {

        for (IPerson person : population.getPeople()) {

            assertSurnameInheritedOnMaleLine(person);
        }
    }

    private void assertSurnameInheritedOnMaleLine(IPerson person) {

        if (person.getSex() == IPerson.MALE) {

            List<Integer> partnership_ids = person.getPartnerships();
            if (partnership_ids != null) {
                for (int partnership_id : partnership_ids) {

                    IPartnership partnership = population.findPartnership(partnership_id);
                    List<Integer> child_ids = partnership.getChildren();

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

    private void assertRetrievedConsistently(IPerson[] sample) {

        for (IPerson person : sample) {
            assertRetrievedConsistently(person);
        }
    }

    private void assertRetrievedConsistently(IPerson person) {

        int id = person.getId();
        IPerson retrieved_person = population.findPerson(id);

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

    private void assertRetrievedConsistently(IPartnership[] sample) {

        for (IPartnership partnership : sample) {
            assertRetrievedConsistently(partnership);
        }
    }

    private void assertRetrievedConsistently(IPartnership partnership) {

        int id = partnership.getId();
        IPartnership retrieved_person = population.findPartnership(id);

        assertEquals(id, retrieved_person.getId());

        if (consistent_across_iterations) {

            assertEquals(partnership.getPartner1Id(), retrieved_person.getPartner1Id());
            assertEquals(partnership.getPartner2Id(), retrieved_person.getPartner2Id());
            assertEquals(partnership.getMarriageDate(), retrieved_person.getMarriageDate());
            assertChildrenEqual(partnership.getChildren(), retrieved_person.getChildren());
        }
    }

    private void assertChildrenEqual(List<Integer> children1, List<Integer> children2) {

        assertArrayEquals(children1.toArray(new Integer[]{}), children2.toArray(new Integer[]{}));
    }

    private void doTooManyIterations(Iterator<?> iterator, int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }

    private void assertPersonIterationIsAsExpected() {

        if (expected_people_id_order != null) {

            Iterator<IPerson> person_iterator = population.getPeople().iterator();

            for (int person_id : expected_people_id_order) {

                assertTrue(person_iterator.hasNext());
                assertEquals(person_iterator.next().getId(), person_id);
            }

            assertFalse(person_iterator.hasNext());
        }
    }

    private void assertPartnershipIterationIsAsExpected() {

        if (expected_partnership_id_order != null) {

            Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            for (int partnership_id : expected_partnership_id_order) {

                assertTrue(partnership_iterator.hasNext());
                assertEquals(partnership_iterator.next().getId(), partnership_id);
            }

            assertFalse(partnership_iterator.hasNext());
        }
    }
}
