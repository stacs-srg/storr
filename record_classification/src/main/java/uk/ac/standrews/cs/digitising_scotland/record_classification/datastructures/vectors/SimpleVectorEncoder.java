package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used by {@link VectorFactory}
 * to build vectors. This encoder uses no hashing. Every time a new token (String) is
 * encoded to a vector SimpleVectorEncoder's internal dictionary is updated to include this
 * token. Every token that is encoded using an instance of SimpleVectorEncoder is thus encoded
 * to a unique index.
 * Created by fraserdunlop on 23/04/2014 at 19:37.
 */
public class SimpleVectorEncoder extends AbstractVectorEncoder<String> implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(SimpleVectorEncoder.class);

    private Map<String, Integer> dictionary;
    private Integer currentMaxTokenIndexValue;

    /**
     * Initialises a SimpleVectorEncoder with an empty dictionary.
     */
    public SimpleVectorEncoder() {

        initialize();
    }

    @Override
    public Vector encode(final Collection<String> strings, final Vector vector) {

        for (String string : strings) {
            addToVector(string, vector);
        }
        return vector;
    }

    /**
     * Token first converted to lower case.
     * The value of the vector at the index of the token's unique index value is incremented by 1.
     *
     * @param token  a token (String) to be encoded to the vector.
     * @param vector the vector which the supplied token (String) is encoded to.
     */
    public void addToVector(final String token, final Vector vector) {

        String trimmedToken = token.trim().toLowerCase(); //remove?
        updateVector(trimmedToken, vector);
    }

    private void updateVector(final String token, final Vector vector) {

        Integer tokenIndexValue = dictionary.get(token);
        if (tokenIndexValue != null) {
            vector.set(tokenIndexValue, vector.get(tokenIndexValue) + 1);
        }
    }

    protected void updateDictionary(final String token) {

        if (dictionary.get(token) == null) {
            dictionary.put(token, currentMaxTokenIndexValue++);
        }
    }

    protected final void initialize() {

        dictionary = new LinkedHashMap<>();
        currentMaxTokenIndexValue = 0;
    }

    protected int getDictionarySize() {

        return dictionary.size();
    }

    /**
     * Write.
     *
     * @param outputStream the out
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     */
    protected void write(final ObjectOutputStream outputStream) throws IOException {

        outputStream.writeInt(currentMaxTokenIndexValue);
        for (String string : dictionary.keySet()) {
            outputStream.writeInt(dictionary.get(string));
            outputStream.writeUTF(string);
        }
    }

    /**
     * Read fields.
     *
     * @param inputStream the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void readFields(final DataInputStream inputStream) throws IOException {

        initialize();
        int currentMaxTokenIndexValue = inputStream.readInt();
        for (int i = 0; i < currentMaxTokenIndexValue; i++) {
            int readint = inputStream.readInt();

            if (i != readint) {
                LOGGER.error("error reading SimpleVectorEncoder dictionary");
                throw new RuntimeException("error reading SimpleVectorEncoder dictionary");
            }
            updateDictionary(inputStream.readUTF());
        }

    }
}
