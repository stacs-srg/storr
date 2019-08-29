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
package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.interfaces.IReferenceType;

public class PersistentMetaData {
    private IReferenceType type = null;
    private String type_name;
    protected Class metadata_class = null;

    public PersistentMetaData() {
    }

    public PersistentMetaData(final Class metadata_class, String type_name) {

        this.metadata_class = metadata_class;
        this.type_name = type_name;
    }

    private void initialiseType(final Class metadata_class) {

        final TypeFactory type_factory = Store.getInstance().getTypeFactory();

        type = type_factory.getTypeWithName(type_name);  // if created already use that one
        if (type == null) {
            type = type_factory.createType(metadata_class, type_name);  // otherwise create it
        }
    }

    public IReferenceType getType() {

        if (type == null) {
            initialiseType(metadata_class);
        }
        return type;
    }
}
