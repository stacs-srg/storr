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

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple check that toString() on the vectors we encode gives us what we would expect.
 * Created by fraserdunlop on 23/04/2014 at 20:44.
 */
public class SimpleVectorEncoderTest {

    /**
     * Test that vectors can be encoded correctly by creating a vector and adding new words to that vector incrementally,
     * checking that the vector is valid after each addition.
     */
    @Test
    public void testCorrectEncoding() {

        NamedVector vector = new NamedVector(new RandomAccessSparseVector(10 ^ 6), "testVector");
        SimpleVectorEncoder encoder = new SimpleVectorEncoder();

        buildDictionary(encoder);

        encoder.addToVector("apple", vector);
        Assert.assertEquals("testVector:{0:1.0}", vector.toString());
        encoder.addToVector("banana", vector);
        Assert.assertEquals("testVector:{0:1.0,1:1.0}", vector.toString());
        encoder.addToVector("carrot", vector);
        Assert.assertEquals("testVector:{0:1.0,1:1.0,2:1.0}", vector.toString());
        encoder.addToVector("carrot", vector);
        Assert.assertEquals("testVector:{0:1.0,1:1.0,2:2.0}", vector.toString());
    }

    private void buildDictionary(final SimpleVectorEncoder encoder) {

        String[] tokens = {"apple", "banana", "carrot"};
        for (String string : tokens) {
            encoder.updateDictionary(string);
        }
    }
}
