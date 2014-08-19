package uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public abstract class AbstractFormatConverter {

    public abstract List<Record> convert(final File inputFile) throws IOException, InputFormatException;

    /**
     * Check line length, for modern cod data it should be 38.
     *
     * @param lineSplit the line split
     */
    protected static void checkLineLength(final String[] lineSplit, final int codeLineLength) {

        if (lineSplit.length != codeLineLength) {
            System.err.println("Line is wrong length, should be" + codeLineLength + ", is " + lineSplit.length);
        }
    }

    /**
     * Converts a string representation of an age group to the format needed by NRS.
     *
     * @param lineSplit the line split
     * @return the int
     */
    protected static int convertAgeGroup(final String lineSplit) {

        //     * TODO make sure this is the correct format

        int group = Integer.parseInt(lineSplit);
        final int max_age_group = 5;
        if (group > max_age_group) { return max_age_group; }

        return group;
    }

    /**
     * Converts sex from M or F characters to 1 or 0. 1 is male, 0 is female.
     *
     * @param sexIndicator the string to convert to binary, 1 (male) or 0 (female)
     * @return the int associated with the sex
     */
    protected static int convertSex(final String sexIndicator) {

        if (sexIndicator.equals("M")) { return 1; }
        return 0;
    }

    /**
     * Removes quotes from a string.
     *
     * @param string the string to remove quotes from
     * @return the string with quotes removed
     */
    protected static String removeQuotes(final String string) {

        String noQuotes = string.replaceAll("\"", "").trim();

        return noQuotes;
    }
}
