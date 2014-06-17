package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors;

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
