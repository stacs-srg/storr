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

import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.JPO_FIELD;
import uk.ac.standrews.cs.utilities.JSONReader;

public class Person extends JPO {

    @JPO_FIELD
    private int age;

    @JPO_FIELD
    public String address;

    public Person() { // requirement for JPO
    }

    public Person(long id, JSONReader reader, IBucket bucket ) throws PersistentObjectException { // a requirement for JPO
        super(id, reader, bucket);
    }

    public Person(int age, String address) {
        this.age = age;
        this.address = address;
    }

    /* Storr stuff */

    private static final JPOMetadata static_metadata;

    @Override
    public JPOMetadata getMetaData() {
        return static_metadata;
    }

    static {
        try {
            static_metadata = new JPOMetadata(Person.class,"JPOPerson");
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }

}
