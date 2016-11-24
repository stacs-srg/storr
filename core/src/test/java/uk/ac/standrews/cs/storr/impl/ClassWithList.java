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
public class ClassWithList extends LXP {

        @LXP_SCALAR(type = LXPBaseType.INT)
        public static final String fieldname1 = "ORIGINAL_ID";

        @LXP_LIST(type = LXPBaseType.INT)
        public static final String fieldname2 = "MY_LIST";

        public ClassWithList(long persistent_Object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
                super(persistent_Object_id, reader);
        }

        public ClassWithList(int id, List<Integer> list ) {
                this.put( fieldname1, id );
                this.put( fieldname2, list );
        }
}
