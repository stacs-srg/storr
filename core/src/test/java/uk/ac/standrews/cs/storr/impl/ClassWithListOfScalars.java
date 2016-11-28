package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

import java.util.List;

/**
 * Created by al on 22/11/2016.
 */
public class ClassWithListOfScalars extends LXP {

        @LXP_SCALAR(type = LXPBaseType.INT)
        public static final String some_int = "S_INT";

        @LXP_LIST(basetype = LXPBaseType.INT)
        public static final String alist = "S_LIST";

        public ClassWithListOfScalars(long persistent_Object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
                super(persistent_Object_id, reader);
        }

        public ClassWithListOfScalars(int id, List<Integer> list ) {
                this.put( some_int, id );
                this.put( alist, list );
        }
}
