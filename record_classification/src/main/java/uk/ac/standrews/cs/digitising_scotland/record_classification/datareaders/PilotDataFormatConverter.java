package uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CODOrignalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class FormatConverter converts a comma separated text file in the format that is used by the modern cod data
 * to a list of Record objects.
 * @author jkc25
 */
public final class PilotDataFormatConverter extends AbstractFormatConverter {

    private static final String CHARSET_NAME = "UTF8";

    /** The Constant CODLINELENGTH. */
    static final int CODLINELENGTH = 8;

    /** The Constant idPosition. */
    private static final int ID_POSITION = 1;

    /** The Constant agePosition. */
    private static final int AGE_POSITION = 3;

    /** The Constant sexPosition. */
    private static final int SEX_POSITION = 2;

    /** The Constant descriptionStart. */
    private static final int DESC_START = 4;

    /** The Constant descriptionEnd. */
    private static final int DESC_END = 6;

    /** The Constant yearPosition. */
    private static final int YEAR_POSITION = 0;

    private static final int IMAGE_QUALITY_POS = 7;

    /**
     * Converts the data in the inputFile (one record per line, comma separated) into {@link Record}s.
     *
     * @param inputFile the input file to be read
     * @return the list of records
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public List<Record> convert(final File inputFile, final CodeDictionary codeDictionary) throws IOException, InputFormatException {

        BufferedReader br = Utils.createBufferedReader(inputFile);

        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\t");

            checkLineLength(lineSplit, CODLINELENGTH);

            int id = Integer.parseInt(lineSplit[ID_POSITION]);
            int imageQuality = parseImageQuality(lineSplit);
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[AGE_POSITION]));
            int sex = convertSex(removeQuotes(lineSplit[SEX_POSITION]));
            List<String> description = formDescription(lineSplit, DESC_START, DESC_END);
            int year = Integer.parseInt(removeQuotes(lineSplit[YEAR_POSITION]));

            CODOrignalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getName());
            HashSet<Classification> goldStandard = new HashSet<>();

            Record r = new Record(id, originalData);
            r.getOriginalData().setGoldStandardClassification(goldStandard);

            recordList.add(r);

        }

        br.close();
        return recordList;
    }

    private static int parseImageQuality(final String[] lineSplit) {

        if (lineSplit[IMAGE_QUALITY_POS].equalsIgnoreCase("null")) {
            return 0;
        }
        else {
            return Integer.parseInt(lineSplit[IMAGE_QUALITY_POS]);
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
    private static List<String> formDescription(final String[] stringArray, final int startPosition, final int endPosition) {

        List<String> descriptionList = new ArrayList<>();

        for (int currentPosition = startPosition; currentPosition <= endPosition; currentPosition++) {
            if (stringArray[currentPosition].length() != 0 && !stringArray[currentPosition].equalsIgnoreCase("null")) {
                descriptionList.add(stringArray[currentPosition].toLowerCase());

            }
        }

        return descriptionList;

    }
}
