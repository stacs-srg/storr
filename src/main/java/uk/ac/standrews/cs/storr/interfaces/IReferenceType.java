package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;


/**
 * Created by al on 20/06/2014.
 */
public interface IReferenceType extends IType {

    /**
     * @return the labels present in the type.
     * For example for a type [name: string, age: int] would return {name,age}
     */
    java.util.Collection<String> getLabels();

    /**
     * @param label - the label whose type is being looked up
     * @return the field type associated with the specified label
     * e.g. for a type [name: string, age: int] and the label "name" this method would return the
     * rep for @class LXPBaseType(INT).
     * @throws KeyNotFoundException if the key is not found
     */
    public IType getFieldType(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * @return the id of this typerep - this is the id of the underlying rep implementation.
     */
    public long getId();

    /**
     * @return the OID used to encode the reference type - e.g. [name: string, age: int]
     */
    ILXP getRep();
}
