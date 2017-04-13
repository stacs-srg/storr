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
package uk.ac.standrews.cs.storr.interfaces;

/**
 * This interface is used to encode all type information about LXPs and the fields associated with labels.
 * <p>
 * For example an OID of the following form:
 * [name: "al", age: 55]
 * <p>
 * would be represented as a reference type [name: string, age: int]
 * Which has the labels {"name","age"}. The field type of field "name" would return the rep
 * of int (encoded as a @class LXPBaseType(INT).
 * This information is encoded as an OID of the form shown above i.e.: [name: string, age: int]
 * <p>
 * Created by al on 31/10/14.
 */
public interface IType {

    /**
     * @param value - a value to check
     * @return true if the value is consistent with the implementing type.
     * For example for a type INT (implemented as @class LXPBaseType),
     * a @call valueConsistentWithType( 7 ) will yield true whereas
     * valueConsistentWithType( 7.3 ) will yield false.
     */
    boolean valueConsistentWithType(Object value);

}
