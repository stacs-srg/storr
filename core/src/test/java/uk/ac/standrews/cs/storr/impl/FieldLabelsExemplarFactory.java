package uk.ac.standrews.cs.storr.impl;


import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;

/**
 * Created by al on 03/10/2014.
 */
public class FieldLabelsExemplarFactory extends TFactory<ClassWithList> implements ILXPFactory<ClassWithList> {


    public FieldLabelsExemplarFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public ClassWithList create(long persistent_object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
        return new ClassWithList(persistent_object_id, reader);
    }


}
