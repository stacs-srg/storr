package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above LXP storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum LXPBaseType implements IType {
    UNKNOWN {
        @Override
        public boolean isReferenceType() {
            return false;
        }

        @Override
        public boolean isBaseType() {
            return true;
        }

        @Override
        public LXPReferenceType getReferenceType() {
            return null;
        }

        @Override
        public LXPBaseType getBaseType() {
            return LXPBaseType.UNKNOWN;

        }
    },
    STRING {
        @Override
        public boolean isReferenceType() {
            return false;
        }

        @Override
        public boolean isBaseType() {
            return true;
        }

        @Override
        public LXPReferenceType getReferenceType() {
            return null;
        }

        @Override
        public LXPBaseType getBaseType() {
            return LXPBaseType.STRING;
        }
    },
    INT {
        @Override
        public boolean isReferenceType() {
            return false;
        }

        @Override
        public boolean isBaseType() {
            return true;
        }

        @Override
        public LXPReferenceType getReferenceType() {
            return null;
        }

        @Override
        public LXPBaseType getBaseType() {
            return LXPBaseType.INT;
        }
    },
    FLOAT {
        @Override
        public boolean isReferenceType() {
            return false;
        }

        @Override
        public boolean isBaseType() {
            return true;
        }

        @Override
        public LXPReferenceType getReferenceType() {
            return null;
        }

        @Override
        public LXPBaseType getBaseType() {
            return LXPBaseType.FLOAT;
        }
    },
    LXP {
        @Override
        public boolean isReferenceType() {
            return false;
        }

        @Override
        public boolean isBaseType() {
            return true;
        }

        @Override
        public LXPReferenceType getReferenceType() {
            return null;
        }

        @Override
        public LXPBaseType getBaseType() {
            return LXPBaseType.LXP;
        }
    };


}
