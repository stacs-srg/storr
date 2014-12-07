package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
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
    protected HashMap<String, String> map;

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
                String value = reader.stringValue();  // TODO USE HAVE HERE???? - BEEF UP PARSING TO SUPPORT TYPED FIELDS
                this.put(key, value);
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
            return Long.parseLong(get(Types.LABEL)); // safe only one way in.
        } catch (KeyNotFoundException e) {
            return -1;
        }
    }

    @Override
    public void addTypeLabel(IReferenceType label) throws Exception {
        if (containsKey(Types.LABEL)) {
            throw new Exception("Type label already specified");
        }
        put(Types.LABEL, Long.toString(label.getId()));
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
    public String get(String key) throws KeyNotFoundException {
        if (containsKey(key)) {
            return map.get(key);
        }
        throw new KeyNotFoundException(key);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
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
}
