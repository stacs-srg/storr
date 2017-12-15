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
import java.util.HashMap;
import java.util.List;

import static uk.ac.standrews.cs.storr.impl.Store.getNextFreePID;

/**
 * Abstract class for an LXP (labeled cross product class).
 *
 * @author al
 */

public abstract class LXP {

    protected long id;
    protected IBucket bucket = null;

    protected Metadata metadata = new Metadata();

    private final static int INITIAL_SIZE = 5;
    private static final int SIZE_INCREMENT = 5;

    protected Object[] field_storage = new Object[INITIAL_SIZE];   // where the data lives in the LXP.

    private int next_free_slot = 0;

    // Constructors

    public LXP() {

        this.id = getNextFreePID();
        this.metadata = metadata;
        // don't know the repo or the bucket
    }

    public LXP( long object_id, IBucket bucket ) {

        this.id = object_id;
        this.bucket = bucket;

        // This constructor used when about to be filled in with values.
    }

    public LXP( long object_id, JSONReader reader, IBucket bucket ) throws PersistentObjectException {
        this(object_id, bucket );
        readJSON(reader, true);
    }

    public LXP(JSONReader reader, IBucket bucket ) throws PersistentObjectException {
        this(getNextFreePID(), reader, bucket );
    }

    // Abstract methods

    /**
     * Checks to see if the given key is present in the lxp.
     * Dynamic classes are at liberty to add fields if required.
     * @param key - a key to be checked
     * @throws IllegalKeyException if the key is not present or illegal
     */
    public abstract void check(String key) throws IllegalKeyException;

    /**
     * @return the metadata associated with the class extending LXP base.
     * This may be static or dynamically created.
     * Two classes are provided corresponding to the above.
     */
    public abstract Metadata getMetaData();

    // Selectors

    /**
     * @return the id of the record
     */
    public long getId() {
        return id;
    }

    /**
     * A getter method over labelled values in the LXPID
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    public Object get(int slot) throws KeyNotFoundException {
        try {
            return field_storage[slot];
        } catch( IndexOutOfBoundsException e ) {
            throw new KeyNotFoundException(slot);
        }
    }

    /**
     * A getter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    public String getString(int slot) throws KeyNotFoundException, TypeMismatchFoundException {

        try {
            return (String) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
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
    public double getDouble(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (Double) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
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
    public int getInt(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (Integer) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
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
    public boolean getBoolean(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (Boolean) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch (ClassCastException e) {
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
    public long getLong(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (Long) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
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
    public List getList(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (List) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
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
    public IStoreReference getRef(int slot) throws KeyNotFoundException, TypeMismatchFoundException {
        try {
            return (IStoreReference) field_storage[slot];
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException(slot);
        } catch( ClassCastException e ) {
            throw new TypeMismatchFoundException("expected String found: " + field_storage[slot].getClass().getName());
        }
    }

    /**
     * @return a reference to teh object on which it was called.
     * @throws PersistentObjectException
     */
    public IStoreReference getThisRef() throws PersistentObjectException {

        if (bucket == null) {
            throw new PersistentObjectException("LXP stored in unknown bucket: " + this.toString() );
        }
        return new StoreReference(bucket.getRepository(), bucket, this);
    }

    public IRepository getRepository() {

        if( bucket == null ) {
            return null;
        } else {
            return bucket.getRepository();
        }
    }

    /**
     *
     * @param label
     * @return the value associated with the label supplied.
     * @throws KeyNotFoundException if the label does not exist in the fieldmap.
     */
    public Object get(String label) throws KeyNotFoundException {

        Integer slot = getMetaData().getSlot( label );
        if( slot == null ) {
            throw new KeyNotFoundException( "No field with name " + label + " in " + this.getId() );
        }
        return field_storage[ slot ];
    }

