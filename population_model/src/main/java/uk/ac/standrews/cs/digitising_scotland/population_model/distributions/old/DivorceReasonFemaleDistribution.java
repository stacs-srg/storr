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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.old;

import java.util.Random;

public class DivorceReasonFemaleDistribution extends DivorceReasonDistribution {
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
     * Separation      22
     *   with consent
     * Separation      9
     */

    @SuppressWarnings("MagicNumber")
    private static final int[] REASON_DISTRIBUTION_WEIGHTS = new int[]{14, 54, 1, 22, 9};

    /**
     * Creates a divorce reason by gender distribution.
     *
     * @param random the random number generator to be used
     */
    public DivorceReasonFemaleDistribution(final Random random) {
        super(random, REASON_DISTRIBUTION_WEIGHTS);
    }
}