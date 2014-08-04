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

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomWordCleaner.class);
    private static final File WORD_FILE = new File(CustomWordCleaner.class.getResource("/customRemovalWords.txt").getFile());
    private static final List<String> WORD_LIST = new ArrayList<>();

    public static void main(final String... args) throws IOException, InputFormatException {

        CustomWordCleaner cleaner = new CustomWordCleaner();
        cleaner.setTokenLimit(Integer.MAX_VALUE);
        cleaner.buildWordList();
        cleaner.runOnFile(args);
    }

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

        if (token.equals("disease")) {
            System.out.println("disease");
        }
        if (WORD_LIST.contains(token)) {
            printDebugInfo(token);
            return "";
        }
        return token;
    }

    private void printDebugInfo(final String token) {

        LOGGER.info("Original token: " + token + " removed");

    }

}
