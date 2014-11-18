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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 *
 * @author jkc25, frjd2
 */
public class CustomWordCleaner extends AbstractDataCleaner {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomWordCleaner.class);

    /** The Constant WORD_FILE. */
    private static final File WORD_FILE = new File(CustomWordCleaner.class.getResource("/customRemovalWords.txt").getFile());

    /** The Constant WORD_LIST. */
    private static final List<String> WORD_LIST = new ArrayList<>();

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     * @throws CodeNotValidException 
     */
    public static void main(final String... args) throws IOException, InputFormatException, CodeNotValidException {

        AbstractDataCleaner.setTokenLimit(Integer.MAX_VALUE);
        CustomWordCleaner cleaner = new CustomWordCleaner();
        cleaner.buildWordList();
        cleaner.runOnFile(args);
    }

    /**
     * Builds the word list.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void buildWordList() throws IOException {

        String line = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(WORD_FILE), FileManipulation.FILE_CHARSET));

        while ((line = in.readLine()) != null) {
            WORD_LIST.add(line.trim().toLowerCase());
        }

        in.close();
    }

    /**
     * Corrects a token to the most similar higher occurrence term.
     *
     * @param token the token to correct
     * @return the highest similarity match
     */
    @Override
    public String correct(final String token) {

        if (WORD_LIST.contains(token)) {
            printDebugInfo(token);
            return "";
        }
        return token;
    }

    /**
     * Prints the debug info.
     *
     * @param token the token
     */
    private void printDebugInfo(final String token) {

        LOGGER.info("Original token: " + token + " removed");

    }

}
