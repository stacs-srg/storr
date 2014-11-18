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
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple tests that check that the toString values of the vectors created are what we would expect.
 * Created by fraserdunlop on 28/04/2014 at 10:52.
 */
public class VectorFactoryTest {

    /**
     * Tests creating a vector from a string.
     */
    @Test
    public void testCreateVectorFromString() {

        VectorFactory vectorFactory = new VectorFactory();
        vectorFactory.updateDictionary("The quick brown fox jumped over the lazy dog");
        Vector testVector = vectorFactory.createVectorFromString("The quick brown fox jumped over the lazy dog");
        Assert.assertEquals("{0:2.0,2:1.0,1:1.0,5:1.0,7:1.0,6:1.0,3:1.0,4:1.0}", testVector.toString());
        Vector testVector2 = vectorFactory.createVectorFromString("The Brown dog jumped");
        Assert.assertEquals("{0:1.0,2:1.0,4:1.0,7:1.0}", testVector2.toString());
    }
}
