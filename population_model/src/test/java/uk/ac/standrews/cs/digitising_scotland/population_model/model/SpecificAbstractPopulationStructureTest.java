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
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * Tests of properties of abstract population interface that hold for specific cases.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SpecificAbstractPopulationStructureTest extends PopulationStructureTest {

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
    public void familyStructure() throws IOException, InconsistentWeightException {

        IPopulation population = (IPopulation) populationWithTwoFamilies()[0];

        IPerson fatherA = population.findPerson(1);
        List<Integer> partnership1_ids = fatherA.getPartnerships();
        assertEquals(1, partnership1_ids.size());

        int partnership1_id = partnership1_ids.get(0);

        IPartnership partnership1 = population.findPartnership(partnership1_id);
        assertEquals(partnership1.getPartner1Id(), 1);
        assertEquals(partnership1.getPartner2Id(), 2);

        List<Integer> child_ids_partnership1 = partnership1.getChildren();
        assertEquals(2, child_ids_partnership1.size());
        assertEquals(3, (int) child_ids_partnership1.get(0));
        assertEquals(4, (int) child_ids_partnership1.get(1));

        IPerson fatherB = population.findPerson(5);
        List<Integer> partnership2_ids = fatherB.getPartnerships();
        assertEquals(1, partnership2_ids.size());

        int partnership2_id = partnership2_ids.get(0);

        IPartnership partnership2 = population.findPartnership(partnership2_id);
        assertEquals(partnership2.getPartner1Id(), 6);
        assertEquals(partnership2.getPartner2Id(), 5);

        List<Integer> child_ids_partnership2 = partnership2.getChildren();
        assertEquals(2, child_ids_partnership2.size());
        assertEquals(7, (int) child_ids_partnership2.get(0));
        assertEquals(8, (int) child_ids_partnership2.get(1));
    }
}
