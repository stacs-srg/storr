package uk.ac.standrews.cs.usp.parser.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;

// TODO: Auto-generated Javadoc
/**
 * Creates {@link Record} objects populated with data from file.
 * @author jkc25, frjd2
 *
 */
public abstract class RecordFactory {

    /** The Constant ENCODING. */
    private static final String ENCODING = "UTF-8";

    /**
     * Creates a list of {@link Record} objects from a file where the records need to be coded.
     * @param inputFile file containing original record data. Format should be description, year, count, image quality. Pipe separated.
     * @return List<Record> of {@link Record} from the file.
     * @throws IOException If file cannot be read
     * @throws InputFormatException if one or more of the lines in the inputFile are mal-formed or not valid
     */
    public static List<Record> makeUnCodedRecordsFromFile(final File inputFile) throws IOException, InputFormatException {

        boolean isCoDFile = isCauseOfDeath(inputFile);
        List<Record> recordList = new ArrayList<Record>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String line;

        while ((line = br.readLine()) != null) {

            if (isCoDFile) {
                recordList.add(createCoDRecord(inputFile, line));
            }
            else {
                recordList.add(createOccRecord(inputFile, line));
            }
        }
        br.close();
        return recordList;
    }

    /**
     * Creates an occupation Record.
     *
     * @param inputFile the input file
     * @param line the line
     * @return the record
     * @throws InputFormatException the input format exception
     */
    public static Record createOccRecord(final File inputFile, final String line) throws InputFormatException {

        String[] lineSplit = line.split("\\|");
        String description = lineSplit[0];
        int year = Integer.parseInt(lineSplit[1]);
        int imageQuality = Integer.parseInt(lineSplit[2]);
        OriginalData originalData = new OriginalData(description, year, imageQuality, inputFile.getPath());
        Record newRecord = new Record(originalData);
        // newRecord.addClassificationSet(getClassificationSet(line));

        return newRecord;
    }

    /**
     * Creates a cause of death {@link Record} from the input file and line pair.
     * @param inputFile File containing pipe separated cause of death data
     * @param line a single line from the inputFile
     * @return Record new Cause of death record containing the year, ageGroup, count and imageQuality from the file
     * @throws InputFormatException if a record cannot be read
     */
    public static Record createCoDRecord(final File inputFile, final String line) throws InputFormatException {

        String[] lineSplit = line.split("\\|");
        String description = lineSplit[5];
        int year = Integer.parseInt(lineSplit[1]);
        int ageGroup = Integer.parseInt(lineSplit[4]);
        int sex = Integer.parseInt(lineSplit[3]);
        int imageQuality = Integer.parseInt(lineSplit[2]);

        OriginalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getPath());
        Record newRecord = new Record(originalData);
        //   newRecord.addClassificationSet(getClassificationSet(line));

        return newRecord;
    }

    /**
     * Creates a list of {@link Record} objects from a file where the records have been human coded previously.
     *
     * @param inputFile file containing original record data. Format should be.... TODO
     * @return List<Record> of {@link Record} from the file.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public static List<Record> makeCodedRecordsFromFile(final File inputFile) throws IOException, InputFormatException {

        List<Record> recordList = new ArrayList<Record>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String line;

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\\|");
            int year = Integer.parseInt(lineSplit[1]);
            int imageQuality = Integer.parseInt(lineSplit[2]);
            int ageGroup = Integer.parseInt(lineSplit[3]);
            int sex = Integer.parseInt(lineSplit[4]);
            String description = lineSplit[5];
            OriginalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getPath());

            for (int i = 6; i < lineSplit.length; i++) {
                Code thisCode = CodeFactory.getInstance().getCode(lineSplit[i].trim());
                Record newRecord = createRecord(thisCode, originalData);
                recordList.add(newRecord);
            }

        }
        br.close();
        return recordList;
    }

    /**
     * Creates a new Record object.
     *
     * @param thisCode the this code
     * @param originalData the original data
     * @return the record
     */
    private static Record createRecord(final Code thisCode, final OriginalData originalData) {

        Record record = new Record(originalData);
        CodeTriple goldStandardClassification = new CodeTriple(thisCode, new TokenSet(originalData.getDescription()), 1.0);
        record.getOriginalData().getGoldStandardCodeTriples().add(goldStandardClassification);
        return record;
    }

    /**
     * Creates a list of {@link Record} objects from a file where the records have been human coded previously.
     * @param inputFile file containing original record data. Format should be.... TODO
     * @return List<Record> of {@link Record} from the file.
     */
    public static List<Record> makeCodedCauseOfDeathRecordsFromFile(final File inputFile) {

        return null;
    }

    /**
     * TODO Quick and dirty check to see if a file is a COD file. Needs to be expanded on.
     *
     * @param inputFile the input file
     * @return true, if is cause of death
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static boolean isCauseOfDeath(final File inputFile) throws IOException {

        //TODO build more comprehensive check here later
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), ENCODING));
        String firstLine = br.readLine();
        br.close();
        if (firstLine == null) { return false; }
        if (firstLine.split("\\|").length > 4) { return true; }

        return false;
    }

}
