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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general;

import java.util.ArrayList;
import java.util.List;

/**
 * The restricted distribution class provided the ability for the return value of the distribution when sampled to be set to fall with a given range.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 * @param <Value> Allows for the distribution to be set up using any specified Value
 */
public abstract class RestrictedDistribution<Value> implements Distribution<Value> {

    // Restricted Distribution Helper Values
    protected Double minimumReturnValue = null;
    protected Double maximumReturnValue = null;

    protected List<Double> unusedSampleValues = new ArrayList<>();
    protected int zeroCount = -1;

    public abstract Value getSample(double earliestReturnValue, double latestReturnValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException;

    /**
     * Check if the given double d falls between the two given values.
     * 
     * @param d The double to be considered.
     * @param earliestReturnValue The smaller value.
     * @param latestReturnValue The larger value.
     * @return Boolean value of true if d falls inbetween the two given values else false.
     */
    protected static boolean inRange(final double d, final double earliestReturnValue, final double latestReturnValue) {
        return earliestReturnValue <= d && d <= latestReturnValue;
    }
}
