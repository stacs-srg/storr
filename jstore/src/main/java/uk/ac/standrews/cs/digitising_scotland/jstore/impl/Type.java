package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above LXP storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum Type {
     UNKNOWN, STRING, INT, FLOAT ,REFERENCE;

    public static Type stringToType(String value) {
        if (STRING.name().toLowerCase().equals(value.toLowerCase())) {
            return STRING;
        }
        if (INT.name().toLowerCase().equals(value.toLowerCase())) {
            return INT;
        }
        if (FLOAT.name().toLowerCase().equals(value.toLowerCase())) {
            return FLOAT;
        }
        if (REFERENCE.name().toLowerCase().equals(value.toLowerCase())) {
            return REFERENCE;
        }
        return UNKNOWN;
    }

}
