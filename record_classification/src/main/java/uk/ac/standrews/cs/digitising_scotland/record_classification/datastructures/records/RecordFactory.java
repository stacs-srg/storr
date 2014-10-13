package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CODOrignalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;

/**
 * Creates {@link Record} objects populated with data from file.
 * @author jkc25, frjd2
 *
 */
public abstract class RecordFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordFactory.class);
    private static int highestId = 0;

    private RecordFactory() {

    }

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
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
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
        List<String> description = new ArrayList<>();
        description.add(lineSplit[0]);
        int year = Integer.parseInt(lineSplit[1]);
        int imageQuality = Integer.parseInt(lineSplit[2]);
        OriginalData originalData = new OriginalData(description, year, imageQuality, inputFile.getPath());
        int id = highestId++;
        return new Record(id, originalData);
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
        final int descriptionPos = 5;
        final int idPos = 0;
        final int yearPos = 1;
        final int ageGroupPos = 4;
        final int sexPos = 3;
        final int imageQualityPos = 2;

        List<String> description = getCodDescriptionFromFile(lineSplit, descriptionPos);
        int id = Integer.parseInt(lineSplit[idPos]);
        int year = Integer.parseInt(lineSplit[yearPos]);
        int ageGroup = Integer.parseInt(lineSplit[ageGroupPos]);
        int sex = Integer.parseInt(lineSplit[sexPos]);
        int imageQuality = Integer.parseInt(lineSplit[imageQualityPos]);

        OriginalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getPath());

        return new Record(id, originalData);
    }

    private static List<String> getCodDescriptionFromFile(final String[] lineSplit, final int descriptionPos) {

        List<String> description = new ArrayList<>();

        for (int i = descriptionPos; i < lineSplit.length; i++) {
            description.add(lineSplit[i].intern());

        }
        return description;
    }

    /**
     * Creates a list of {@link Record} objects from a file where the records have been human coded previously.
     *
     * @param inputFile file containing original record data. Format should be.... TODO
     * @return List<Record> of {@link Record} from the file.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     * @throws NumberFormatException 
     */
    public static List<Record> makeCodedCodRecordsFromFile(final File inputFile, final CodeDictionary codeDictionary) throws InputFormatException, NumberFormatException, IOException {

        List<Record> recordList = null;
        if (isCodFile(inputFile)) {
            recordList = makeCoDRecordsFromFile(inputFile, codeDictionary);
        }
        else {
            recordList = makeOccupationRecordsFromFile(inputFile, codeDictionary);

        }

        return recordList;
    }

    private static boolean isCodFile(File inputFile) throws IOException {

        //Todo build more comprehensive test
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
        String line;

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\\|");
            if (lineSplit.length == 7) {
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    private static List<Record> makeOccupationRecordsFromFile(final File inputFile, final CodeDictionary codeDictionary) throws IOException, InputFormatException {

        final String delimiter = "\\|";
        final int idPos = 0;
        final int yearPos = 4;
        final int sexPos = 3;
        final int descriptionPos = 5;
        final int codePos = 6;

        List<Record> recordList = new ArrayList<Record>();
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
        String line;

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(delimiter);
            int id = Integer.parseInt(lineSplit[idPos]);
            int year = Integer.parseInt(lineSplit[yearPos]);
            List<String> description = getOCCDescriptionFromFile(lineSplit, descriptionPos);
            OriginalData originalData = new OriginalData(description, year, 1, inputFile.getName());

            Code thisCode = getCode(codeDictionary, lineSplit, codePos);
            Record newRecord = createRecord(id, thisCode, originalData);
            recordList.add(newRecord);

        }
        closeReader(br);
        return recordList;
    }

    private static List<String> getOCCDescriptionFromFile(String[] lineSplit, int descriptionPos) {

        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(lineSplit[descriptionPos]);
        return descriptionList;
    }

    private static List<Record> makeCoDRecordsFromFile(final File inputFile, final CodeDictionary codeDictionary) throws IOException, InputFormatException {

        final int yearPos = 1;
        final int imageQualityPos = 2;
        final int ageGroupPos = 3;
        final int sexPos = 4;
        final int descriptionPos = 5;

        List<Record> recordList = new ArrayList<Record>();
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
        String line;

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\\|");
            int year = Integer.parseInt(lineSplit[yearPos]);
            int imageQuality = Integer.parseInt(lineSplit[imageQualityPos]);
            int ageGroup = Integer.parseInt(lineSplit[ageGroupPos]);
            int sex = Integer.parseInt(lineSplit[sexPos]);
            List<String> description = getCodDescriptionFromFile(lineSplit, descriptionPos);
            OriginalData originalData = new CODOrignalData(description, year, ageGroup, sex, imageQuality, inputFile.getPath());

            for (int i = 6; i < lineSplit.length; i++) {
                Code thisCode = getCode(codeDictionary, lineSplit, i);
                Record newRecord = createRecord(thisCode, originalData);
                recordList.add(newRecord);
            }

        }
        closeReader(br);
        return recordList;
    }

    private static Code getCode(final CodeDictionary codeDictionary, final String[] lineSplit, final int i) {

        Code thisCode = null;
        try {
            thisCode = codeDictionary.getCode(lineSplit[i].trim());
        }
        catch (CodeNotValidException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return thisCode;
    }

    private static void closeReader(final BufferedReader br) {

        try {
            br.close();
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Creates a new Record object.
     *
     * @param thisCode the this code
     * @param originalData the original data
     * @return the record
     */
    private static Record createRecord(final Code thisCode, final OriginalData originalData) {

        final int scaleFactor = 1000;
        int id = (int) Math.rint(Math.random() * scaleFactor);
        Record record = new Record(id, originalData);
        Classification goldStandardClassification = new Classification(thisCode, new TokenSet(originalData.getDescription()), 1.0);
        record.getOriginalData().getGoldStandardClassifications().add(goldStandardClassification);
        return record;
    }

    /**
     * Creates a new Record object.
     *
     * @param thisCode the this code
     * @param originalData the original data
     * @return the record
     */
    private static Record createRecord(final int ID, final Code thisCode, final OriginalData originalData) {

        int id = ID;
        Record record = new Record(id, originalData);
        Classification goldStandardClassification = new Classification(thisCode, new TokenSet(originalData.getDescription()), 1.0);
        record.getOriginalData().getGoldStandardClassifications().add(goldStandardClassification);
        return record;
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
        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);
        String firstLine = br.readLine();
        br.close();
        return firstLine != null && firstLine.split("\\|").length > 4;
    }

    /**
     * Resets the ID count to 0.
     */
    public static void resetIdCount() {

        highestId = 0;
    }

}
