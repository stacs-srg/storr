package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors.SimpleVectorEncoder;

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

    private void buildDictionary(SimpleVectorEncoder encoder) {

        String[] tokens = {"apple", "banana", "carrot"};
        for (String string : tokens) {
            encoder.updateDictionary(string);
        }
    }
}
