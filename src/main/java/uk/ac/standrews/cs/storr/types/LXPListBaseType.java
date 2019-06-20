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

import uk.ac.standrews.cs.storr.interfaces.IType;

import java.util.List;

/**
 * Created by al on 22/1//2016
 * A class representing types that may be encoded above OID storage layer (optional)
 * Represents lists of base types
 */
public enum LXPListBaseType implements IType {

    UNKNOWN {
        @Override
        public boolean valueConsistentWithType(Object value) {
            throw new RuntimeException("Encountered ARRAY OF UNKNOWN type whilst checking field contents");
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


