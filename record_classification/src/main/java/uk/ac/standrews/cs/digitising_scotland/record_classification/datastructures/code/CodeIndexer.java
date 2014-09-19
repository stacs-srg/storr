package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 *FIXME rewrite this
 *
 * @author jkc25, frjd2
 */
public final class CodeIndexer implements Serializable {

    private static final long serialVersionUID = 3721498072294663528L;

    /** The code map. */
    private Map<String, Code> codeMap = new HashMap<String, Code>();

    /** Maps UID's to codes. */
    private Map<Integer, Code> idToCodeMap = new HashMap<Integer, Code>();

    /** Maps code to their UIDs. */
    private Map<Code, Integer> codeToIDMap = new HashMap<Code, Integer>();

    /** The current max id. */
    private int currentMaxID;

    private CodeDictionary codeDictionary;

    /**
     * Instantiates a new code factory.
     * @param codeChecker the definitive list of codes so that no malformed codes are added to the codeIndexer.
     */
    public CodeIndexer(final CodeDictionary codeChecker) {

        this.codeDictionary = codeChecker;
        MachineLearningConfiguration.getDefaultProperties().setProperty("numCategories", String.valueOf(idToCodeMap.size()));
    }

    /**
     * Adds gold standard codes from each record to the {@link CodeIndexer}.
     * @param bucket bucket with gold standard codes
     * @throws CodeNotValidException indicates a code is not in the {@link CodeIndexer}, usually because it is malformed
     */
    public void addGoldStandardCodes(final Bucket bucket) throws CodeNotValidException {

        for (Record record : bucket) {
            for (Classification classification : record.getOriginalData().getGoldStandardClassifications()) {
                putCodeInMap(classification.getCode().getCodeAsString());
            }
        }

    }

    /**
     * Returns the code that this id is mapped to.
     * @param id associated with mapped code
     * @return Code assosciated with this id
     */
    public Code getCode(final Integer id) {

        return idToCodeMap.get(id);
    }

    /**
     * Returns the total number of output classes based on the size of the code map.
     * @return the number of output classes in the codeMap.
     */
    public int getNumberOfOutputClasses() {

        return codeMap.size();
    }

    /**
     * Puts a code in the map after checking that it's valid by using the {@link CodeDictionary}.
     *
     * @param code the code to add to the map
     * @throws CodeNotValidException the code not valid exception, thrown if a code is not in the {@link CodeDictionary}
     */
    private void putCodeInMap(final String code) throws CodeNotValidException {

        if (!codeMap.containsKey(code)) {
            if (codeIsValid(code)) {
                createCodeAndAddToMaps(code);
            }
            else {
                throw new CodeNotValidException("Code is not in definitve code list supplied");
            }

        }
    }

    private void createCodeAndAddToMaps(final String codeToAdd) {

        Code code = new Code(codeToAdd, codeDictionary.getDescription(codeToAdd));
        codeMap.put(codeToAdd, code);
        idToCodeMap.put(currentMaxID, code);
        codeToIDMap.put(code, currentMaxID);
        currentMaxID++;
    }

    private boolean codeIsValid(final String codeToAdd) {

        return codeDictionary.isValidCode(codeToAdd);
    }

    public void writeCodeFactory(final File path) throws IOException {

        FileOutputStream fos = new FileOutputStream(path.getAbsoluteFile() + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        write(oos);
    }

    public CodeIndexer readCodeFactory(final File path) throws IOException, ClassNotFoundException {

        InputStream file = new FileInputStream(path.getAbsoluteFile() + ".ser");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        CodeIndexer recoveredFactory = null;
        try {
            recoveredFactory = (CodeIndexer) input.readObject();
            codeMap = recoveredFactory.codeMap;
            idToCodeMap = recoveredFactory.idToCodeMap;
            currentMaxID = recoveredFactory.currentMaxID;

        }
        finally {
            input.close();
        }
        return recoveredFactory;
    }

    public void write(final ObjectOutputStream oos) throws IOException {

        oos.writeObject(this);
        oos.close();
    }

}
