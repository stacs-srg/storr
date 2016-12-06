package uk.ac.standrews.cs.storr.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.types.Types;
import uk.ac.standrews.cs.storr.util.ErrorHandling;

import java.io.StringWriter;
import java.util.*;

import static uk.ac.standrews.cs.storr.impl.Store.getNextFreePID;

/**
 * This is a labelled Cross Product (a tuple).
 * This is the basic unit that is stored in Buckets.
 * Higher order language level types may be constructed above this basic building block.
 * LXP provides a thin wrapper over a Map (providing name value lookup) along with identity and the ability to save and recover persistent versions (encoded in JSON).
 */
public class LXP implements ILXP {

    private long id;
    protected HashMap<String, Object> map;
    private IRepository repo = null;
    private IBucket bucket = null;

    public LXP() {
        this.id = getNextFreePID();
        this.map = new HashMap<>();
        // don't know the repo or the bucket.
    }


    public LXP(long object_id, IRepository repository, IBucket bucket) {
        this.id = object_id;
        this.map = new HashMap<>();
        this.repo = repository;
        this.bucket = bucket;
        // This constructor used when about to be filled in with values.
    }

//    public LXP(long id, JSONReader jsonReader) {
//        this.id = getNextFreePID();
//        this.map = new HashMap<>();
//    }

    public LXP(long object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        this(object_id, repository, bucket );

        try {
            reader.nextSymbol();
            reader.object();

            while (!reader.isEndOfStream()) {

                String key = reader.key();

                if (reader.have(JSONReader.LONG)) {
                    long value = reader.longValue();
                    this.put(key, value);
                } else if (reader.have(JSONReader.INTEGER)) {
                    int value = reader.intValue();
                    this.put(key, value);
                } else if (reader.have(JSONReader.DOUBLE)) {
                    double value = reader.doubleValue();
                    this.put(key, value);
                } else if (reader.have(JSONReader.STRING)) {
                    String value = reader.stringValue();
                    this.put(key, value);
                } else if (reader.have(JSONReader.BOOLEAN)) {
                    Boolean value = reader.booleanValue();
                    this.put(key, value);
                } else if( reader.have(JSONReader.ARRAY)) {
                    reader.nextSymbol(); // east the array symbol
                    List l = new ArrayList();
                    while( ! reader.have(JSONReader.ENDARRAY)) {
                        if (reader.have(JSONReader.LONG)) {
                            l.add( reader.longValue() );
                        } else if (reader.have(JSONReader.INTEGER)) {
                            l.add( reader.intValue() );
                        } else if (reader.have(JSONReader.DOUBLE)) {
                            l.add( reader.doubleValue() );
                        } else if (reader.have(JSONReader.STRING)) {
                            l.add( reader.stringValue() );
                        } else if (reader.have(JSONReader.BOOLEAN)) {
                            l.add(reader.booleanValue());
                        } else {
                            throw new PersistentObjectException("Unexpected type in JSON Array");
                        }
                    }
                    reader.nextSymbol(); // eat the end array
                    reader.nextSymbol(); // eat the comma  //<<<<<<<<<<<<<, AL IS HERE - MAY BE WRONG!! TEST AGAIN.
                    this.put(key, l);
                } else {
                    throw new PersistentObjectException("Unexpected type in JSON string: " );
                }
            }
        } catch (JSONException e) {
            if (reader.have(JSONReader.ENDOBJECT)) { // we are at the end and that is OK
                return;
            }
            // otherise bad stuff has happened
            throw new PersistentObjectException(e);
        }
    }

    public LXP(JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        this(getNextFreePID(), reader, repository, bucket);
    }

    @Override
    public ILXP create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        try {
            return new LXP(persistent_object_id, reader, repository, bucket );
        } catch (IllegalKeyException e) {
            throw new PersistentObjectException("Illegal key exception");
        }
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

        this.repo = repository;
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
                return (long) new Long(((Integer) result).intValue()); // what a mess!
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
            }  else {
                throw new TypeMismatchFoundException("expected List found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }


    @Override
    public IStoreReference getRef(String key) throws KeyNotFoundException, TypeMismatchFoundException {
        if (containsKey(key)) {
            Object result = map.get(key);
            if (result instanceof String) { // expected
                return new StoreReference( (String) result );
            } else {
                throw new TypeMismatchFoundException("expected IStoreReference found: " + result.getClass().getName());
            }
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public IStoreReference getThisRef() throws PersistentObjectException {
        if( repo == null ) {
            throw new PersistentObjectException( "LXP stored in unkown repository");
        }
        if( bucket == null ) {
            throw new PersistentObjectException( "LXP stored in unkown bucket");
        }
        return new StoreReference( repo, bucket, this );
    }

    @Override
    public void put(String key, String value) throws IllegalKeyException {
        check(key);
        map.put(key, value);
    }

    @Override
    public void put(String key, boolean value) throws IllegalKeyException {
        check(key);
        map.put(key, new Boolean(value));
    }

    @Override
    public void put(String key, long value) throws IllegalKeyException {
        check(key);
        map.put(key, new Long(value));
    }

    @Override
    public void put(String key, List list) throws IllegalKeyException {
        check(key);
        map.put(key, list);
    }

    @Override
    public void put(String key, double value) throws IllegalKeyException {
        check(key);
        map.put(key, new Double(value));
    }

    @Override
    public void put(String key, int value) throws IllegalKeyException {
        check(key);
        map.put(key, new Integer(value));
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

    public void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            writer.key(key);
            Object value = entry.getValue();

            if ( value instanceof List ) {
                writer.array();
                for( Object o : (List) value ) {
                    writeSimpleValue( writer,o );
                }
                writer.endArray();
            } else {
                writeSimpleValue( writer, value );
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

        StringWriter sw = new StringWriter();
        try {
            serializeToJSON(new JSONWriter(sw), repo, bucket);
        } catch (JSONException e) {
            ErrorHandling.error("in OID.toString()");
        }
        return sw.toString();
    }

    //****** Private methods ******//


    private void check(String key) throws IllegalKeyException {
        if (key == null || key.equals("")) {
            throw new IllegalKeyException("null key");
        }
    }
}
