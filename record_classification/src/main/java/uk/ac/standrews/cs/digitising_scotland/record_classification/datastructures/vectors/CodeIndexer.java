package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * This class represents all the outputs codes that are being used in the OLR models.
 * As the size of the OLR models depend on the number of output classes it was deemed undesirable to consturct models
 * that contained codes that are never seen in the training data. To avoid this, this class should hold all the output classes
 * that are actually used.
 *
 * @author jkc25, frjd2
 */
public final class CodeIndexer implements Serializable {

    private static final long serialVersionUID = 3073583599428985116L;

    /** Maps UID's to codes. */
    private Map<Integer, Code> idToCodeMap = new HashMap<Integer, Code>();

    /** Maps code to their UIDs. */
    private Map<Code, Integer> codeToIDMap = new HashMap<Code, Integer>();

    /** The current max id. */
    private int currentMaxID;

    private final String numCategoriesId = "numCategories";

    /**
     * Instantiates a new CodeIndexer.
     */
    public CodeIndexer() {

        MachineLearningConfiguration.getDefaultProperties().setProperty(numCategoriesId, String.valueOf(idToCodeMap.size()));
    }

    /**
     * Instantiates a new CodeIndexer with all the codes from the supplied bucket added to the index.
     * @param records The bucket to add the codes from
     */
    public CodeIndexer(final Iterable<Record> records) {

        addGoldStandardCodes(records);

    }

    /**
     * Instantiates a new CodeIndexer with all the codes from the {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary} in the index.
     * @param dictionary The dictionary to add the codes from
     */
    public CodeIndexer(final CodeDictionary dictionary) {

        addGoldStandardCodes(dictionary);
    }

    /**
     * Adds gold standard codes from each record to the {@link CodeIndexer}.
     * @param dictionary CodeDictionary with codes to be added
     */
    public void addGoldStandardCodes(final CodeDictionary dictionary) {

        Iterator<Entry<String, Code>> i = dictionary.getIterator();
        while (i.hasNext()) {
            Entry<String, Code> entry = i.next();
            putCodeInMap(entry.getValue());
        }

        MachineLearningConfiguration.getDefaultProperties().setProperty(numCategoriesId, String.valueOf(idToCodeMap.size()));

    }

    /**
     * Adds gold standard codes from each record to the {@link CodeIndexer}.
     * @param records records with gold standard codes
     */
    public void addGoldStandardCodes(final Iterable<Record> records) {

        for (Record record : records) {
            for (Classification classification : record.getOriginalData().getGoldStandardClassifications()) {
                putCodeInMap(classification.getCode());
            }
        }
        MachineLearningConfiguration.getDefaultProperties().setProperty(numCategoriesId, String.valueOf(idToCodeMap.size()));

    }

    /**
     * Returns the code that this id is mapped to.
     * @param id associated with mapped code
     * @return Code associated with this id
     */
    public Code getCode(final Integer id) {

        return idToCodeMap.get(id);
    }

    /**
     * Returns the ID that this code is mapped to.
     * @param code associated with mapped id
     * @return ID associated with this code
     */
    public Integer getID(final Code code) {

        return codeToIDMap.get(code);
    }

    /**
     * Returns the total number of output classes based on the size of the code map.
     * @return the number of output classes in the codeMap.
     */
    public int getNumberOfOutputClasses() {

        return codeToIDMap.size();
    }

    /**
     * Puts a code in the map after checking that it's valid by using the {@link CodeDictionary}.
     *
     * @param code the code to add to the map
     * @throws uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException the code not valid exception, thrown if a code is not in the {@link CodeDictionary}
     */
    private void putCodeInMap(final Code code) {

        if (!codeToIDMap.containsKey(code)) {
            createCodeAndAddToMaps(code);
        }
    }

    private void createCodeAndAddToMaps(final Code code) {

        idToCodeMap.put(currentMaxID, code);
        codeToIDMap.put(code, currentMaxID);
        currentMaxID++;
    }

    @Override
    public String toString() {

        return "CodeIndexer [idToCodeMap=" + idToCodeMap + ", codeToIDMap=" + codeToIDMap + ", currentMaxID=" + currentMaxID + "]";
    }

}
