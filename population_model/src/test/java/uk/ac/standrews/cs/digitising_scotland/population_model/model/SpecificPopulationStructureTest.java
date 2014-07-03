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
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * Tests of properties of abstract population interface that hold for specific cases.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SpecificPopulationStructureTest extends PopulationStructureTest {

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
    public void familyStructureIsAsExpected() throws IOException, InconsistentWeightException, ParseException {

        IPopulation population = (IPopulation) populationWithTwoFamilies()[0];

        final int fatherA_id = 1;
        final int motherA_id = 2;
        final int child1A_id = 3;
        final int child2A_id = 4;

        final int fatherB_id = 5;
        final int motherB_id = 6;
        final int child1B_id = 7;
        final int child2B_id = 8;

        // Check that father of family A has one partnership.
        IPerson fatherA = population.findPerson(fatherA_id);
        List<Integer> partnershipA_ids = fatherA.getPartnerships();
        assertEquals(1, partnershipA_ids.size());

        // Check that partnership points to father and mother of family A.
        int partnershipA_id = partnershipA_ids.get(0);
        IPartnership partnershipA = population.findPartnership(partnershipA_id);
        assertEquals(partnershipA.getPartner1Id(), fatherA_id);
        assertEquals(partnershipA.getPartner2Id(), motherA_id);

        // Check that the family has the expected two children.
        List<Integer> child_ids_partnershipA = partnershipA.getChildren();
        assertEquals(2, child_ids_partnershipA.size());
        assertEquals(child1A_id, (int) child_ids_partnershipA.get(0));
        assertEquals(child2A_id, (int) child_ids_partnershipA.get(1));

        // Check that father of family B has one partnership.
        IPerson fatherB = population.findPerson(fatherB_id);
        List<Integer> partnershipB_ids = fatherB.getPartnerships();
        assertEquals(1, partnershipB_ids.size());

        // Check that partnership points to father and mother of family B.
        // The order of the partners within the partnership is as originally created.
        int partnershipB_id = partnershipB_ids.get(0);
        IPartnership partnershipB = population.findPartnership(partnershipB_id);
        assertEquals(partnershipB.getPartner1Id(), motherB_id);
        assertEquals(partnershipB.getPartner2Id(), fatherB_id);

        // Check that the family has the expected two children.
        List<Integer> child_ids_partnershipB = partnershipB.getChildren();
        assertEquals(2, child_ids_partnershipB.size());
        assertEquals(child1B_id, (int) child_ids_partnershipB.get(0));
        assertEquals(child2B_id, (int) child_ids_partnershipB.get(1));
    }
}
