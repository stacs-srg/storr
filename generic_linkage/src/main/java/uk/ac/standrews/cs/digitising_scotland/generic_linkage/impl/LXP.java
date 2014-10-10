package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LXP implements ILXP {

    private int id;
    private HashMap<String, String> map;
    private final static LXP instance = new LXP();

    public LXP() {

        this.id = Store.getInstance().getNextFreePID();
        this.map = new HashMap<>();
    }

    public LXP( int id ) {

        this.id = id;
        this.map = new HashMap<>();
    }

    public LXP(int id, JSONReader reader) throws PersistentObjectException {
        this( id );
        try {
            reader.nextSymbol();
            reader.object();

            while (!reader.isEndOfStream()) {

                String key = reader.key();
                String value = reader.stringValue();
                this.put(key, value);
            }

        } catch (JSONException e) {
            throw new PersistentObjectException(e);
        }
    }

    public LXP(JSONReader reader) throws PersistentObjectException {
        this( Store.getInstance().getNextFreePID(), reader );
    }

    public LXP(int id, JSONReader reader, int label_id) throws PersistentObjectException {
        this( id, reader ); // must do this first

            if( ! checkConsistentWith( label_id ) ) {
                throw new PersistentObjectException("incompatible with label");
            }
    }

    @Override
    public LXP create(int label_id, JSONReader reader) throws PersistentObjectException  {
        return new LXP(label_id,reader);
    }

    @Override
    /*
     * This is a no-op for this class
     */
    public ILXP convert(ILXP base) {
        return base;
    }

    @Override
    public boolean checkConsistentWith(int label_id) {
        return true; // there is no contract with this class - creates whatever is there.
        // over-ridden in super classes.
    }

    @Override
    public int getId() {

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
    public String get(String key) {
        return map.get(key);       // TOD) throw key not found exception
    }

    @Override
    public String put(String key, String value) {
        return map.put(key,value);
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> getKeys() {
        return map.keySet();
    }

    public void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            writer.key(key);
            String value = entry.getValue();
            writer.value(value);
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

    public static ILXPFactory getInstance() {
        return instance;
    }
}
