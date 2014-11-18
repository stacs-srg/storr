/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;

// TODO: Auto-generated Javadoc
/**
 * This class contains a collection of all of the possible valid codes that can be used in training or classification.
 * If a code is not in this class then it is assumed that the code is not correct, ie a possible typo or mistaken entry.
 * @author jkc25, frjd2
 *
 */
public class CodeDictionary {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeDictionary.class);

    /**
     * Map of codes strings to code descriptions.
     */
    private Map<String, Code> validCodes;

    /**
     * Instantiates a new CodeDictionary.
     *
     * @param codeDictionaryFile the code dictionary file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public CodeDictionary(final File codeDictionaryFile) throws IOException {

        validCodes = initMap(codeDictionaryFile);
    }

    /**
     * Inits the map.
     *
     * @param correctCodes the correct codes
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Map<String, Code> initMap(final File correctCodes) throws IOException {

        validCodes = new HashMap<>();
        BufferedReader br = ReaderWriterFactory.createBufferedReader(correctCodes);
        String line;
        while ((line = br.readLine()) != null) {
            parseLineAndAddToMap(line);
        }
        br.close();
        return validCodes;
    }

    /**
     * Parses the line and add to map.
     *
     * @param line the line
     */
    private void parseLineAndAddToMap(final String line) {

        String[] splitLine = line.split("\t");
        if (splitLine.length == 2) {
            String codeFromFile = splitLine[0].trim();
            String descriptionFromFile = splitLine[1].trim();
            createCodeAndAddToMap(codeFromFile, descriptionFromFile);
        }
        else if (splitLine.length == 1) {
            String codeFromFile = splitLine[0].trim();
            String descriptionFromFile = "No Description";
            createCodeAndAddToMap(codeFromFile, descriptionFromFile);
        }
    }

    /**
     * Creates the code and add to map.
     *
     * @param codeFromFile the code from file
     * @param descriptionFromFile the description from file
     */
    private void createCodeAndAddToMap(final String codeFromFile, final String descriptionFromFile) {

        Code code = new Code(codeFromFile, descriptionFromFile);
        validCodes.put(codeFromFile, code);
    }

    /**
     * Gets the code object associated with the string representation.
     *
     * @param codeAsString the code as string
     * @return the code object
     * @throws CodeNotValidException the code not valid exception
     */
    public Code getCode(final String codeAsString) throws CodeNotValidException {

        final Code code = validCodes.get(codeAsString);
        if (code == null) {
            LOGGER.error(codeAsString + " is not a valid code", new CodeNotValidException(codeAsString + " is not a valid code"));
            throw new CodeNotValidException(codeAsString + " is not a valid code");

        }
        return code;
    }

    /**
     * Gets the total number of codes in the dictionary.
     *
     * @return the total number of codes
     */
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

    public boolean isValid(final String code) {

        return validCodes.get(code) != null;
    }
}
