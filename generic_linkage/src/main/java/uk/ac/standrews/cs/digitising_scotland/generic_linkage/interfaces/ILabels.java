package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;

import java.util.List;

/**
 * Created by al on 20/06/2014.
 */
public interface ILabels {

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    List<String> getLabels();

    public Type getType(String label);
}
