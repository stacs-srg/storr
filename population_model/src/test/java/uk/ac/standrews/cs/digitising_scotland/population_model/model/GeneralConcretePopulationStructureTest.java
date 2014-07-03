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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests of properties of concrete population that should hold for all populations.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public class GeneralConcretePopulationStructureTest {

    // TODO move to abstract tests where possible

    private CompactPopulation population;
    private final int population_size;

    @Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {

        return Arrays.asList(new Object[][]{{1}, {10}, {100}, {1000}});
    }

    public GeneralConcretePopulationStructureTest(final int population_size) {

        this.population_size = population_size;
    }

    @Before
    public void setup() throws IOException, NegativeDeviationException, NegativeWeightException {

        population = new CompactPopulation(population_size);
    }

    @Test
    public void populationSize() {

        assertEquals(population_size, population.getPeopleArray().length);
    }



    @Test
    public void noSiblingsMarried() {

        assertNoSiblingsMarried();
    }

    @Test
    public void noParentsMarriedToChildren() {

        assertNoParentsMarriedToChildren();
    }

    @Test
    public void noSameSexMarriages() {

        assertNoSameSexMarriages();
    }

    private void assertParentsHaveSensibleAgesAtChildBirth() {

        CompactPerson[] people = population.getPeopleArray();

        for (final CompactPerson p : people) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child_index : partnership.getChildren()) {
                        assertTrue(population.parentsHaveSensibleAgesAtChildBirth(partnership, people[child_index].getId()));
                    }
                }
            }
        }
    }

    private void assertNoSiblingsMarried() {

        for (final CompactPerson p : population.getPeopleArray()) {

            // Include half-siblings.
            final Set<Integer> siblings = new HashSet<>();

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child_index : partnership.getChildren()) {

                        assertNotMarriedToAnyOf(child_index, siblings);
                        siblings.add(child_index);
                    }
                }
            }
        }
    }

    private void assertNotMarriedToAnyOf(final int p, final Set<Integer> people) {

        assertTrue(notMarriedToAnyOf(p, people));
    }

    private boolean notMarriedToAnyOf(final int p, final Set<Integer> people) {

        for (final int p2 : people) {
            if (population.married(p, p2)) { return false; }
        }

        return true;
    }

    private void assertNoParentsMarriedToChildren() {

        for (final CompactPerson p : population.getPeopleArray()) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    for (final int child : partnership.getChildren()) {

                        assertNotSame(child, partnership.getPartner1());
                        assertNotSame(child, partnership.getPartner2());
                    }
                }
            }
        }
    }

    private void assertNoSameSexMarriages() {

        for (final CompactPerson p : population.getPeopleArray()) {

            if (p.getPartnerships() != null) {
                for (final CompactPartnership partnership : p.getPartnerships()) {

                    CompactPerson[] people = population.getPeopleArray();
                    assertTrue(CompactPerson.oppositeSex(people[partnership.getPartner1()], people[partnership.getPartner2()]));
                }
            }
        }
    }
}
