package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LXP implements ILXP {

    private long id;
    protected HashMap<String, Object> map;

    public LXP() {

        this.id = Store.getInstance().getNextFreePID();
        this.map = new HashMap<>();
    }

    public LXP(long object_id) {

        this.id = object_id;
        this.map = new HashMap<>();
    }

    public LXP(long object_id, JSONReader reader) throws PersistentObjectException {
        this(object_id);
        try {
            reader.nextSymbol();
            reader.object();

            while (!reader.isEndOfStream()) {

                String key = reader.key();

                // TODO Parising experiment

                if (reader.have(JSONReader.LONG)) {
                    long value = reader.longValue();
                    System.out.println("for key: " + key + " ,read a long :" + value);  // TODO add long
                    this.put(key, value);
                }
                if (reader.have(JSONReader.INTEGER)) {
                    int value = reader.intValue();
                    System.out.println("for key: " + key + " ,read a string :" + value);
                    this.put(key, value);
                } else if (reader.have(JSONReader.DOUBLE)) {
                    double value = reader.doubleValue();
                    System.out.println("for key: " + key + " ,read a double :" + value);
                    this.put(key, value);
                } else if (reader.have(JSONReader.STRING)) {
                    String value = reader.stringValue();
                    System.out.println("for key: " + key + " ,read a string :" + value);
                    this.put(key, value);
                } else if (reader.have(JSONReader.BOOLEAN)) {
                    Boolean value = reader.booleanValue();
                    System.out.println("for key: " + key + " ,read a bool :" + value);
                    this.put(key, value);
                } else {
                    throw new PersistentObjectException("Unexpected type in JSON string");
                }
            }
        } catch (JSONException e) {
            if (reader.have(JSONReader.ENDOBJECT)) { // we are at the end and that is OK
                return;
            }
            // otherise bad stuff has happend
            throw new PersistentObjectException(e);
        }
    }

    public LXP(JSONReader reader) throws PersistentObjectException {
        this(Store.getInstance().getNextFreePID(), reader);
    }

    @Override
    public ILXP create(long persistent_object_id, JSONReader reader) throws PersistentObjectException {
        return new LXP(persistent_object_id, reader);
    }


    @Override
    public boolean checkConsistentWith(long label_id) {
        return true; // there is no contract with this class - creates whatever is there.
        // over-ridden in super classes.
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
        put(Types.LABEL, label.getId());
    }

    @Override
    public long getId() {

        return id;
    }

    /**
     * This method writes data to a writer - typically used for persistent storage.
     *
     * @throws JSONException
     */
    public void serializeToJSON(JSONWriter writer) throws JSONException {

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
                return (long) new Long(((Integer) result).intValue()); // what a mess!
            } else {
                throw new TypeMismatchFoundException("expected Long found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void put(String key, boolean value) {
        map.put(key, new Boolean(value));
    }

    @Override
    public void put(String key, long value) {
        map.put(key, new Long(value));
    }

    @Override
    public void put(String key, double value) {
        map.put(key, new Double(value));
    }

    @Override
    public void put(String key, int value) {
        map.put(key, new Integer(value));
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> getLabels() {
        return map.keySet();
    }

    public void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            writer.key(key);
            Object value = entry.getValue();

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
    }

    public String toString() {

        StringWriter sw = new StringWriter();
        try {
            serializeToJSON(new JSONWriter(sw));
        } catch (JSONException e) {
            ErrorHandling.error("in LXP.toString()");
        }
        return sw.toString();
    }
}
