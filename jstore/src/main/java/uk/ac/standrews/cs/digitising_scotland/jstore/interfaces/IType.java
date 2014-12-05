package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

/**
 * This interface is used to encode all type information about LXPs and the fields associated with labels.
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
    public boolean valueConsistentWithType(String value);

}
