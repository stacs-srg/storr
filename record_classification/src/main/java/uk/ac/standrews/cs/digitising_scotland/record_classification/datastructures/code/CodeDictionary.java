package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * This class contains a collection of all of the possible valid codes that can be used in training or classification.
 * If a code is not in this class then it is assumed that the code is not correct, ie a possible typo or mistaken entry.
 * @author jkc25, frjd2
 *
 */
public class CodeDictionary {

    /**
     * Map of codes strings to code descriptions.
     */
    private Map<String, Code> validCodes;

    /**
     * Instantiates a new CodeDictionary.
     * @param codeDictionaryFile
     * @throws IOException
     */
    public CodeDictionary(final File codeDictionaryFile) throws IOException {

        validCodes = initMap(codeDictionaryFile);
    }

    private Map<String, Code> initMap(final File correctCodes) throws IOException {

        validCodes = new HashMap<>();
        BufferedReader br = Utils.createBufferedReader(correctCodes);
        String line;
        while ((line = br.readLine()) != null) {
            parseLineAndAddToMap(line);
        }
        br.close();
        return validCodes;
    }

    private void parseLineAndAddToMap(final String line) {

        String[] splitLine = line.split("\t");
        String codeFromFile = splitLine[0].trim();
        String descriptionFromFile = splitLine[1].trim();
        createCodeAndAddToMap(codeFromFile, descriptionFromFile);
    }

    private void createCodeAndAddToMap(final String codeFromFile, final String descriptionFromFile) {

        Code code = new Code(codeFromFile, descriptionFromFile);
        validCodes.put(codeFromFile, code);
    }

    public Code getCode(String codeAsString) throws CodeNotValidException {

        final Code code = validCodes.get(codeAsString);
        if (code == null) { throw new CodeNotValidException(codeAsString + " is not a valid code"); }
        return code;
    }

    public int getTotalNumberOfCodes() {

        return validCodes.size();
    }

    /**
     * Returns an iterator over the validCode map.
     * @return A set of String, Code entries
     */
    public Iterator<Entry<String, Code>> getIterator() {

        return validCodes.entrySet().iterator();

    }
}
