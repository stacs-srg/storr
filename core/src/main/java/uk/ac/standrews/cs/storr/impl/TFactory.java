package uk.ac.standrews.cs.storr.impl;


import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;

/**
 * Created by al on 03/10/2014.
 */
public abstract class TFactory<T extends ILXP> implements ILXPFactory<T> {

    protected long required_type_labelID;

    @Override
    public long getTypeLabel() {
        return required_type_labelID;
    }
}
