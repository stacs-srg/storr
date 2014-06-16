package uk.ac.standrews.cs.usp.parser.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.CODOrignalData;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;

// TODO: Auto-generated Javadoc

/**
 * Contains methods for reading and writing to file {@link Bucket}s and {@link Record}s objects ready for clerking by NRS.
 *
 * @author jkc25, frjd2
 */
public class DataClerkingWriter implements Closeable, AutoCloseable {

    /**
     * The writer.
     */
    private BufferedWriter writer;
    private static final String DELIMITER = "|";

    /**
     * Instantiates a new data clerking writer.
     *
     * @param outputPath the output path to write to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public DataClerkingWriter(final File outputPath) throws IOException {

        // writer = new BufferedWriter(new FileWriter(outputPath, true));

        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream, "UTF-8");
        writer = new BufferedWriter(outputStream);
    }

    /**
     * Write this {@link Record} to file.
     *
     * @param record the record to be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final Record record) throws IOException {

        String recordAsString = formatRecord(record);
        writer.write(recordAsString);

    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {

        writer.close();
    }

    /**
     * Format record.
     *
     * @param record the record
     * @return the string
     */
    private String formatRecord(final Record record) {

        String description = getDescription(record);
        String year = getYear(record);
        String imageQuality = getImageQuality(record);
        String sex = getSex(record);
        String ageGroup = getAgeGroup(record);
        String codes = getCodes(record);

        return description + year + imageQuality + sex + ageGroup + codes + "\n";
    }

    /**
     * Gets the image quality.
     *
     * @param record the record
     * @return the image quality
     */
    private String getImageQuality(final Record record) {

        return String.valueOf(record.getOriginalData().getImageQuality()) + DELIMITER;
    }

    /**
     * Gets the year.
     *
     * @param record the record
     * @return the year
     */
    private String getYear(final Record record) {

        return String.valueOf(record.getOriginalData().getYear()) + DELIMITER;
    }

    /**
     * Gets the description.
     *
     * @param record the record
     * @return the description
     */
    private String getDescription(final Record record) {

        return record.getOriginalData().getDescription() + DELIMITER;
    }

    /**
     * Gets the codes.
     *
     * @param record the record
     * @return the codes
     */
    private String getCodes(final Record record) {

        StringBuilder sb = new StringBuilder();

        Set<CodeTriple> classifications = record.getCodeTriples();
        for (CodeTriple codeTriple : classifications) {

            Code code = codeTriple.getCode();
            String codeAsString = code.getCodeAsString();
            String description = getDesciption(record, code);
            String explanation = getExplanation(codeAsString);
            sb.append(codeAsString + DELIMITER + description + DELIMITER + explanation //+ classification.getProvenance().getClassifier()
                            + DELIMITER);

        }

        return sb.toString();
    }

    private String getDesciption(final Record record, final Code code) {

        String description;
        if (record.isCoDRecord()) {
            description = code.getDescription();
        }
        else {
            description = "Classified into HISCO";
        }
        return description;
    }

    /**
     * Gets the explanation.
     *
     * @param codeAsString the code as string
     * @return the explanation
     */
    private String getExplanation(final String codeAsString) {

        if (codeAsString.length() == 5) {
            if (!String.valueOf(codeAsString.charAt(4)).equals("0")) { return "Coded to the extended historical version (codes ending .01 to .09)"; } //TODO test
        }

        return "";
    }

    /**
     * Gets the age group.
     *
     * @param record the record
     * @return the age group
     */
    private String getAgeGroup(final Record record) {

        if (!record.isCoDRecord()) { return ""; }
        CODOrignalData originalData = (CODOrignalData) record.getOriginalData();
        int ageGroup = originalData.getAgeGroup();

        String ageGroupString;

        switch (ageGroup) {
            case 0:
                ageGroupString = "(" + 0 + ") 0 - 1";
                break;
            case 1:
                ageGroupString = "(" + 1 + ") 2 - 5";
                break;
            case 2:
                ageGroupString = "(" + 2 + ") 6 - 10";
                break;
            case 3:
                ageGroupString = "(" + 3 + ") 11 - 15";
                break;
            case 4:
                ageGroupString = "(" + 4 + ") 16 - 45";
                break;
            default:
                ageGroupString = "(" + 5 + ") 56+";
        }
        return ageGroupString + DELIMITER;
    }

    /**
     * Gets the sex.
     *
     * @param record the record
     * @return the sex
     */
    private String getSex(final Record record) {

        if (!record.isCoDRecord()) { return ""; }
        CODOrignalData originalData = (CODOrignalData) record.getOriginalData();
        int sex = originalData.getSex();
        if (sex == 0) { return "F" + DELIMITER; }
        return "M" + DELIMITER;

    }
}
