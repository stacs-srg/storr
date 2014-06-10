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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompactPopulationAdapterTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    private CompactPerson[] makePopulation(int n) {

        CompactPerson[] result =  new CompactPerson[n];
        for (int i = 0; i < n; i++) {
            result[i] = new CompactPerson(0, true);
        }
        return result;
    }

    @Test
    public void testEmptyPopulation1() {

        CompactPerson[] population = makePopulation(0);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        assertFalse(population_interface.getPeople().iterator().hasNext());
        assertFalse(population_interface.getPartnerships().iterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyPopulation2() {

        CompactPerson[] population = makePopulation(0);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        population_interface.getPeople().iterator().next();
    }


    @Test(expected = NoSuchElementException.class)
    public void testEmptyPopulation3() {

        CompactPerson[] population = makePopulation(0);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        population_interface.getPartnerships().iterator().next();
    }

    @Test
    public void testPopulation1() {

        CompactPerson[] population = makePopulation(1);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        assertTrue(population_interface.getPeople().iterator().hasNext());
        assertFalse(population_interface.getPartnerships().iterator().hasNext());
    }

    @Test
    public void testPopulation2() {

        CompactPerson[] population = makePopulation(3);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        Iterator<IPerson> iterator = population_interface.getPeople().iterator();

        iterator.next();
        iterator.next();
        iterator.next();
        assertFalse(iterator.hasNext());

        assertFalse(population_interface.getPartnerships().iterator().hasNext());
    }

    @Test
    public void testPopulation3() {

        CompactPerson[] population = makePopulation(3);

        CompactPartnership partnership = new CompactPartnership(0, 0, 0);
        List<CompactPartnership> partnerships = new ArrayList<>();
        partnerships.add(partnership);
        population[1].setPartnerships(partnerships);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        Iterator<IPartnership> iterator = population_interface.getPartnerships().iterator();

        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), partnership);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testPopulation4() {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        Iterator<IPartnership> iterator = makePartnershipIterator(partnership1, partnership2, partnership3);

        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), partnership1);
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), partnership2);
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), partnership3);
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testPopulation5() {

        CompactPartnership partnership1 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership2 = new CompactPartnership(0, 0, 0);
        CompactPartnership partnership3 = new CompactPartnership(0, 0, 0);

        Iterator<IPartnership> iterator = makePartnershipIterator(partnership1, partnership2, partnership3);

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
    }

    private Iterator<IPartnership> makePartnershipIterator(CompactPartnership partnership1, CompactPartnership partnership2, CompactPartnership partnership3) {

        CompactPerson[] population = makePopulation(3);

        List<CompactPartnership> partnerships1 = new ArrayList<>();
        List<CompactPartnership> partnerships2 = new ArrayList<>();

        partnerships1.add(partnership1);
        population[0].setPartnerships(partnerships1);

        partnerships2.add(partnership2);
        partnerships2.add(partnership3);
        population[2].setPartnerships(partnerships2);

        IPopulation population_interface = new CompactPopulationAdapter(population);

        return population_interface.getPartnerships().iterator();
    }
}
