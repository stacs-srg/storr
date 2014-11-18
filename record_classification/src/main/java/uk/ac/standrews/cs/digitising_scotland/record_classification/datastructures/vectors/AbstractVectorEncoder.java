/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors;

import org.apache.mahout.math.Vector;

import java.util.Collection;

/**
 * An abstract class outlining what vector encoders should do.
 * Created by fraserdunlop on 28/04/2014 at 10:01.
 */
public abstract class AbstractVectorEncoder<Feature> {

    /**
     * Should take a (String) token and encode it into a vector.
     * @param features the features to encode.
     * @param vector the vector to encode the token into.
     */
    public abstract Vector encode(final Collection<Feature> features, final Vector vector);

}
