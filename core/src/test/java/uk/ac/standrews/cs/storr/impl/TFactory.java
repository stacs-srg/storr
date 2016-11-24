package uk.ac.standrews.cs.storr.impl;


import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.types.Types;

/**
 * Created by al on 03/10/2014.
 */
public abstract class TFactory<T extends ILXP> implements ILXPFactory<T> {

    protected long required_type_labelID;

    /*
     * This says that we can we can create an instance of this type iff the labels supplied in the label_id are present
     */
    @Override
    public boolean checkConsistentWith(long label_id) {
        return Types.checkLabelsConsistentWith(label_id, required_type_labelID);
    }

    @Override
    public long getTypeLabel() {
        return required_type_labelID;
    }

}
