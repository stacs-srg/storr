package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.TypedLXPException;

/**
 * Created by al on 20/06/2014.
 */
public interface ITypedLXP extends ILXP {

    // Interfaces for all the Types supported by @link uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type

    void put(String key, int value) throws TypedLXPException;

    int getInt(String key) throws TypedLXPException;

    void putRef(String key, int id) throws TypedLXPException;

    int getRef(String key) throws TypedLXPException;

    ILXP getReferend(String key) throws TypedLXPException;

    void put(String key, float value) throws TypedLXPException;

    float getFloat(String key) throws TypedLXPException;
}
