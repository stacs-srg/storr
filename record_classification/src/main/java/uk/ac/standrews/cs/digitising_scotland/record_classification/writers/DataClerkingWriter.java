package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Contains methods for writing {@link Record}s to file in the format specified by NRS.
 *
 * @author jkc25, frjd2
 */
public class DataClerkingWriter extends OutputDataFormatter implements Closeable, AutoCloseable {

    private BufferedWriter writer;

    /**
     * Instantiates a new data clerking writer.
     *
     * @param outputPath the output path to write to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public DataClerkingWriter(final File outputPath) throws IOException {

        writer = (BufferedWriter) Utils.createBufferedWriter(outputPath);
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
     * Parses the data held in a record into the expected output format.
     *
     * @param record the record
     * @return the string
     */
    private String formatRecord(final Record record) {

        StringBuilder sb = new StringBuilder();
        char fieldID = 'a';
        for (String description : record.getDescription()) {
            int id = record.getid();
            String year = getYear(record);
            String codes = getCodes(record, description);
            sb.append(year + id + "|" + fieldID + "|" + description + "|" + codes + "\n");
            fieldID++;
        }

        return sb.toString();
    }
}
