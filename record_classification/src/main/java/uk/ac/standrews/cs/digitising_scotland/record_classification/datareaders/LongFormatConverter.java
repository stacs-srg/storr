package uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter.checkLineLength;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter.convertAgeGroup;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter.convertSex;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter.removeQuotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CODOrignalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class FormatConverter converts a comma separated text file in the format that is used by the modern cod data
 * to a list of Record objects.
 * @author jkc25
 */
public final class LongFormatConverter extends AbstractFormatConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongFormatConverter.class);

    private static final String CHARSET_NAME = "UTF8";

    /** The Constant CODLINELENGTH. */
    static final int CODLINELENGTH = 38;

    /** The Constant idPosition. */
    private static final int ID_POSITION = 0;

    /** The Constant agePosition. */
    private static final int AGE_POSITION = 34;

    /** The Constant sexPosition. */
    private static final int SEX_POSITION = 35;

    /** The Constant descriptionStart. */
    private static final int DESC_START = 1;

    /** The Constant descriptionEnd. */
    private static final int DESC_END = 4;

    /** The Constant yearPosition. */
    private static final int YEAR_POSITION = 37;

    /**
     * Converts the data in the inputFile (one record per line, comma separated) into {@link Record}s.
     *
     * @param inputFile the input file to be read
     * @return the list of records
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public List<Record> convert(final File inputFile) throws IOException, InputFormatException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), CHARSET_NAME));

        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());

            checkLineLength(lineSplit, CODLINELENGTH);

            int id = Integer.parseInt(lineSplit[ID_POSITION]);
            int imageQuality = 1;
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[AGE_POSITION]));
            int sex = convertSex(removeQuotes(lineSplit[SEX_POSITION]));
            String description = formDescription(lineSplit, DESC_START, DESC_END);
            int year = Integer.parseInt(removeQuotes(lineSplit[YEAR_POSITION]));

            CODOrignalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getName());
            HashSet<CodeTriple> goldStandard = new HashSet<>();
            populateGoldStandardSet(lineSplit, goldStandard);

            Record r = new Record(id, originalData);
            r.getOriginalData().setGoldStandardClassification(goldStandard);

            if (goldStandard.size() == 0) {
                LOGGER.info("Gold Standard Set Empty: " + r.getDescription());
            }
            else {
                recordList.add(r);
            }
        }

        br.close();
        return recordList;
    }

    /**
     * Populate gold standard set.
     *
     * @param lineSplit the line split
     * @param goldStandard the gold standard
     */
    private static void populateGoldStandardSet(final String[] lineSplit, final HashSet<CodeTriple> goldStandard) {

        final int start_pos = 6;
        final int end_pos = 31;
        final int jump_size = 3;

        for (int i = start_pos; i < end_pos; i = i + jump_size) {
            if (lineSplit[i].length() != 0) {
                int causeIdentifier = Integer.parseInt(lineSplit[i]);

                if (causeIdentifier != start_pos) {
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
                    description = description + ", " + stringArray[currentPosition].toLowerCase();
                }
                else {
                    description = stringArray[currentPosition].toLowerCase();
                }
            }
        }

        return description;

    }

}
