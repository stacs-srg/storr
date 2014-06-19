package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());

            checkLineLength(lineSplit);

            int id = Integer.parseInt(lineSplit[0]);
            int imageQuality = 1;
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[34]));
            int sex = convertSex(removeQuotes(lineSplit[35]));
            String description = formDescription(lineSplit, 1, 4);
            int year = Integer.parseInt(removeQuotes(lineSplit[37]));

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

    private static void populateGoldStandardSet(String[] lineSplit, HashSet<CodeTriple> goldStandard) {

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
     * Form description.
     *
     * @param lineSplit the line split
     * @param i the i
     * @param j the j
     * @return the string
     */
    private static String formDescription(final String[] lineSplit, final int i, final int j) {

        String description = "";

        for (int k = i; k <= j; k++) {
            if (lineSplit[k].length() != 0) {
                if (k != i) {
                    description = description + "," + lineSplit[k];
                }
                else {
                    description = lineSplit[k];
                }
            }
        }

        return description;

    }

    /**
     * Convert age group.
     *
     * @param lineSplit the line split
     * @return the int
     */
    private static int convertAgeGroup(final String lineSplit) {

        int group = Integer.parseInt(lineSplit);
        if (group > 5) { return 5; }

        return group;
    }

    /**
     * Convert sex.
     *
     * @param lineSplit the line split
     * @return the int
     */
    private static int convertSex(final String lineSplit) {

        if (lineSplit.equals("M")) { return 1; }
        return 0;
    }

    /**
     * Removes the quotes.
     *
     * @param string the string
     * @return the string
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
