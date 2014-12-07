package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 14/10/2014.
 */
public abstract class AbstractLXP extends LXP {

    protected long required_type_labelID;

    public AbstractLXP() {

        super();
    }

    public AbstractLXP(long object_id) {

        super(object_id);
    }

    public AbstractLXP(long object_id, JSONReader reader) throws PersistentObjectException {
        super(object_id, reader);
    }

    public AbstractLXP(JSONReader reader) throws PersistentObjectException {
        super(reader);
    }

    /*
     * This says that we can we can create an instance of this type iff the labels supplied in the label_id are present
     */
    @Override
    public boolean checkConsistentWith(long label_id) {

        return Types.checkLabelsConsistentWith(label_id, required_type_labelID);

    }

}
