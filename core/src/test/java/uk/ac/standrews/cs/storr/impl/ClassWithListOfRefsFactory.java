package uk.ac.standrews.cs.storr.impl;


import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.types.Types;

import java.util.List;

/**
 * Created by al on 03/10/2014.
 */
public class ClassWithListOfRefsFactory extends TFactory<ClassWithListOfRefs> implements ILXPFactory<ClassWithListOfRefs> {


    public ClassWithListOfRefsFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public ClassWithListOfRefs create(long persistent_object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
        return new ClassWithListOfRefs(persistent_object_id, reader);
    }

     public ClassWithListOfRefs create(int id, List<LXP> list) {
         ClassWithListOfRefs result = new ClassWithListOfRefs( id,list );
         result.put(Types.LABEL,required_type_labelID );
         return result;
     }

}
