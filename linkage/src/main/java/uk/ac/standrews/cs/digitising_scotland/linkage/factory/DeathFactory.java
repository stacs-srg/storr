package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class DeathFactory extends TFactory<Death> implements ILXPFactory<Death> {


    public DeathFactory(long deathlabelID) {
        this.required_type_labelID = deathlabelID;
    }


    @Override
    public Death create(long label_id, JSONReader reader) throws PersistentObjectException {
        return new Death(label_id, reader);
    }

}
