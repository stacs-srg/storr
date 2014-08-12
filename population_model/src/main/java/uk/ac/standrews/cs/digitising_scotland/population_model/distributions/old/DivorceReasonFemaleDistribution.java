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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.old;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.DivorceReason;

public class DivorceReasonFemaleDistribution implements Distribution<DivorceReason> {
    /*
     * from numberofdivorcesageatdivorceandmaritalstatusbeforemarriage_tcm77-351699
     * from http://www.ons.gov.uk/ons/rel/vsob1/divorces-in-england-and-wales/2012/rtd-divorces---number-of-divorces-age-at-divorce-and-marital-status-before-marriage.xls
     * Divorces granted to a sole party: Party to whom granted and fact proven at divorce, 1974-2012
     * 
     * Data for the year 2012
     * 
     * Reason      Divorces per 100
     * Adultery        14
     * Behaviour       54
     * Desertion       1
     * Seperation      22
     *   with consent
     * Seperation      9
     */

    private static final int[] REASON_DISTRIBUTION_WEIGHTS = new int[]{14, 54, 1, 22, 9};

    private final WeightedIntegerDistribution distribution;

    /**
     * Creates a divorce reason by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public DivorceReasonFemaleDistribution(final Random random) {
        try {
            distribution = new WeightedIntegerDistribution(0, 4, REASON_DISTRIBUTION_WEIGHTS, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    /**
     * Returns the reason for divorce.
     *
     * @return Indicates the gender of the instigator or no divorce
     */
    @Override
    public DivorceReason getSample() {

        switch ((int) distribution.getSample()) {
            case 0:
                return DivorceReason.ADULTERY;
            case 1:
                return DivorceReason.BEHAVIOUR;
            case 2:
                return DivorceReason.DESERTION;
            case 3:
            	return DivorceReason.SEPARATION_WITH_CONSENT;
            case 4:
            	return DivorceReason.SEPARATION;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }
}