    //------------- Putters -------------

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, Object value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, String value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, double value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, int value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, boolean value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }
    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, long value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param list  - the list to associated with the @param label
     */
    public void put(int slot, List list)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = list;
    }

    /**
     * A setter method over labelled values in the LXP
     *
     * @param slot - the slot number of the required field
     * @param value - the value to associated with the @param label
     */
    public void put(int slot, IStoreReference value)  {
        if( slot >= field_storage.length ) {
            grow_storage( slot );
        }
        field_storage[ slot ] = value;
    }

    /**
     * Associated the value supplied with the supplied key in this object.
     * @param key - the key with which to associate a value.
     * @param value - the value to be added to the tuple.
     * @throws IllegalKeyException
     */
    public void put( String key, Object value) {
        check(key);
        field_storage[ getMetaData().getSlot(key) ] = value;
    }

    // Slot management

    private void copy_array( int new_size ) {
        field_storage = Arrays.copyOf(field_storage, new_size );
    }

    private void grow_storage( int requested_slot ) {
        int new_size = Math.max( requested_slot + SIZE_INCREMENT, field_storage.length + SIZE_INCREMENT ); // Leave some space for expansion.
        copy_array( new_size );
    }

    /**
     * @return the first free slot in the storage array, grow the array if necessary
     */
    public Integer findfirstFree() {
        if( next_free_slot >= field_storage.length ) {
            copy_array( field_storage.length + SIZE_INCREMENT );
        }
        return next_free_slot++;

    }

    // JSON Manipulation - write methods

    /**
     * Writes the state of the LXP to a Bucket.
     *
     * @param writer     the stream to which the state is written.
     * @param bucket     @throws JSONException if the record being written cannot be written to legal JSON.
     */
    public void serializeToJSON(JSONWriter writer, IBucket bucket) throws JSONException {

        this.bucket = bucket;
        serializeToJSON( writer );
    }

    protected void serializeToJSON(JSONWriter writer) throws JSONException {
        writer.object();
        serializeFieldsToJSON(writer);
        writer.endObject();
    }

    private void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for( int i = 0; i < getMetaData().getFieldCount(); i++ ) {

            String key = getMetaData().getFieldName( i );
            writer.key(key);
            Object value = field_storage[ i ];

            if (value instanceof ArrayList) {
                writer.array();
                for (Object o : (List) value) {
                    if (o instanceof LXP ) {
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

    private void writeReference( JSONWriter writer, LXP value ) {
        try {
            IStoreReference reference = value.getThisRef();
            writer.value(reference.toString() );
        } catch (PersistentObjectException e) {
            throw new JSONException( "Cannot serialise reference" );
        }
    }

    private void writeSimpleValue(JSONWriter writer, Object value) throws JSONException {

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

    private Object readValue(JSONReader reader) throws JSONException {

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


    private void readArray(JSONReader reader, String key) throws JSONException, PersistentObjectException, BucketException {

        reader.nextSymbol(); // eat the array symbol
        List list = new ArrayList();

        while (!reader.have(JSONReader.ENDARRAY)) {

            Object value = readValue(reader);

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

    private void readJSON(JSONReader reader, boolean isObject) throws JSONException, PersistentObjectException {

        try {
            reader.nextSymbol();
            if (isObject) reader.object();

            HashMap<String, Integer> field_name_to_slot = getMetaData().getFieldNamesToSlotNumbers();

            while (!reader.isEndOfStream()) {

                String key = reader.key().intern(); // keep the keys identical whenever possible.
                Object value = readValue(reader);

                if (value != null) {
                    check(key);
                    put( field_name_to_slot.get(key),value );

                } else if (reader.have(JSONReader.ARRAY)) {
                    readArray(reader, key);

                } else {
                    throw new PersistentObjectException("No matching value for the key " + key);
                }
            }
        } catch (JSONException | BucketException e) {
            if (reader.have(JSONReader.ENDOBJECT)) { // we are at the end and that is OK
                return;
            }
            // otherwise bad stuff has happened
            throw new PersistentObjectException(e);
        }

    }


}
