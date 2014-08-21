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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.Distribution;

/**
 * The interface for Temporal Distributions.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 * @param <Value> The specified value type to be used by the distribution.
 */
public interface ITemporalDistribution<Value> extends Distribution<Value> {

    /**
     * Method returns a Value from the distribution for the specified date.
     * 
     * @param date The year of the distribution to be sampled.
     * @return The Value returned from the distibution.
     */
    Value getSample(int date);

}
