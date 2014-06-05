package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class LXP extends HashMap<String, String> implements ILXP {

    private int id;

    public LXP() {
    }

    public LXP(int id) {
        this();
        this.id = id;
    }

    public LXP(int id, JSONReader reader) throws PersistentObjectException {
        try {
            this.id = id;

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
    public void put(String key, int value) {
        put(key, Integer.toString(value));
    }

    @Override
    public String get(String key) {
        return super.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(key);
    }

    public void serializeFieldsToJSON(JSONWriter writer) throws JSONException {

        for (Map.Entry<String, String> entry : entrySet()) {
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
}
