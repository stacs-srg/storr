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

/**
 * Created by al on 20/06/2014.
 * A class representing types that may be encoded above OID storage layer (optional)
 * Mirrors types used in @class java.sql.Types
 */
public enum LXPBaseType implements IType {

    STRING {
        @Override
        public boolean valueConsistentWithType(final Object value) {
            return value instanceof String;
        }
    },

    INT {
        @Override
        public boolean valueConsistentWithType(final Object value) {
            return value instanceof Integer;
        }
    },

    LONG {
        @Override
        public boolean valueConsistentWithType(final Object value) {

            return value instanceof Long || value instanceof Integer; // permit int<>long coorecion.
        }
    },

    DOUBLE {
        @Override
        public boolean valueConsistentWithType(final Object value) {

            return value instanceof Double || value instanceof Float;
        }
    },

    BOOLEAN {
        @Override
        public boolean valueConsistentWithType(final Object value) {
            return value instanceof Boolean;
        }
    },

    UNKNOWN {
        @Override
        public boolean valueConsistentWithType(final Object value) {
            throw new RuntimeException("Encountered UNKNOWN type whilst checking field contents");
        }
    }
}
