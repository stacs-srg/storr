package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Relationship extends AbstractLXP {

    public Relationship() {
        super();
    }

    public Relationship(int label_id, JSONReader reader) throws PersistentObjectException {

        super(Store.getInstance().getNextFreePID(), reader );

    }


}
