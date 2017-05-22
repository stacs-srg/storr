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
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.io.StringWriter;
import java.util.*;

import static uk.ac.standrews.cs.storr.impl.Store.getNextFreePID;

/**
 * This is a Labelled Cross Product (a tuple).
 * This is the basic unit that is stored in Buckets.
 * Higher order language level types may be constructed above this basic building block.
 * LXP provides a thin wrapper over a Map (providing name value lookup) along with identity and the ability to save and recover persistent versions (encoded in JSON).
 */
public class LXP implements ILXP, Comparable<LXP> {

    protected Map<String, Object> map;
    private long id;
    private IRepository repository = null;
    private IBucket bucket = null;

    public LXP() {

        this.id = getNextFreePID();
        this.map = new HashMap<>();
        // don't know the repo or the bucket.
    }

    public LXP(long object_id, IRepository repository, IBucket bucket) {

        this.id = object_id;
        this.map = new HashMap<>();
        this.repository = repository;
        this.bucket = bucket;
        // This constructor used when about to be filled in with values.
    }

    public LXP(long object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        this(object_id, repository, bucket);

        readJSON(reader, true);
    }

    public LXP(JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        this(getNextFreePID(), reader, repository, bucket);
    }

    /**
     * Use this constructor to create inner references of LXP
     *
     * @param reader
     * @param repository
     * @param bucket
     * @param isObject if false, just read the content of object; otherwise read the object
     * @throws PersistentObjectException
     */
    private LXP(JSONReader reader, IRepository repository, IBucket bucket, boolean isObject) throws PersistentObjectException {
        this(getNextFreePID(), repository, bucket);

        readJSON(reader, isObject);
    }

    @Override
    public ILXP create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        try {
            return new LXP(persistent_object_id, reader, repository, bucket);
        } catch (IllegalKeyException e) {
            throw new PersistentObjectException("Illegal key exception");
        }
    }

    @Override
    public IRepository getRepository() {
        return repository;
    }

    @Override
    public long getTypeLabel() {
        try {
            return getLong(Types.LABEL); // safe only one way in.
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            return -1;
        }
    }

    @Override
    public void addTypeLabel(IReferenceType label) throws Exception {
        if (containsKey(Types.LABEL)) {
            throw new Exception("Type label already specified");
        }
        try {
            put(Types.LABEL, label.getId());
        } catch (IllegalKeyException e) {
            throw new Exception("Illegal label");
        }
    }

    @Override
    public long getId() {

        return id;
    }

    /**
     * This method writes data to a writer - typically used for persistent storage.
     */
    public void serializeToJSON(JSONWriter writer, IRepository repository, IBucket bucket) throws JSONException {

        this.repository = repository;
        this.bucket = bucket;
        writer.object();
        serializeFieldsToJSON(writer);
        writer.endObject();
    }

    @Override
    public Object get(String key) throws KeyNotFoundException {
        if (containsKey(key)) {
            return map.get(key);
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public String getString(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof String) {
                return (String) result;
            } else {
                throw new TypeMismatchFoundException("expected string found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public double getDouble(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof Double) {
                return (Double) result;
            } else {
                throw new TypeMismatchFoundException("expected double found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public int getInt(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof Integer) {
                return (Integer) result;
            } else {
                throw new TypeMismatchFoundException("expected integer found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public boolean getBoolean(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                throw new TypeMismatchFoundException("expected Boolean found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public long getLong(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof Long) {
                return (Long) result;
            } else if (result instanceof Integer) {
                return Long.valueOf((Integer) result);
            } else {
                throw new TypeMismatchFoundException("expected Long found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public List getList(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof List) {
                return (List) result;
            } else {
                throw new TypeMismatchFoundException("expected List found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public IStoreReference getRef(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if( result instanceof IStoreReference ) { // expected
                return (IStoreReference) result;
            }
            if (result instanceof String) { // expected
                return new StoreReference(repository.getStore(), (String) result);
            } else {
                throw new TypeMismatchFoundException("expected String found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public IStoreReference getThisRef() throws PersistentObjectException {
        if (repository == null) {
            throw new PersistentObjectException("LXP stored in unknown repository");
        }
        if (bucket == null) {
            throw new PersistentObjectException("LXP stored in unknown bucket");
        }
        return new StoreReference(repository, bucket, this);
    }

    @Override
    public void put(String key, String value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, boolean value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, long value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, List list) throws IllegalKeyException {
        check(key);
        map.put(key, list);
    }

    @Override
    public void put(String key, double value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, int value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, IStoreReference value) throws IllegalKeyException {
        check(key);
        map.put(key, value.toString());
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> getLabels() {
        return map.keySet();
    }

    private void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            writer.key(key);
            Object value = entry.getValue();

            if (value instanceof List) {
                writer.array();
                for (Object o : (List) value) {
                    writeSimpleValue(writer, o);
                }
                writer.endArray();
            } else {
                writeSimpleValue(writer, value);
            }
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

    public String toString() {

        StringWriter writer = new StringWriter();
        try {
            serializeToJSON(new JSONWriter(writer), repository, bucket);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    @Override
    public int compareTo(LXP o) {
        return Long.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {

        return (o instanceof LXP) && (compareTo((LXP) o)) == 0;
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }

    //****** Private methods ******//

    private void check(String key) throws IllegalKeyException {
        if (key == null || key.equals("")) {
            throw new IllegalKeyException("null key");
        }
    }

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
            return reader.stringValue();
        }
        if (reader.have(JSONReader.BOOLEAN)) {
            return reader.booleanValue();
        }

        return null;
    }

    private void readJSON(JSONReader reader, boolean isObject) throws JSONException, PersistentObjectException {

        try {
            reader.nextSymbol();
            if (isObject) reader.object();

            while (!reader.isEndOfStream()) {

                String key = reader.key();
                Object value = readValue(reader);

                if (value != null) {
                    check(key);
                    map.put(key, value);

                } else if (reader.have(JSONReader.ARRAY)) {
                    readArrayIfPresent(reader, key);

                } else if (reader.have(JSONReader.OBJECT)) {

                    ILXP lxp = new LXP(reader, repository, bucket, false);
                    bucket.makePersistent(lxp);

                    IStoreReference ref = new StoreReference(repository.getStore(), repository.getName(), bucket.getName(), lxp.getId());
                    put(key, ref);

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

    // FIXME/BUG - This method does not work if we have an array of objects
    //
    //    {
    //        "glossary": [{
    //        "title": "test title"
    //    }]
    //    }
    //
    private void readArrayIfPresent(JSONReader reader, String key) throws JSONException, PersistentObjectException {

        if (reader.have(JSONReader.ARRAY)) {

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
            reader.nextSymbol(); // eat the comma
            put(key, list);

        } else {
            throw new PersistentObjectException("Unexpected type in JSON string: ");
        }
    }

}
