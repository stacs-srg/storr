package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;

/**
 * Interface to a LXP (labeled cross product class).
 * It provides a thin wrapper over a Map (providing name value lookup) along with identity and the ability to save and recover persistent versions (encoded in JSON).
 *
 * @author al
 */
public interface ILXP extends ILXPFactory {

    /**
     * @return the id of the record
     */
    long getId();

    /**
     * Writes the state of the LXP to a Bucket.
     *
     * @param writer the stream to which the state is written.
     */
    void serializeToJSON(JSONWriter writer) throws JSONException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException
     */
    String get(String label) throws KeyNotFoundException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     * @return the value associated with @param label
     * @throws KeyNotFoundException
     */
    String put(String label, String value);

    /**
     * @param label - the label to be looked up
     * @return true if the LXP contains the supplied label
     */
    boolean containsKey(String label);

    /**
     * @return all the labels contained in the LXP
     */
    java.util.Set<String> getLabels();

    /**
     * Associates a type with this LXP.
     * If a type is present it must be structurally compatible with the fields of the LXP
     *
     * @param typelabel - the typelabel to associate with the LXP
     * @throws Exception if the typelabel does not match the structure of the lXP.
     */
    void addTypeLabel(IReferenceType typelabel) throws Exception;
}
