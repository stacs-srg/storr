package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Death extends LXP {

    public Death() {
        super();
    }

    public Death(int label_id, JSONReader reader) throws PersistentObjectException {

        super(Store.getInstance().getNextFreePID(), reader, label_id );

    }

    public boolean checkConsistentWith(int label_id) {
        return false; // TODO AL IS HERE
    }
}
