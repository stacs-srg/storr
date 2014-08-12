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


/**
 * Created by graham on 11/08/2014.
 */
public abstract class DivorceReasonDistribution implements Distribution<DivorceReason> {

    protected final WeightedIntegerDistribution distribution;

    /**
     * Creates a divorce reason distribution.
     *
     * @param random the random number generator to be used
     */
    public DivorceReasonDistribution(final Random random, final int[] reason_distribution_weights) {
        try {
            distribution = new WeightedIntegerDistribution(0, 4, reason_distribution_weights, random);
        } catch (final NegativeWeightException e) {
            throw new RuntimeException("negative weight exception: " + e.getMessage());
        }
    }

    /**
     * Returns the reason for divorce.
     *
     * @return the reason for divorce
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