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

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by graham on 16/07/2014.
 */
public class CompactPartnershipTest {

    @Test
    public void getPartner() {

        final int partner1_index = 2;
        final int partner2_index = 3;

        final CompactPartnership p = new CompactPartnership(partner1_index, partner2_index, -1);

        assertEquals(partner2_index, p.getPartner(partner1_index));
        assertEquals(partner1_index, p.getPartner(partner2_index));
        assertEquals(-1, p.getPartner(4));
    }

    @Test
    public void compareTo() {

        final CompactPartnership p1 = new CompactPartnership(0, 0, 5);
        final CompactPartnership p2 = new CompactPartnership(0, 0, 7);
        final CompactPartnership p3 = new CompactPartnership(0, 0, 7);

        assertTrue(p1.compareTo(p2) < 0);
        assertTrue(p1.compareTo(p3) < 0);

        assertTrue(p2.compareTo(p1) > 0);
        assertTrue(p2.compareTo(p3) < 0);

        assertTrue(p3.compareTo(p1) > 0);
        assertTrue(p3.compareTo(p2) > 0);
    }

    @Test
    public void equals() {

        final CompactPartnership p1 = new CompactPartnership(0, 0, 5);
        final CompactPartnership p2 = new CompactPartnership(0, 0, 7);
        final CompactPartnership p3 = new CompactPartnership(0, 0, 7);

        assertFalse(p1.equals(p2));
        assertFalse(p1.equals(p3));

        assertFalse(p2.equals(p1));
        assertFalse(p2.equals(p3));

        assertFalse(p3.equals(p1));
        assertFalse(p3.equals(p2));
    }
}
