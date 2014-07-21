package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

/**
 * Contains methods for reading and writing to file {@link Bucket}s and {@link Record}s objects ready for clerking by NRS.
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

        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream, FileManipulation.FILE_CHARSET);
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
     * Parses the data held in a record into the expected output format.
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

}
