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

import uk.ac.standrews.cs.storr.types.JPO_FIELD;

public class ProtoTypicalJPO extends JPO {

    /*
     * All persistent fields must be labelled as JPO_FIELDs like the examples below.
     * supported scalar fields are: int, string, double, long
     * supported ref fields are IStoreReference<T> where T extends PersistentObject
     * supported collections are List<T> where T extends PersistentObject
     */
    @JPO_FIELD
    private int field1;

    @JPO_FIELD
    private String field2;

    /*
     * JPO has two protected fields that must NOT be reused as field names in any JPO.
     *protected long $$$$id$$$$id$$$$;
     * protected IBucket $$$bucket$$$bucket$$$;
     *
     * Similarly four selectors are provided:
     *
     *     long getId() which returns the object id
     *     Object getBucket() which returns the storage bucket
     *     IStoreReference getThisRef() which returns a reference to this JPO
     *     JPOMetadata getMetaData() which returns the JPO metadata, this must be provided by the boilerplate code.
     *
     *  These names are not permitted in a JPO.
     */

    /*
     * All JPOs require a null constructor which is used in dynamic object creation on object load
     */
    public ProtoTypicalJPO() {
    }

    /*
     * Such a constructor is NOT a JPO requirement but we might expect one of these.
     */
    public ProtoTypicalJPO(int field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    /* Storr support mechanism - ALL STORR JPO OBJECTS MUST HAVE THIS BOILERPLATE CODE */

    /*
     * This field is used to store the metadata for the class.
     */
    private static final JPOMetadata static_metadata;

    /*
     * This selector returns the class metadata.
     */
    @Override
    public JPOMetadata getMetaData() {
        return static_metadata;
    }

    /*
     * This static initialiser initialises the static meta data
     * The two parameters to the JPOMetadata constructor are the name of this class and the name the type is given in the store.
     */
    static {
        try {
            static_metadata = new JPOMetadata(ProtoTypicalJPO.class,"ProtoTypicalJPO");
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }

}
