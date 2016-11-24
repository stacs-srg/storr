package uk.ac.standrews.cs.storr.interfaces;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;

import java.util.List;

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
     * @throws JSONException if the record being written cannot be written to legal JSON.
     */
    void serializeToJSON(JSONWriter writer) throws JSONException;


    /**
     * A getter method over labelled values in the LXPOID
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    Object get(String label) throws KeyNotFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a String
     */
    String getString(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a double
     */
    double getDouble(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not an integer
     */
    int getInt(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a boolean
     */
    boolean getBoolean(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a long
     */
    long getLong(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the list associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a long
     */
    List getList(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A getter method over labelled values in the LXP
     *
     * @param label - the label whose value is required
     * @return the value associated with @param label
     * @throws KeyNotFoundException       - if the tuple does not contain the key
     * @throws TypeMismatchFoundException if the value associated with the key is not a Store reference
     */
    IStoreReference getRef(String label) throws KeyNotFoundException, TypeMismatchFoundException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, String value) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, double value) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, int value) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, boolean value) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, long value) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param list - the list to associated with the @param label
     */
    void put(String label, List list) throws IllegalKeyException;

    /**
     * A setter method over labelled values in the LXP
     *
     * @param label - the label whose value is being set
     * @param value - the value to associated with the @param label
     */
    void put(String label, IStoreReference value) throws IllegalKeyException;



    /**
     * @param label - the label to be looked up
     * @return true if the OID contains the supplied label
     */
    boolean containsKey(String label);

    /**
     * @return all the labels contained in the OID
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
