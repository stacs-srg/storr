package uk.ac.standrews.cs.digitising_scotland.linkage.factory;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class BirthFactory extends TFactory<Birth> implements ILXPFactory<Birth> {


    public BirthFactory(int birthlabelID ) {
        this.required_type_labelID = birthlabelID;
    }

    @Override
    public Birth create(int label_id, JSONReader reader) throws PersistentObjectException {
        return new Birth( label_id, reader );
    }

    @Override
    public Birth convert(ILXP base) {
        return null; // TODO write me.
    }


}
