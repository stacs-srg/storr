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

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for an LXP (labeled cross product class).
 *
 * @author al
 */
public abstract class LXP extends PersistentObject {

    LXPMetadata metadata = new LXPMetadata(); // field not used in Static LXP instances

    private final static int INITIAL_SIZE = 5;
    private static final int SIZE_INCREMENT = 5;

    private Object[] field_storage = new Object[INITIAL_SIZE];   // where the data lives in the LXP.

    private int next_free_slot = 0;

    // Constructors

    public LXP() {
        super();
    }

    public LXP(long object_id, IBucket bucket) {
        super( object_id,bucket );
    }

    // Abstract methods

    /**
     * Checks to see if the given key is present in the lxp.
     * Dynamic classes are at liberty to add fields if required.
     *
     * @param key - a key to be checked
     * @throws IllegalKeyException if the key is not present or illegal
     */
    public abstract void check(String key) throws IllegalKeyException;

    /**
     * @return the metadata associated with the class extending LXP base.
     * This may be static or dynamically created.
     * Two classes are provided corresponding to the above.
     */
    public abstract LXPMetadata getMetaData();

    // Selectors

    /**
     * A getter method over labelled values in the LXPID
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    public Object get(final int slot) throws KeyNotFoundException {

        try {
            return field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        }
    }

    public int getFieldCount() {
        return metadata.getFieldCount();
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    public String getString(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (String) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected String found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a double
     */
    public double getDouble(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (Double) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected double found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not an integer
     */
    public int getInt(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (Integer) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected int found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a boolean
     */
    public boolean getBoolean(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (Boolean) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected boolean found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a long
     */
    public long getLong(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (Long) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected Long found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the list associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a long
     */
    public List getList(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (List) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected String found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a Store reference
     */
    public IStoreReference getRef(final int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (IStoreReference) field_storage[slot];
        } catch (final IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (final ClassCastException e) {
            throw new TypeMismatchFoundException("expected String found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * @return a reference to the object on which it was called.
     */
    public IStoreReference getThisRef() throws PersistentObjectException {

        if ($$$bucket$$$bucket$$$ == null) {
            throw new PersistentObjectException("Null $$$bucket$$$bucket$$$ encountered in LXP (uncommited LXP reference) : " + toString());
        }
        return new LXPReference($$$bucket$$$bucket$$$.getRepository(), $$$bucket$$$bucket$$$, this);
    }

    public IRepository getRepository() {

        if ($$$bucket$$$bucket$$$ == null) {
            return null;
        } else {
            return $$$bucket$$$bucket$$$.getRepository();
        }
    }

    /**
     * @return the value associated with the label supplied.
     * @throws KeyNotFoundException if the label does not exist in the fieldmap.
     */
    public Object get(final String label) throws KeyNotFoundException {

        final Integer slot = getMetaData().getSlot(label);
        if (slot == null) {
            throw new KeyNotFoundException("No field with name " + label + " in " + this.getId());
        }
        return field_storage[slot];
    }

    //------------- Putters -------------

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final Object value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final String value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final double value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final int value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final boolean value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final long value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the list to associated with the @param label
     */
    public void put(final int slot, final List value) {

        putValue(slot, value);
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot  - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(final int slot, final IStoreReference value) {

        putValue(slot, value);
    }

    /**
     * Associated the value supplied with the supplied key in this object.
     *
     * @param key   - the key with which to associate a value.
     * @param value - the value to be added to the tuple.
     */
    public void put(final String key, final Object value) {

        check(key);
        field_storage[getMetaData().getSlot(key)] = value;
    }

    // Slot management

    private void putValue(final int slot, final Object value) {

        if (slot >= field_storage.length) {
            growStorage(slot);
        }
        field_storage[slot] = value;
    }

    private void copyArray(final int new_size) {
        field_storage = Arrays.copyOf(field_storage, new_size);
    }

    private void growStorage(final int requested_slot) {

        final int new_size = Math.max(requested_slot + SIZE_INCREMENT, field_storage.length + SIZE_INCREMENT); // Leave some space for expansion.
        copyArray(new_size);
    }

    /**
     * @return the first free slot in the storage array, grow the array if necessary
     */
    public Integer findFirstFree() {

        if (next_free_slot >= field_storage.length) {
            copyArray(field_storage.length + SIZE_INCREMENT);
        }
        return next_free_slot++;
    }

    // JSON Manipulation - write methods


    void serializeToJSON(final JSONWriter writer) throws JSONException {

        writer.object();
        serializeFieldsToJSON(writer);
        writer.endObject();
    }

    private void serializeFieldsToJSON(final JSONWriter writer) throws JSONException {

        for (int i = 0; i < getMetaData().getFieldCount(); i++) {

            final String key = getMetaData().getFieldName(i);
            writer.key(key);
            final Object value = field_storage[i];

            if (value instanceof ArrayList) {
                writer.array();
                for (final Object o : (List) value) {
                    if (o instanceof LXP) {
                        writeReference(writer, (LXP) o);
                    } else {
                        writeSimpleValue(writer, o);
                    }
                }
                writer.endArray();
            } else {
                writeSimpleValue(writer, value);
            }
        }
    }

    private static void writeReference(final JSONWriter writer, final LXP value) {

        try {
            final IStoreReference reference = value.getThisRef();
            writer.value(reference.toString());
        } catch (final PersistentObjectException e) {
            throw new JSONException("Cannot serialise reference");
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

    private void readArray(final JSONReader reader, final String key) throws JSONException, PersistentObjectException, BucketException {

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

        put(key, list);
    }

    void readJSON(final JSONReader reader, final boolean isObject) throws JSONException, PersistentObjectException {

        try {
            reader.nextSymbol();
            if (isObject) reader.object();

            final Map<String, Integer> field_name_to_slot = getMetaData().getFieldNamesToSlotNumbers();

            while (!reader.isEndOfStream()) {

                final String key = reader.key().intern(); // keep the keys identical whenever possible.
                final Object value = readValue(reader);

                if (value != null) {
                    check(key);
                    put(field_name_to_slot.get(key), value);

                } else if (reader.have(JSONReader.ARRAY)) {
                    readArray(reader, key);

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
}
