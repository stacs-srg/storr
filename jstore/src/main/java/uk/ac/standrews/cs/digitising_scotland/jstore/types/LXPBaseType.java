package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above LXP storage layer (optional)
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
    LXP {
        @Override
        public boolean valueConsistentWithType(Object value) {
            return value instanceof Long;
        }
    }

}
