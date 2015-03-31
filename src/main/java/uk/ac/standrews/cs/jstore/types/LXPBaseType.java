package uk.ac.standrews.cs.jstore.types;

import uk.ac.standrews.cs.jstore.interfaces.IType;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above OID storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum LXPBaseType implements IType {

    UNKNOWN {
        @Override
        public boolean valueConsistentWithType(Object value) {
            ErrorHandling.error("Encountered UNKNOWN type whilst checking field contents");
            return false;

        }
    },
    STRING {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof String;
        }
    },
    INT {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Integer;
        }
    },
    LONG {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Long;
        }
    },
    DOUBLE {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Boolean;
        }
    },
    BOOLEAN {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Boolean;
        }
    },
    OID {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Long;
        }
    }

}
