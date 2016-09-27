package uk.ac.standrews.cs.jstore.interfaces;

/**
 * This interface is used to encode all type information about LXPs and the fields associated with labels.
 *
 *  For example an OID of the following form:
 * [name: "al", age: 55]
 *
 * would be represented as a reference type [name: string, age: int]
 * Which has the labels {"name","age"}. The field type of field "name" would return the rep
 * of int (encoded as a @class LXPBaseType(INT).
 * This information is encoded as an OID of the form shown above i.e.: [name: string, age: int]

 * Created by al on 31/10/14.
 */
public interface IType {

    /**
     * @param value - a value to check
     * @return true if the value is consistent with the implementing type.
     * For example for a type INT (implemented as @class LXPBaseType),
     * a @call valueConsistentWithType( 7 ) will yield true whereas
     * valueConsistentWithType( 7.3 ) will yield false.
     */
    public boolean valueConsistentWithType(Object value);

}
