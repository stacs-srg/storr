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

    public LXP(final int id) {
        this.id = id;
    }

    public LXP(final int id, final JSONReader reader) throws PersistentObjectException {

        this(id);

        try {
            reader.nextSymbol();
            reader.object();

            while (!reader.isEndOfStream()) {

                String key = reader.key();
                String value = reader.stringValue();
                put(key, value);
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
     * Writes data to a writer - typically used for persistent storage.
     *
     * @throws JSONException
     */
    public void serializeToJSON(final JSONWriter writer) throws JSONException {

        writer.object();
        serializeFieldsToJSON(writer);
        writer.endObject();
    }

    public void serializeFieldsToJSON(final JSONWriter writer) throws JSONException {

        for (Map.Entry<String, String> entry : entrySet()) {
            String key = entry.getKey();
            writer.key(key);
            String value = entry.getValue();
            writer.value(value);
        }
    }

    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            serializeToJSON(new JSONWriter(sw));
            return sw.toString();

        } catch (JSONException e) {
            ErrorHandling.error("in LXP.toString()");
            return "";
        }
    }
}
