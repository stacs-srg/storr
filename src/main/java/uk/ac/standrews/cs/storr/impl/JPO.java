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

import org.apache.commons.lang.reflect.FieldUtils;
import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *
 * Java Persistent Object Class
 *
 * @author al
 */
public abstract class JPO extends PersistentObject {

    // Constructors

    public JPO() {

        this.$$$$id$$$$id$$$$ = new Random().nextLong();

        // don't know the repo or the $$$bucket$$$bucket$$$
    }

    public JPO(final long object_id) {

        this.$$$$id$$$$id$$$$ = object_id;

        // This constructor used when about to be filled in with values.
    }

    public JPO(long object_id, IBucket bucket) {
        this(object_id);
        this.$$$bucket$$$bucket$$$ = bucket;
    }

    public JPO(long object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        this(object_id, bucket);
        this.readJSON(reader, true);
    }

    // Selectors

    public abstract JPOMetadata getMetaData();

    public IStoreReference getThisRef() throws PersistentObjectException {
        if (this.$$$bucket$$$bucket$$$ == null) {
            throw new PersistentObjectException("Null $$$bucket$$$bucket$$$ encountered in LXP (uncommited LXP reference) : " + this.toString());
        } else {
            return new JPOReference(this.$$$bucket$$$bucket$$$.getRepository(), this.$$$bucket$$$bucket$$$, this);
        }
    }


    void serializeToJSON(final JSONWriter writer) throws JSONException {

        writer.object();
        serializeFieldsToJSON(writer);
        writer.endObject();
    }

    /**
     * Writes the state of the LXP to a Bucket.
     *
     * @param writer the stream to which the state is written.
     * @param bucket @throws JSONException if the record being written cannot be written to legal JSON.
     *
     *       THIS METHOD CALLED FROM BUCKET
     */
    public void serializeToJSON(final JSONWriter writer, IBucket bucket) throws JSONException {

        this.$$$bucket$$$bucket$$$ = bucket;
        serializeToJSON(writer);
    }


    private void serializeFieldsToJSON(final JSONWriter writer) throws JSONException {

        Collection<JPOField> fields = ((JPOMetadata) getMetaData()).getStorrFields();

        for (JPOField field : fields) {

            String key = field.name;

            Object value;
            try {
                value = FieldUtils.readField(this, key, true);
            } catch (IllegalAccessException e) {
                throw new JSONException("Cannot access field: " + key + "," + e.getMessage());
            }
            writer.key(key);
            if (field.isJPORef) {
                writeReference(writer, (JPOReference) value);
            } else if (field.isLXPRef) {
                writeReference(writer, (LXPReference) value);
            }
            else if (field.isList) {
                writeArray(writer, value);
            } else {
                writeSimpleValue(writer, value);
            }
        }
    }

    private void writeArray(JSONWriter writer, Object value) {
        if( value == null ) {
            writeSimpleValue(writer, "null");
        } else {
            writer.array();
            for (final Object o : (List) value) {
                try {
                    if (o instanceof JPO) {
                        writeReference(writer, ((JPO) o).getThisRef());
                    } else if (o instanceof LXP) {
                        writeReference(writer, ((LXP) o).getThisRef());
                    } else {
                    }
                    writeSimpleValue(writer, o);
                } catch (PersistentObjectException e) {
                    throw new JSONException("Cannot get reference object from array");
                }
            }
            writer.endArray();
        }
    }


    private static void writeReference(final JSONWriter writer, final IStoreReference value) {

        if( value == null ) {
            writer.value("null");
        } else {
            writer.value(value.toString());
        }
    }

    private static void writeSimpleValue(final JSONWriter writer, final Object value) throws JSONException {

        if (value instanceof Double) {
            writer.value(((Double) (value)).doubleValue());
        } else if (value instanceof Integer) {
            writer.value(((Integer) (value)).intValue());
        } else if (value instanceof Boolean) {
            writer.value(((Boolean) (value)).booleanValue());
        } else if (value instanceof Long) {
            writer.value(((Long) (value)).longValue());
        } else {
            writer.value(value); // default is to write a string
        }
    }

    // JSON Manipulation - read methods

    private static Object readValue(final JSONReader reader) throws JSONException {

        if (reader.have(JSONReader.LONG)) {
            return reader.longValue();
        }
        if (reader.have(JSONReader.INTEGER)) {
            return reader.intValue();
        }
        if (reader.have(JSONReader.DOUBLE)) {
            return reader.doubleValue();
        }
        if (reader.have(JSONReader.STRING)) {
            return reader.stringValue().intern(); // keep the Strings the same whenever possible.
        }
        if (reader.have(JSONReader.BOOLEAN)) {
            return reader.booleanValue();
        }

        return null;
    }

    private List readArray(final JSONReader reader, final String key) throws JSONException, PersistentObjectException, BucketException {

        reader.nextSymbol(); // eat the array symbol
        final List list = new ArrayList();

        while (!reader.have(JSONReader.ENDARRAY)) {

            final Object value = readValue(reader);

            if (value != null) {
                list.add(value);
            } else {
                throw new PersistentObjectException("Unexpected type in JSON Array");
            }
        }
        reader.nextSymbol(); // eat the end array

        if (reader.have(JSONReader.COMMA)) {
            reader.nextSymbol(); // eat the comma
        }

        return list;
    }


    public void readJSON(final JSONReader reader, final boolean isObject) throws JSONException, PersistentObjectException {

        try {
            reader.nextSymbol();
            if (isObject) reader.object();

            while (!reader.isEndOfStream()) {

                final String key = reader.key().intern(); // keep the keys identical whenever possible.
                final Object value = readValue(reader);

                if (value != null) {
                    put(key, value);
                } else if (reader.have(JSONReader.ARRAY)) {   // TODO doesn't cope with null
                    List list = readArray(reader, key);
                    put(key, list);
                } else {
                    throw new PersistentObjectException("No matching value for the key " + key);
                }
            }
        } catch (final JSONException | BucketException e) {

            if (reader.have(JSONReader.ENDOBJECT)) { // we are at the end and that is OK
                return;
            }
            // otherwise bad stuff has happened
            throw new PersistentObjectException(e);
        }
    }

    public void put( String key, Object value ) throws PersistentObjectException {

        JPOMetadata md = getMetaData();

        JPOField field = md.get(key);

        if( field == null ) {
            throw new PersistentObjectException( "key not found: " + key );
        }

        if( field.isList ) {
            if( value instanceof String && ((String)value).equals("null") ) {
                value = null;
            }
        } else if( field.isLXPRef || field.isJPORef ) {
            // check it is a string
            if( ! ( value instanceof  String ) ) {
                throw new PersistentObjectException( "Encountered store reference type not String encoded");
            }
            String str_val = (String) value;
            if( str_val.equals("null") ) {
                value = null;
            } else {
                if( field.isLXPRef ) {
                    value = new LXPReference(Store.getInstance(), (String) value);
                } else {
                    value = new JPOReference(Store.getInstance(), (String) value);
                }
            }
        }
        try {
            FieldUtils.writeField(this, key, value, true);
        } catch (IllegalAccessException e) {
            throw new PersistentObjectException(e.getMessage());
        }

    }
}
