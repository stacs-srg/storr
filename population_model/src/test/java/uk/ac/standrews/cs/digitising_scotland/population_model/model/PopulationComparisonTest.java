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
import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulationTestCases;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by graham on 21/07/2014.
 */
@RunWith(Parameterized.class)
public abstract class PopulationComparisonTest extends GeneralPopulationStructureTests {

    protected static final Random RANDOM = RandomFactory.getRandom();

    protected IPopulation original_population;
    protected IPopulationWriter population_writer;

    // The name string gives informative labels in the JUnit output.
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> generateData() throws Exception {

        // Use each of the compact population test cases to create a database test population.
        return getTestCases(CompactPopulationTestCases.getTestPopulations());
    }

    public PopulationComparisonTest(final IPopulation population) throws Exception {

        super();
        original_population = population;
        original_population.setConsistentAcrossIterations(true);
    }

    @Test
    public void populationsEquivalent() throws Exception {

        assertEquals(population.getNumberOfPeople(), original_population.getNumberOfPeople());
        assertEquals(population.getNumberOfPartnerships(), original_population.getNumberOfPartnerships());

        assertSamePeopleIds(population.getPeople(), original_population.getPeople());
        assertSamePartnershipIds(population.getPartnerships(), original_population.getPartnerships());

        assertSamePersonAttributes(population, original_population);
        assertSamePartnershipAttributes(population, original_population);
    }

    protected static List<Object[]> getTestCases(final IPopulation... populations) {

        final List<Object[]> result = new ArrayList<>();

        for (final IPopulation population : populations) {
            result.add(new Object[]{population});
        }
        return result;
    }

    private static void assertSamePersonAttributes(final IPopulation population, final IPopulation original_population) {

        for (final IPerson person1 : population.getPeople()) {
            final IPerson person2 = original_population.findPerson(person1.getId());

            assertSamePersonAttributes(person1, person2);
        }
    }

    private static void assertSamePartnershipAttributes(final IPopulation population, final IPopulation original_population) {

        for (final IPartnership partnership1 : population.getPartnerships()) {

            final IPartnership partnership2 = original_population.findPartnership(partnership1.getId());

            assertSamePartnershipAttributes(partnership1, partnership2);
        }
    }

    private static void assertSamePersonAttributes(final IPerson person1, final IPerson person2) {

        assertEquals(person1.getSex(), person2.getSex());
        assertNotNull(person1.getBirthDate());
        assertEqualDates(person1.getBirthDate(), person2.getBirthDate());
        assertEqualDates(person1.getDeathDate(), person2.getDeathDate());
        assertEquals(person1.getParentsPartnership(), person2.getParentsPartnership());

        assertEquals(person1.getBirthPlace(), person2.getBirthPlace());
        assertEquals(person1.getDeathPlace(), person2.getDeathPlace());
        assertEquals(person1.getDeathCause(), person2.getDeathCause());
        assertEquals(person1.getOccupation(), person2.getOccupation());
    }

    private static void assertEqualDates(final Date date1, final Date date2) {

        if (date1 != null) {

            assertNotNull(date2);
            assertEquals(DateManipulation.formatDate(date1), DateManipulation.formatDate(date2));
        }
    }

    private static void assertSamePartnershipAttributes(final IPartnership partnership1, final IPartnership partnership2) {

        assertEquals(partnership1.getMalePartnerId(), partnership2.getMalePartnerId());
        assertEquals(partnership1.getFemalePartnerId(), partnership2.getFemalePartnerId());
        assertEqualSets(partnership1.getChildIds(), partnership2.getChildIds());
        assertEqualDates(partnership1.getMarriageDate(), partnership2.getMarriageDate());
    }

    private static void assertEqualSets(final List<Integer> set1, final List<Integer> set2) {

        if (set1 == null) {
            assertNull(set2);

        } else {
            assertNotNull(set2);
            assertEquals(set1.size(), set2.size());

            for (final int i : set1) {
                assertTrue(set2.contains(i));
            }
        }
    }

    private static void assertSamePeopleIds(final Iterable<IPerson> people1, final Iterable<IPerson> people2) {

        final Set<Integer> ids = new HashSet<>();
        for (final IPerson person : people1) {
            ids.add(person.getId());
        }

        for (final IPerson person : people2) {
            assertTrue(ids.contains(person.getId()));
        }
    }

    private static void assertSamePartnershipIds(final Iterable<IPartnership> partnerships1, final Iterable<IPartnership> partnerships2) {

        final Set<Integer> ids = new HashSet<>();
        for (final IPartnership partnership : partnerships1) {
            ids.add(partnership.getId());
        }

        for (final IPartnership partnership : partnerships2) {
            assertTrue(ids.contains(partnership.getId()));
        }
    }
}
