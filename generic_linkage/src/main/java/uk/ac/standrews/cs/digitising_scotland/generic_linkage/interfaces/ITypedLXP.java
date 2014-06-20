package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Created by al on 20/06/2014.
 */
public interface ITypedLXP extends ILXP {

    // Interfaces for all the TYpes supported by @link uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type

    void put(String key, int value);

    int getInt(String key) throws NumberFormatException;

    void putRef(String key, int id);

    int getRef(String key);

    ILXP getReferend(String key);

    void put(String key, float value);

    float getFloat(String key);
}
