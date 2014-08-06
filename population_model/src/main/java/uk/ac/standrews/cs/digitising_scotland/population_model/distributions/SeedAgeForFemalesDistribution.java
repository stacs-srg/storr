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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;

import java.util.Random;

/**
 * Provides a distribution of ages for female seed population at start date.
 * 
 * @author Tom Dalton (tsd4@st-andrews.co.uk)
 */
public class SeedAgeForFemalesDistribution implements Distribution<Integer> {

    private static final int MINIMUM_AGE_IN_YEARS = 0;
    private static final int MAXIMUM_AGE_IN_YEARS = 100;

    @SuppressWarnings("MagicNumber")
    private static final int[] AGE_DISTRIBUTION_WEIGHTS = new int[]{1076, 1099, 1128, 1149, 1179, 1170, 1172, 1202, 1217, 1257, 1275, 1254, 1242, 1256, 1226, 1207, 1211, 1169, 1150, 1155, 1210, 1218, 1180, 1136, 1146, 1182, 1233, 1285, 1365, 1448, 1491, 1478, 1521, 1534, 1567, 1575, 1582, 1569, 1543, 1504, 1463, 1405, 1377, 1358, 1316, 1272, 1243, 1253, 1239, 1229, 1236, 1276, 1314, 1413, 1486, 1214, 1188, 1158, 1110, 1004, 945, 983, 987, 976, 953, 930, 903, 873, 873, 878, 887, 872, 844, 813, 808, 796, 774, 756, 738, 746, 753, 707, 504, 414, 426, 414, 404, 362, 315, 265, 224, 185, 151, 117, 88, 65, 48, 35, 24, 18, 26};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates an age at divorce for males distribution.
     *
     * @param random The random number generator to be used.
     */
    public SeedAgeForFemalesDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution((int) (MINIMUM_AGE_IN_YEARS * PopulationLogic.DAYS_PER_YEAR), (int) (MAXIMUM_AGE_IN_YEARS * PopulationLogic.DAYS_PER_YEAR) - 1, AGE_DISTRIBUTION_WEIGHTS, random);

        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    @Override
    public Integer getSample() {
        return distribution.getSample();
    }
}
