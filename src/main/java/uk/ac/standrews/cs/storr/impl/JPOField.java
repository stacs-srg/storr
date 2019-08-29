/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr-expt.
 *
 * storr-expt is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr-expt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr-expt. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package main.java.toy.test;

import java.lang.reflect.Type;

public class JPOField {
    public final String name;
    public final Type type;
    public final boolean isList;
    public final boolean isStorRef;

    public JPOField(String name, Type type, boolean isList, boolean isStorRef) {
        this.name = name;
        this.type = type;
        this.isList = isList;
        this.isStorRef = isStorRef;

    }
}

