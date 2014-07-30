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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.in_memory.CompactPopulation;
/**
 * Provides a distribution of ages for male seed population at start date.
 * 
 * @author Tom Dalton (tsd4@st-andrews.co.uk)
 */
public class MaleAgeAtSeedDistribution implements Distribution<Integer> {

    private static final int MINIMUM_AGE_IN_YEARS = 0;
    private static final int MAXIMUM_AGE_IN_YEARS = 100;
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{1177, 1211, 1242, 1262, 1295, 1288, 1293, 1323, 1339, 1380, 1404, 1381, 1366, 1382, 1346, 1331, 1346, 1295, 1258, 1230, 1276, 1283, 1240, 1185, 1191, 1242, 1295, 1340, 1428, 1505, 1547, 1537, 1576, 1591, 1633, 1631, 1634, 1617, 1587, 1546, 1507, 1455, 1427, 1408, 1363, 1315, 1281, 1290, 1278, 1264, 1270, 1308, 1353, 1456, 1537, 1250, 1222, 1195, 1141, 1025, 958, 996, 996, 986, 958, 924, 892, 847, 832, 825, 811, 777, 737, 698, 678, 648, 606, 568, 533, 516, 496, 454, 307, 237, 231, 212, 193, 162, 133, 106, 80, 63, 47, 34, 24, 17, 12, 8, 6, 4, 7};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates an age at divorce for males distribution.
     *
     * @param random The random number generator to be used.
     */
    public MaleAgeAtSeedDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution((int) (MINIMUM_AGE_IN_YEARS * CompactPopulation.DAYS_PER_YEAR), (int) (MAXIMUM_AGE_IN_YEARS * CompactPopulation.DAYS_PER_YEAR) - 1, AGE_DISTRIBUTION_WEIGHTS, random);

        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }
}
