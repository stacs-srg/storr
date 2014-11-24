package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;

/**
 * Created by al on 03/10/2014.
 */
public abstract class TFactory<T extends ILXP> implements ILXPFactory<T> {

    protected int required_type_labelID;

    /*
     * This says that we can we can create an instance of this type iff the labels supplied in the label_id are present
     */
    @Override
    public boolean checkConsistentWith(int label_id) {
        return Types.checkLabelsConsistentWith(label_id, required_type_labelID);
    }

    @Override
    public int getTypeLabel() {
        return required_type_labelID;
    }

}
