package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;

/**
 * The Class HumanReadableWriter formats the fields in a record into a human readable format.
 */
public class HumanReadableWriter extends OutputDataFormatter implements Closeable {

    /** The writer. */
    private BufferedWriter writer;

    /**
     * Instantiates a new human readable writer.
     *
     * @param outputPath the output path
     * @param delimiter the delimiter
     * @throws FileNotFoundException the file not found exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public HumanReadableWriter(final File outputPath, final String delimiter) throws FileNotFoundException, UnsupportedEncodingException {

        writer = (BufferedWriter) ReaderWriterFactory.createBufferedWriter(outputPath);
        setDelimier(delimiter);
    }

    /**
     * Write this {@link Record} to file in a human readable format.
     *
     * @param record the record to be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final Record record) throws IOException {

        String recordAsString = formatRecord(record);
        writer.write(recordAsString);

    }

    /**
     * Formats the record into a human readable format.
     *
     * @param record the record to produce a string version of.
     * @return the string version of the record
     */
    private String formatRecord(final Record record) {

        String id = getID(record);
        String description = getDescription(record);
        String year = getYear(record);
        String imageQuality = getImageQuality(record);
        String sex = getSex(record);
        String ageGroup = getAgeGroup(record);
        String codes = getCodes(record);

        return id + description + year + imageQuality + sex + ageGroup + codes + "\n";
    }

    /**
     * Gets the codes.
     *
     * @param record the record
     * @return the codes
     */
    public String getCodes(final Record record) {

        StringBuilder sb = new StringBuilder();
        Set<Classification> classifications = record.getClassifications();

        for (Classification codeTriple : classifications) {

            Code code = codeTriple.getCode();
            String codeAsString = code.getCodeAsString();
            String description = getDesciption(record, code);
            sb.append(codeAsString).append(getDelimiter()).append(description).append(getDelimiter());

        }

        return sb.toString();
    }

    @Override
    public void close() throws IOException {

        writer.close();

    }

}
