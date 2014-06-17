package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors;


import org.apache.mahout.math.Vector;

/**
 * An abstract class outlining what vector encoders should do.
 * Created by fraserdunlop on 28/04/2014 at 10:01.
 */
public abstract class AbstractVectorEncoder {
    /**
     * Should take a (String) token and encode it into a vector.
     * @param token the token to encode.
     * @param vector the vector to encode the token into.
     */
    public abstract void addToVector(final String token, final Vector vector);

    /*
     * allows resetting of the internal dictionary for testing purposes
     */
    protected abstract void reset();
}
