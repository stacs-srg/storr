package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;


/**
 * Created by al on 20/06/2014.
 */
public interface IReferenceType extends IType {

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    java.util.Collection<String> getLabels();

    public IType getFieldType(String label) throws KeyNotFoundException;

    public int getId();

    ILXP getRep();
}
