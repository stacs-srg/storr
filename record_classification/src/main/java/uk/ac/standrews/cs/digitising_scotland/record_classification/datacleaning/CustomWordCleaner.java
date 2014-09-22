package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

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
