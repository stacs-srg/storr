package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Type;


/**
 * Created by al on 20/06/2014.
 */
public interface ITypeLabel {

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    java.util.Collection<String> getLabels();

    public Type getFieldType(String label) throws KeyNotFoundException;

    public int getId();
}
