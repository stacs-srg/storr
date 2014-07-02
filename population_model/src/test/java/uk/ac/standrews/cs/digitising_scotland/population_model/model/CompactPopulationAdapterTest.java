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
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

public class CompactPopulationAdapterTest {

    @Test
    public void findPersonInEmptyPopulation() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(0);
        assertNull(population.findPerson(0));
    }

    @Test
    public void findPartnershipInEmptyPopulation() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(0);
        assertNull(population.findPartnership(0));
    }

    @Test
    public void findNonExistentPerson() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(1);
        assertNull(population.findPerson(-1));
    }

    @Test
    public void findNonExistentPartnership() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(1);
        assertNull(population.findPartnership(-1));
    }

    @Test
    public void findOnlyPersonInPopulation() throws IOException, InconsistentWeightException {

        findLastPerson(1);
    }

    @Test
    public void findLastPersonInPopulation() throws IOException, InconsistentWeightException {

        findLastPerson(10);
    }

    @Test
    public void iterateOverEmptyPopulation() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(0);

        checkPersonIteration(population);
        checkPartnershipIteration(population);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextPersonFromEmptyPopulation() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(0);
        population.getPeople().iterator().next();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextPartnershipFromEmptyPopulation() throws IOException, InconsistentWeightException {

        IPopulation population = makePopulation(0);
        population.getPartnerships().iterator().next();
    }

    @Test
    public void iterateOverPopulationSizeOne() throws IOException, InconsistentWeightException {
        iterateOverPopulationWithoutPartnerships(1);
    }

    @Test
    public void iterateOverPopulationSize3() throws IOException, InconsistentWeightException {
        iterateOverPopulationWithoutPartnerships(3);
    }

    public void iterateOverPopulationWithoutPartnerships(int population_size) throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(population_size);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population);
    }

    @Test
    public void iterateOverPopulationWithOnePartnership() throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(3);

        CompactPartnership partnership = new CompactPartnership(0, 0, 0);
        List<CompactPartnership> partnerships = new ArrayList<>();
        partnerships.add(partnership);
        people[1].setPartnerships(partnerships);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population, partnership);
    }

    @Test
    public void iterateOverPopulationWithTwoFamilies() throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeopleInTwoFamilies();
        List<CompactPartnership> partnerships1 = people[0].getPartnerships();
        CompactPartnership partnership = partnerships1.get(0);
        CompactPartnership partnership1 = people[2].getPartnerships().get(0);
        CompactPartnership[] partnerships = new CompactPartnership[]{partnership, partnership1};
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        CompactPerson[] people_in_expected_order = new CompactPerson[]{people[0], people[4], people[6], people[1], people[2], people[3], people[5], people[7]};

        checkPersonIteration(population, people_in_expected_order);
        checkPartnershipIteration(population, partnerships);
    }

    @Test
    public void findPartnershipInPopulationWithOnePartnership() throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(3);

        CompactPartnership partnership = new CompactPartnership(0, 0, 0);
        List<CompactPartnership> partnerships = new ArrayList<>();
        partnerships.add(partnership);
        people[1].setPartnerships(partnerships);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEqualIds(population.findPartnership(partnership.getId()), partnership);
    }

    @Test
    public void iterateOverPopulationWithThreePartnerships() throws IOException, InconsistentWeightException {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        CompactPerson[] people = makePopulationWithPartnerships(partnership1, partnership2, partnership3);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population, partnership1, partnership2, partnership3);
    }

    @Test
    public void findPartnershipInPopulationWithThreePartnerships() throws IOException, InconsistentWeightException {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        CompactPerson[] people = makePopulationWithPartnerships(partnership1, partnership2, partnership3);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEqualIds(population.findPartnership(partnership1.getId()), partnership1);
        assertEqualIds(population.findPartnership(partnership2.getId()), partnership2);
        assertEqualIds(population.findPartnership(partnership3.getId()), partnership3);

        assertNull(population.findPartnership(-1));
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPersonIterations() throws IOException, InconsistentWeightException {

        doTooManyIterations(makePopulationWithThreePartnerships().getPeople().iterator(), 3);
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPartnershipIterations() throws IOException, InconsistentWeightException {

        doTooManyIterations(makePopulationWithThreePartnerships().getPartnerships().iterator(), 3);
    }

    @Test
    public void iteratorDoesntRepeat() throws IOException, InconsistentWeightException {

//        IPopulation population1 = makePopulation(100);
//        assertDoesntRepeat(population1);
//
//        IPopulation population2 = makePopulationWithThreePartnerships();
//        assertDoesntRepeat(population2);

        CompactPerson[] people = makePeopleInTwoFamilies();

        IPopulation population3 = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
        assertDoesntRepeat(population3);
    }

    private void assertDoesntRepeat(IPopulation population) {

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

    private IPopulation makePopulation(int population_size) throws IOException, InconsistentWeightException {

        return new CompactPopulationAdapter(new CompactPopulation(makePeople(population_size), 0, 0));
    }

    private CompactPerson[] makePeople(int n) {

        CompactPerson[] result = new CompactPerson[n];
        for (int i = 0; i < n; i++) {
            result[i] = new CompactPerson(0, true);
        }
        return result;
    }

    private IPopulation makePopulationWithThreePartnerships() throws IOException, InconsistentWeightException {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        CompactPerson[] people = makePopulationWithPartnerships(partnership1, partnership2, partnership3);
        return new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));
    }

    private CompactPerson[] makePopulationWithPartnerships(CompactPartnership partnership1, CompactPartnership partnership2, CompactPartnership partnership3) {

        CompactPerson[] population = makePeople(3);

        List<CompactPartnership> partnerships1 = new ArrayList<>();
        List<CompactPartnership> partnerships2 = new ArrayList<>();

        partnerships1.add(partnership1);
        population[0].setPartnerships(partnerships1);

        partnerships2.add(partnership2);
        partnerships2.add(partnership3);
        partnerships2.add(partnership1); // Two people may share a partnership.
        population[2].setPartnerships(partnerships2);

        return population;
    }

    private CompactPerson[] makePeopleInTwoFamilies() throws IOException, InconsistentWeightException {

        CompactPerson fatherA = new CompactPerson(0, true);
        CompactPerson wifeA = new CompactPerson(0, false);
        CompactPerson child1A = new CompactPerson(0, false);
        CompactPerson child2A = new CompactPerson(0, true);

        CompactPerson fatherB = new CompactPerson(0, true);
        CompactPerson wifeB = new CompactPerson(0, false);
        CompactPerson child1B = new CompactPerson(0, true);
        CompactPerson child2B = new CompactPerson(0, false);

        CompactPerson[] population = new CompactPerson[]{fatherA, wifeA, wifeB, fatherB, child1A, child1B, child2A, child2B};

        CompactPartnership partnershipA = new CompactPartnership(0, 1, 0);
        CompactPartnership partnershipB = new CompactPartnership(2, 3, 0);

        List<CompactPartnership> partnershipsHusbandA = new ArrayList<>();
        List<CompactPartnership> partnershipsWifeA = new ArrayList<>();
        List<CompactPartnership> partnershipsHusbandB = new ArrayList<>();
        List<CompactPartnership> partnershipsWifeB = new ArrayList<>();

        partnershipsHusbandA.add(partnershipA);
        partnershipsWifeA.add(partnershipA);
        partnershipsHusbandB.add(partnershipB);
        partnershipsWifeB.add(partnershipB);

        fatherA.setPartnerships(partnershipsHusbandA);
        wifeA.setPartnerships(partnershipsWifeA);
        fatherB.setPartnerships(partnershipsHusbandB);
        wifeB.setPartnerships(partnershipsWifeB);

        List<Integer> childrenA = new ArrayList<>();
        List<Integer> childrenB = new ArrayList<>();

        childrenA.add(4);
        childrenA.add(6);
        childrenB.add(5);
        childrenB.add(7);

        partnershipA.setChildren(childrenA);
        partnershipB.setChildren(childrenB);

        return population;
    }

    private void findLastPerson(int population_size) throws IOException, InconsistentWeightException {

        CompactPerson[] people = makePeople(population_size);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEquals(people[population_size - 1].getId(), population.findPerson(people[population_size - 1].getId()).getId());
    }

    private void checkPersonIteration(IPopulation population, CompactPerson... people) {

        Iterator<IPerson> person_iterator = population.getPeople().iterator();

        for (CompactPerson person : people) {

            assertTrue(person_iterator.hasNext());
            assertEquals(person_iterator.next().getId(), person.getId());
        }

        assertFalse(person_iterator.hasNext());
    }

    private void checkPartnershipIteration(IPopulation population, CompactPartnership... partnerships) {

        Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

        for (int i = 0; i < partnerships.length; i++) {

            assertTrue(partnership_iterator.hasNext());
            assertEqualIds(partnership_iterator.next(), partnerships[i]);
        }

        assertFalse(partnership_iterator.hasNext());
    }

    private void doTooManyIterations(Iterator<?> iterator, int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }

    private void assertEqualIds(IPartnership partnership1, CompactPartnership partnership2) {

        assertEquals(partnership1.getId(), partnership2.getId());
    }
}
