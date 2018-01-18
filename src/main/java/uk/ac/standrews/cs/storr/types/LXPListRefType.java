/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.types;

import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.interfaces.IType;

import java.util.List;

/**
 * Created by al on 22/1//2016
 * A class representing types that may be encoded above OID storage layer (optional)
 * Represents lists of reference types
 */
public class LXPListRefType implements IType {

    private IReferenceType contents_type;
    private IStore store;

    LXPListRefType(IReferenceType list_contents_type, IStore store) {

        this.contents_type = list_contents_type;
        this.store = store;
    }

    public boolean valueConsistentWithType(Object value) {

        TypeFactory type_factory = store.getTypeFactory();

        if( value == null ) {
            return true; // permit all null values
        }
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                return true; // cannot check contents due to type erasure - and is empty so OK.
            } else {
                // Need to check the contents of the list are type compatible with expected type.
                for (Object o : list) {
                    LXP record = (LXP) o;
                    if (equals(type_factory.getTypeWithName("lxp"))) { // if we just require an lxp don't do more structural checking.
                        // all Lxp types match
                        return true;
                    } else {
                        if (!Types.checkStructuralConsistency(record, contents_type)) {
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
