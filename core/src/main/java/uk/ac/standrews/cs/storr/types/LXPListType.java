package uk.ac.standrews.cs.storr.types;

import uk.ac.standrews.cs.storr.interfaces.IType;
import uk.ac.standrews.cs.storr.util.ErrorHandling;

import java.util.List;

/**
 * Created by al on 22/1//2016
 * A class representing types that may be encoded above OID storage layer (optional)
 * Represents lists of base types
 */
public enum LXPListType implements IType {

    UNKNOWN {
        @Override
        public boolean valueConsistentWithType(Object value) {
            ErrorHandling.error("Encountered ARRAY OF UNKNOWN type whilst checking field contents");
            Throwable t = new Throwable("");
            t.printStackTrace();
            return false;
        }
    },
    STRING {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new String[list.size()]);
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    },
    INT {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new Integer[list.size()]);
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    },
    LONG {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new Long[list.size()]);
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    },
    DOUBLE {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new Double[list.size()]);
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    },
    BOOLEAN {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new Boolean[list.size()]);
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    },
    REF {
        @Override
        public boolean valueConsistentWithType(Object value) {
            if (value instanceof List) {
                List list = (List) value;
                if (list.isEmpty()) {
                    return true; // cannot check contents due to type erasure - and is empty so OK.
                } else {
                    try {
                        list.toArray(new Long[list.size()]); // oids are longs
                        // everything is type compatible
                        return true;
                    } catch (ArrayStoreException e) {
                        // types don't match
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    }
}


