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
