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

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Tests of properties of abstract population interface that hold for specific cases.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SpecificPopulationStructureTest {

    @Test(expected = NoSuchElementException.class)
    public void getNextPersonFromEmptyPopulation() throws IOException, InconsistentWeightException {

        final IPopulation population = CompactPopulationTestCases.makePopulation(0);
        population.getPeople().iterator().next();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNextPartnershipFromEmptyPopulation() throws IOException, InconsistentWeightException {

        final IPopulation population = CompactPopulationTestCases.makePopulation(0);
        population.getPartnerships().iterator().next();
    }

    @Test
    public void findPerson() {

        IDFactory.resetId();
        final CompactPerson[] people = CompactPopulationTestCases.makePeople(10);
        final CompactPopulation population = new CompactPopulation(people, 0, 0);

        for (int i = 0; i < people.length; i++) {
            assertEquals(people[i], population.findPerson(i + 1));
        }
    }

    @Test
    public void somePartnershipsAndChildrenAreCreated() throws Exception {

        checkSomePartnershipsAndChildrenAreCreated(100);
        checkSomePartnershipsAndChildrenAreCreated(1000);
    }

    @Test
    public void familyStructureIsAsExpected() throws Exception {

        final IPopulation population = CompactPopulationTestCases.populationWithTwoFamilies();

        // Check that father of family A has one partnership.
        final IPerson fatherA = population.findPerson(CompactPopulationTestCases.fatherA_id);
        final List<Integer> partnershipA_ids = fatherA.getPartnerships();
        assertEquals(1, partnershipA_ids.size());

        // Check that partnership points to father and mother of family A.
        final int partnershipA_id = partnershipA_ids.get(0);
        final IPartnership partnershipA = population.findPartnership(partnershipA_id);

        assertPartnerIds(partnershipA.getMalePartnerId(), partnershipA.getFemalePartnerId(), CompactPopulationTestCases.fatherA_id, CompactPopulationTestCases.motherA_id);

        // Check that the family has the expected two child_ids.
        final List<Integer> child_ids_partnershipA = partnershipA.getChildIds();
        assertEquals(2, child_ids_partnershipA.size());
        assertEquals(CompactPopulationTestCases.child1A_id, (int) child_ids_partnershipA.get(0));
        assertEquals(CompactPopulationTestCases.child2A_id, (int) child_ids_partnershipA.get(1));

        // Check that the child_ids refer to the parents' partnership.
        final IPerson child1A = population.findPerson(CompactPopulationTestCases.child1A_id);
        final IPerson child2A = population.findPerson(CompactPopulationTestCases.child2A_id);
        assertEquals(partnershipA_id, child1A.getParentsPartnership());
        assertEquals(partnershipA_id, child2A.getParentsPartnership());

        // Check that father of family B has one partnership.
        final IPerson fatherB = population.findPerson(CompactPopulationTestCases.fatherB_id);
        final List<Integer> partnershipB_ids = fatherB.getPartnerships();
        assertEquals(1, partnershipB_ids.size());

        // Check that partnership points to father and mother of family B.
        // The order of the partners within the partnership is as originally created.
        final int partnershipB_id = partnershipB_ids.get(0);
        final IPartnership partnershipB = population.findPartnership(partnershipB_id);

        assertPartnerIds(partnershipB.getMalePartnerId(), partnershipB.getFemalePartnerId(), CompactPopulationTestCases.fatherB_id, CompactPopulationTestCases.motherB_id);

        // Check that the family has the expected two child_ids.
        final List<Integer> child_ids_partnershipB = partnershipB.getChildIds();
        assertEquals(2, child_ids_partnershipB.size());
        assertEquals(CompactPopulationTestCases.child1B_id, (int) child_ids_partnershipB.get(0));
        assertEquals(CompactPopulationTestCases.child2B_id, (int) child_ids_partnershipB.get(1));

        // Check that the child_ids refer to the parents' partnership.
        final IPerson child1B = population.findPerson(CompactPopulationTestCases.child1B_id);
        final IPerson child2B = population.findPerson(CompactPopulationTestCases.child2B_id);
        assertEquals(partnershipB_id, child1B.getParentsPartnership());
        assertEquals(partnershipB_id, child2B.getParentsPartnership());
    }

    private static void assertPartnerIds(final int partner1Id, final int partner2Id, final int father_id, final int mother_id) {

        assertTrue(partner1Id == father_id && partner2Id == mother_id);
    }

    private static void checkSomePartnershipsAndChildrenAreCreated(final int population_size) throws Exception {

        final IPopulation population = CompactPopulationTestCases.fullPopulation(population_size);
        assertTrue(population.getNumberOfPartnerships() > 0);
        assertTrue(numberOfChildrenIn(population) > 0);
    }

    private static int numberOfChildrenIn(final IPopulation population) {

        int count = 0;
        for (final IPartnership partnership : population.getPartnerships()) {
            final List<Integer> child_ids = partnership.getChildIds();
            if (child_ids != null) {
                count += child_ids.size();
            }
        }
        return count;
    }
}
