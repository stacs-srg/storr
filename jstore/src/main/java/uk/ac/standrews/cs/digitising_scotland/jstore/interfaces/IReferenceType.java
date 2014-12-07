package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;


/**
 * This is used to represent a type of an LXP - for example an LXP of the following form:
 * [name: "al", age: 55] would be represented as a reference type [name: string, age: int]
 * Which has the labels {"name","age"}. The field type of field "name" would return the rep
 * of int (encoded as a @class LXPBaseType(INT).
 * This information is encoded as an LXP of the form shown above i.e.: [name: string, age: int]
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
     * @throws KeyNotFoundException
     */
    public IType getFieldType(String label) throws KeyNotFoundException;

    /**
     * @return the id of this typerep - this is the id of the underlying rep implementation.
     */
    public long getId();

    /**
     * @return the LXP used to encode the reference type - e.g. [name: string, age: int]
     */
    ILXP getRep();
}
