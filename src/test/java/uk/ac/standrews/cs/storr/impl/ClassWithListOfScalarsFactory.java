package uk.ac.standrews.cs.storr.impl;


import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.Types;

import java.util.List;

/**
 * Created by al on 03/10/2014.
 */
public class ClassWithListOfScalarsFactory extends TFactory<ClassWithListOfScalars> implements ILXPFactory<ClassWithListOfScalars> {


    public ClassWithListOfScalarsFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public ClassWithListOfScalars create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        return new ClassWithListOfScalars(persistent_object_id, reader, repository, bucket);
    }

    public ClassWithListOfScalars create(int id, List<Integer> list) {
        ClassWithListOfScalars result = new ClassWithListOfScalars(id, list);
        result.put(Types.LABEL, required_type_labelID);
        return result;
    }

}
