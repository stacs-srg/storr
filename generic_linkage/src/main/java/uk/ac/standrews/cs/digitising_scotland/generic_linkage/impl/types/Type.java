package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above LXP storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum Type {
     UNKNOWN, STRING, INT, FLOAT ,REFERENCE;

    public static Type SringToType(String value) {
        if( STRING.name().equals(value) ) {
            return STRING;
        }
        if( INT.name().equals(value) ) {
            return INT;
        }
        if( FLOAT.name().equals(value) ) {
            return FLOAT;
        }
        if( REFERENCE.name().equals(value) ) {
            return REFERENCE;
        }
        return UNKNOWN;
    }

}
