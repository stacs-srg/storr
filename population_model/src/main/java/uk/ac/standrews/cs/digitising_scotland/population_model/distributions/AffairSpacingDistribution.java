/*
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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPartnership;

/**
 * The Affair Spacing Distribution class is a wrapper for the normal distribution. 
 * The class uses program generated values returns values representing the spacing between affairs in a given partnership.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class AffairSpacingDistribution extends NormalDistribution {
    
    private static final int STANDARD_DEVIATION_FACTOR = 4;

    /**
     * Sets up an affair spacing distribution for the given partnership. 
     * Given the use of a normal distribution will tend to given a reasonably even but non regular spacing of affairs.
     * 
     * @param partnership The partnership for which the affair spacing distribution should be created for.
     * @param random A random to be used to generate the sample value.
     * @return The constructed instance of AffairSpacingDistribution.
     */
    public static AffairSpacingDistribution affairDistributionFactory(final OrganicPartnership partnership, final Random random) {
        int midPoint = (partnership.getTimeline().getEndDate() - partnership.getTimeline().getStartDay()) / 2;
        int mean = partnership.getTimeline().getStartDay() + midPoint;
        try {
            return new AffairSpacingDistribution(mean, midPoint / STANDARD_DEVIATION_FACTOR, random);
        } catch (NegativeDeviationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AffairSpacingDistribution(final double mean, final double standard_deviation, final Random random) throws NegativeDeviationException {
        super(mean, standard_deviation, random);
    }

    /**
     * Returns an integer value representing the time in days to the next affair.
     * 
     * @return The number of days until the next affair.
     */
    public int getIntSample() {
        int temp = super.getSample().intValue();
        return temp;
    }
}