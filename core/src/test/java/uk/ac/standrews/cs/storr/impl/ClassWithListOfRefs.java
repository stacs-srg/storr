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
public class ClassWithListOfRefs extends LXP {

        @LXP_SCALAR(type = LXPBaseType.INT)
        public static final String some_int = "R_INT";

        @LXP_LIST(reftype = "lxp")
        public static final String alist = "R_LIST";


        public ClassWithListOfRefs(long persistent_Object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
                super(persistent_Object_id, reader);
        }

        public ClassWithListOfRefs(int id, List<LXP> list ) {
                this.put( some_int, id );
                this.put( alist, list );
        }
}
