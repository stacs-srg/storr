package uk.ac.standrews.cs.storr.types;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.interfaces.IType;

import java.util.List;

/**
 * Created by al on 22/1//2016
 * A class representing types that may be encoded above OID storage layer (optional)
 * Represents lists of reference types
 */
public class LXPListRefType implements IType {

    IReferenceType contents_type;  // AL IS HERE

    public LXPListRefType(IReferenceType list_contents_type) {
        this.contents_type = list_contents_type;
    }

    public boolean valueConsistentWithType(Object value) {
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                return true; // cannot check contents due to type erasure - and is empty so OK.
            } else {
                // Need to check the contents of the list are type compatible with expected type.
                for( Object o : list ) {
                    LXP record = (LXP) o;
                    if (this.equals(TypeFactory.getInstance().typeWithName("lxp"))) { // if we just require an lxp don't do more structural checking.
                        // all Lxp types match
                        return true;
                    } else {
                        if( ! Types.check_structural_consistency(record, contents_type) ) {
                            return false;
                        }
                    }
                }
                // everything checked out
                return true;
            }
        } else {
            return false;
        }
    }
}


