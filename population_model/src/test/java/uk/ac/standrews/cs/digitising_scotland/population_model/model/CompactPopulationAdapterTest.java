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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class CompactPopulationAdapterTest {

    @Test
    public void findPersonInEmptyPopulation() {

        IPopulation population = makePopulation(0);
        assertNull(population.findPerson(0));
    }

    @Test
    public void findPartnershipInEmptyPopulation() {

        IPopulation population = makePopulation(0);
        assertNull(population.findPartnership(0));
    }

    @Test
    public void findNonExistentPerson() {

        IPopulation population = makePopulation(1);
        assertNull(population.findPerson(-1));
    }

    @Test
    public void findNonExistentPartnership() {

        IPopulation population = makePopulation(1);
        assertNull(population.findPartnership(-1));
    }

    @Test
    public void findOnlyPersonInPopulation() {

        findLastPerson(1);
    }

    @Test
    public void findLastPersonInPopulation() {

        findLastPerson(10);
    }

    @Test
    public void iterateOverEmptyPopulation() {

        IPopulation population = makePopulation(0);

        checkPersonIteration(population);
        checkPartnershipIteration(population);
        checkPopulationIteration(population);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextPersonFromEmptyPopulation() {

        IPopulation population = makePopulation(0);
        population.getPeople().iterator().next();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextPartnershipFromEmptyPopulation() {

        IPopulation population = makePopulation(0);
        population.getPartnerships().iterator().next();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextObjectFromEmptyPopulation() {

        IPopulation population = makePopulation(0);
        population.getPopulation().iterator().next();
    }

    @Test
    public void iterateOverPopulationSizeOne() {
        iterateOverPopulationWithoutPartnerships(1);
    }

    @Test
    public void iterateOverPopulationSize3() {
        iterateOverPopulationWithoutPartnerships(3);
    }

    public void iterateOverPopulationWithoutPartnerships(int population_size) {

        CompactPerson[] people = makePeople(population_size);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population);
        checkPopulationIteration(population, people);
    }

    @Test
    public void iterateOverPopulationWithOnePartnership() {

        CompactPerson[] people = makePeople(3);

        CompactPartnership partnership = new CompactPartnership(0, 0, 0);
        List<CompactPartnership> partnerships = new ArrayList<>();
        partnerships.add(partnership);
        people[1].setPartnerships(partnerships);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population, partnership);
        checkPopulationIteration(population, people[0], people[1], partnership, people[2]);
    }

    @Test
    public void findPartnershipInPopulationWithOnePartnership() {

        CompactPerson[] people = makePeople(3);

        CompactPartnership partnership = new CompactPartnership(0, 0, 0);
        List<CompactPartnership> partnerships = new ArrayList<>();
        partnerships.add(partnership);
        people[1].setPartnerships(partnerships);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEquals(population.findPartnership(partnership.getId()), partnership);
    }

    @Test
    public void iterateOverPopulationWithThreePartnerships() {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        CompactPerson[] people = makePopulationWithPartnerships(partnership1, partnership2, partnership3);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        checkPersonIteration(population, people);
        checkPartnershipIteration(population, partnership1, partnership2, partnership3);
        checkPopulationIteration(population, people[0], partnership1, people[1], people[2], partnership2, partnership3);
    }

    @Test
    public void findPartnershipInPopulationWithThreePartnerships() {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        CompactPerson[] people = makePopulationWithPartnerships(partnership1, partnership2, partnership3);

        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEquals(population.findPartnership(partnership1.getId()), partnership1);
        assertEquals(population.findPartnership(partnership2.getId()), partnership2);
        assertEquals(population.findPartnership(partnership3.getId()), partnership3);

        assertNull(population.findPartnership(-1));
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPersonIterations() {

        doTooManyIterations(makePopulationWithThreePartnerships().getPeople().iterator(), 3);
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyPartnershipIterations() {

        doTooManyIterations(makePopulationWithThreePartnerships().getPartnerships().iterator(), 3);
    }

    @Test(expected = NoSuchElementException.class)
    public void tooManyObjectIterations() {

        doTooManyIterations(makePopulationWithThreePartnerships().getPopulation().iterator(), 6);
    }

    private IPopulation makePopulation(int population_size) {

        return new CompactPopulationAdapter(new CompactPopulation(makePeople(population_size), 0, 0));
    }

    private CompactPerson[] makePeople(int n) {

        CompactPerson[] result = new CompactPerson[n];
        for (int i = 0; i < n; i++) {
            result[i] = new CompactPerson(0, true);
        }
        return result;
    }

    private IPopulation makePopulationWithThreePartnerships() {

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
        population[2].setPartnerships(partnerships2);

        return population;
    }

    private void findLastPerson(int population_size) {

        CompactPerson[] people = makePeople(population_size);
        IPopulation population = new CompactPopulationAdapter(new CompactPopulation(people, 0, 0));

        assertEquals(people[population_size - 1], population.findPerson(people[population_size - 1].getId()));
    }

    private void checkPersonIteration(IPopulation population, CompactPerson... people) {

        Iterator<IPerson> person_iterator = population.getPeople().iterator();

        for (int i = 0; i < people.length; i++) {

            assertTrue(person_iterator.hasNext());
            assertEquals(person_iterator.next(), people[i]);
        }

        assertFalse(person_iterator.hasNext());
    }

    private void checkPartnershipIteration(IPopulation population, CompactPartnership... partnerships) {

        Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

        for (int i = 0; i < partnerships.length; i++) {

            assertTrue(partnership_iterator.hasNext());
            assertEquals(partnership_iterator.next(), partnerships[i]);
        }

        assertFalse(partnership_iterator.hasNext());
    }

    private void checkPopulationIteration(IPopulation population, Object... objects) {

        Iterator<Object> partnership_iterator = population.getPopulation().iterator();

        for (int i = 0; i < objects.length; i++) {

            assertTrue(partnership_iterator.hasNext());
            assertEquals(partnership_iterator.next(), objects[i]);
        }

        assertFalse(partnership_iterator.hasNext());
    }

    private void doTooManyIterations(Iterator<?> iterator, int number_available) {

        for (int i = 0; i < number_available + 1; i++) {
            iterator.next();
        }
    }
}
