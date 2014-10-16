package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Birth extends AbstractLXP {

    public Birth() {
        super();
    }

    public Birth(int persistent_Object_id, JSONReader reader, int required_label_id) throws PersistentObjectException {

        super(persistent_Object_id, reader  );

    }

}

