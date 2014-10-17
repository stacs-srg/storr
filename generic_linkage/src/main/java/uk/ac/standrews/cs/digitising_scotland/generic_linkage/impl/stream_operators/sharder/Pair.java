package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IPair;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.IOException;
import java.util.Set;

/**
 * Created by al on 19/06/2014.
 */
public class Pair<T extends ILXP> implements IPair<T> { //TODO fix up this
    private T first;
    private T second;

    public Pair( T first, T second ) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public T second() {
        return second;
    }

    //TODO all bodged below here

    @Override
    public int getLabel() { return -1; }    // TODO write these!

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void serializeToJSON(JSONWriter writer) throws JSONException {

    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String put(String key, String value) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }


    @Override
    public ILXP create(int label_id, JSONReader reader) throws PersistentObjectException {
        return null;
    }

    @Override
    public boolean checkConsistentWith(int label_id) throws IOException, PersistentObjectException {
        return false;
    }

}
