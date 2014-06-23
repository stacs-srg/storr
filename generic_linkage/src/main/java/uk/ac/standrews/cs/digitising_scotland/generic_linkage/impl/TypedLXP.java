package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ITypedLXP;

/**
 * Created by al on 20/06/2014.
 */
public class TypedLXP extends LXP implements ITypedLXP  {
    @Override
    public void put(String key, int value) {

        // TODO need checks in here
        super.put(key,Integer.toString(value));

    }

    @Override
    public int getInt(String key) throws NumberFormatException {

        // TODO need checks in here

        return Integer.parseInt(super.get(key));
    }

    @Override
    public void putRef(String key, int id) {

        // TODO need checks in here
        super.put(key,Integer.toString(id));

    }

    @Override
    public int getRef(String key) {
        // TODO need checks in here

        return Integer.parseInt(super.get(key));
    }

    @Override
    public ILXP getReferend(String key) {

        int ref = getRef(key);
        // make into an ILXP by lookup in store!
        return null;
    }

    @Override
    public void put(String key, float value) {
        // TODO need checks in here

        super.put(key,Float.toString(value));
    }

    @Override
    public float getFloat(String key) {
        // TODO need checks in here

        return Float.parseFloat(super.get(key));
    }
}
