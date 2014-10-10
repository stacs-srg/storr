package uk.ac.standrews.cs.digitising_scotland.linkage.factory;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class MarriageFactory extends TFactory<Marriage> implements ILXPFactory<Marriage> {


    public MarriageFactory(int marriagelabelID) {
        this.required_type_labelID = marriagelabelID;
    }


    @Override
    public Marriage create(int label_id, JSONReader reader) throws PersistentObjectException {
        return new Marriage( label_id, reader );
    }

    @Override
    public Marriage convert(ILXP base) {
        return null; // TODO write me.
    }
}
