package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above LXP storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum LXPBaseType implements IType {  // TODo get rid of all this shit and put the value matching code from Types into here.

    UNKNOWN {
        @Override
        public boolean valueConsistentWithType(String value) {
            ErrorHandling.error("Encountered UNKNOWN type whilst checking field contents");
            return false;

        }
    },
    STRING {
        @Override
        public boolean valueConsistentWithType(String value) {
            return true;   // everything is string encoded in JSON.
        }
    },
    INT {
        @Override
        public boolean valueConsistentWithType(String value) {
            try {
                Integer i = Integer.valueOf(value);
                // we got have an int - all OK.
                return true;
            } catch (NumberFormatException e) {
                // it wasn't an int value;
                return false;
            }
        }
    },
    FLOAT {
        @Override
        public boolean valueConsistentWithType(String value) {
            try {
                Float f = Float.valueOf(value);
                // we got have a float - all OK.
                return true;
            } catch (NumberFormatException e) {
                // it wasn't a float value;
                return false;
            }
        }
    },
    LXP {
        @Override
        public boolean valueConsistentWithType(String value) {
            Integer id = Integer.valueOf(value);  // must be a reference to a record of appropriate type
            ILXP record = null;
            try {
                IBucket bucket = Store.getInstance().getObjectCache().getBucket(id);
                if (bucket == null) { // didn't find the bucket
                    return false;
                }
                record = bucket.get(id);
                if (record == null) { // we haven't found that record in the store
                    return false;
                }
            } catch (BucketException e) {
                ErrorHandling.exceptionError(e, "Recovering record type");
                return false;
            }
            return true;
        }
    }

//    UNKNOWN {
//        @Override
//        public boolean isReferenceType() {
//            return false;
//        }
//
//        @Override
//        public boolean isBaseType() {
//            return true;
//        }
//
//        @Override
//        public LXPReferenceType getReferenceType() {
//            return null;
//        }
//
//        @Override
//        public LXPBaseType getBaseType() {
//            return LXPBaseType.UNKNOWN;
//
//        }
//    },
//    STRING {
//        @Override
//        public boolean isReferenceType() {
//            return false;
//        }
//
//        @Override
//        public boolean isBaseType() {
//            return true;
//        }
//
//        @Override
//        public LXPReferenceType getReferenceType() {
//            return null;
//        }
//
//        @Override
//        public LXPBaseType getBaseType() {
//            return LXPBaseType.STRING;
//        }
//    },
//    INT {
//        @Override
//        public boolean isReferenceType() {
//            return false;
//        }
//
//        @Override
//        public boolean isBaseType() {
//            return true;
//        }
//
//        @Override
//        public LXPReferenceType getReferenceType() {
//            return null;
//        }
//
//        @Override
//        public LXPBaseType getBaseType() {
//            return LXPBaseType.INT;
//        }
//    },
//    FLOAT {
//        @Override
//        public boolean isReferenceType() {
//            return false;
//        }
//
//        @Override
//        public boolean isBaseType() {
//            return true;
//        }
//
//        @Override
//        public LXPReferenceType getReferenceType() {
//            return null;
//        }
//
//        @Override
//        public LXPBaseType getBaseType() {
//            return LXPBaseType.FLOAT;
//        }
//    },
//    LXP {
//        @Override
//        public boolean isReferenceType() {
//            return false;
//        }
//
//        @Override
//        public boolean isBaseType() {
//            return true;
//        }
//
//        @Override
//        public LXPReferenceType getReferenceType() {
//            return null;
//        }
//
//        @Override
//        public LXPBaseType getBaseType() {
//            return LXPBaseType.LXP;
//        }
//    };


}
