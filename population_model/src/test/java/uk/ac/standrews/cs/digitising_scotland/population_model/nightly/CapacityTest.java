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
package uk.ac.standrews.cs.digitising_scotland.population_model.nightly;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.test.category.Slow;

/**
 * Tests to see if we can cope with capacity.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
@Category(Slow.class)
// Test run only nightly.
public class CapacityTest {

    private static final int POPULATION_SIZE = 1000000;

    @Ignore
    @Test
    public void capacity() throws Exception {

        /*
         * Java VM arguments:
         * 
         * $ java -d64 -Xms512m -Xmx4g HelloWorld where,
         * 
         * -d64: Will enable 64-bit JVM -Xms512m: Will set initial heap size as 512 MB -Xmx4g: Will set maximum heap size as 4 GB
         */

        new CompactPopulation(POPULATION_SIZE, CompactPopulation.START_YEAR, CompactPopulation.END_YEAR, null);
    }
}
