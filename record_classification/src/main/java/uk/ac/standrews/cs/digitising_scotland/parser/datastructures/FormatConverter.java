package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class FormatConverter converts a comma separated text file in the format that is used by the modern cod data
 * to a list of Record objects.
 * @author jkc25
 */
public final class FormatConverter {

    static final int CODLINELENGTH = 38;
    private static final int idPosition = 0;
    private static final int agePosition = 34;
    private static final int sexPosition = 35;
    private static final int descriptionStart = 1;
    private static final int descriptionEnd = 4;
    private static final int yearPosition = 37;

    private FormatConverter() {

    }

    /**
     * Converts the data in the inputFile (one record per line, comma separated) into {@link Record}s.
     *
     * @param inputFile the input file to be read
     * @return the list of records
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public static List<Record> convert(final File inputFile) throws IOException, InputFormatException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));

        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());

            checkLineLength(lineSplit);

            int id = Integer.parseInt(lineSplit[idPosition]);
            int imageQuality = 1;
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[agePosition]));
            int sex = convertSex(removeQuotes(lineSplit[sexPosition]));
            String description = formDescription(lineSplit, descriptionStart, descriptionEnd);
            int year = Integer.parseInt(removeQuotes(lineSplit[yearPosition]));

            CODOrignalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getName());
            HashSet<CodeTriple> goldStandard = new HashSet<>();
            populateGoldStandardSet(lineSplit, goldStandard);

            Record r = new Record(originalData);
            r.getOriginalData().setGoldStandardClassification(goldStandard);
            recordList.add(r);
        }

        br.close();
        return recordList;
    }

    private static void populateGoldStandardSet(final String[] lineSplit, final HashSet<CodeTriple> goldStandard) {

        for (int i = 6; i < 31; i = i + 3) {
            if (lineSplit[i].length() != 0) {
                int causeIdentifier = Integer.parseInt(lineSplit[i]);

                if (causeIdentifier != 6) {
                    Code code = CodeFactory.getInstance().getCode(removeQuotes(lineSplit[i + 2]));

                    TokenSet tokenSet = new TokenSet(lineSplit[causeIdentifier]);

                    CodeTriple codeTriple = new CodeTriple(code, tokenSet, 1.0);
                    goldStandard.add(codeTriple);
                }
            }
        }
    }

    /**
     * Concatenates strings  between the start and end points of an array with a ',' delimiter.
     *
     * @param stringArray the String array with consecutive strings to concatenate
     * @param startPosition the first index to concatenate
     * @param endPosition the last index to concatenate
     * @return the concatenated string, comma separated
     */
    private static String formDescription(final String[] stringArray, final int startPosition, final int endPosition) {

        String description = "";

        for (int currentPosition = startPosition; currentPosition <= endPosition; currentPosition++) {
            if (stringArray[currentPosition].length() != 0) {
                if (currentPosition != startPosition) {
                    description = description + ", " + stringArray[currentPosition];
                }
                else {
                    description = stringArray[currentPosition];
                }
            }
        }

        return description;

    }

    /**
     * Converts a string representation of an age group to the format needed by NRS.
     *
     * @param lineSplit the line split
     * @return the int
     */
    private static int convertAgeGroup(final String lineSplit) {

        //     * TODO make sure this is the correct format

        int group = Integer.parseInt(lineSplit);
        if (group > 5) { return 5; }

        return group;
    }

    /**
     * Converts sex from M or F characters to 1 or 0. 1 is male, 0 is female.
     *
     * @param sexIndicator the string to convert to binary, 1 (male) or 0 (female)
     * @return the int associated with the sex
     */
    private static int convertSex(final String sexIndicator) {

        if (sexIndicator.equals("M")) { return 1; }
        return 0;
    }

    /**
     * Removes quotes from a string.
     *
     * @param string the string to remove quotes from
     * @return the string with quotes removed
     */
    private static String removeQuotes(final String string) {

        String noQuotes = string.replaceAll("\"", "").trim();

        return noQuotes;
    }

    /**
     * Check line length, for modern cod data it should be 38.
     *
     * @param lineSplit the line split
     */
    private static void checkLineLength(final String[] lineSplit) {

        if (lineSplit.length != CODLINELENGTH) {
            System.err.println("Line is wrong length, should be 38, is " + lineSplit.length);
        }
    }

}
