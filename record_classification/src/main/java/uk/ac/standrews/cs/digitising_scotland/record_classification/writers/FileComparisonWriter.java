package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

/**
 * The Class FileComparisonWriter is used to write a human readable copy of a record to a file using a chosen delimiter.
 * This class is designed to compare the gold standard codes to classification codes.
 */
public class FileComparisonWriter extends OutputDataFormatter implements Closeable {

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
    public FileComparisonWriter(final File outputPath, final String delimiter) throws FileNotFoundException, UnsupportedEncodingException {

        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream, FileManipulation.FILE_CHARSET);
        writer = new BufferedWriter(outputStream);
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
        String codes = getCodes(record);

        return id + description + codes + "\n";
    }

    /**
     * Gets the codes.
     *
     * @param record the record
     * @return the codes
     */
    public String getCodes(final Record record) {

        final String dlim = getDelimiter();
        StringBuilder sb = new StringBuilder();
        Set<CodeTriple> classifications = record.getCodeTriples();
        Set<CodeTriple> goldStandardSet = record.getGoldStandardClassificationSet();

        for (CodeTriple goldCodeTriple : goldStandardSet) {
            Code goldCode = goldCodeTriple.getCode();

            if (Utils.contains(goldCode, classifications)) {
                sb.append("[correct]" + dlim + Utils.getCodeTripleWithCode(goldCode, classifications).getTokenSet() + dlim + goldCode.getDescription() + dlim);

            }
            else {
                sb.append("[missing]" + dlim + goldCodeTriple.getTokenSet() + dlim + goldCode.getDescription() + dlim);
            }
        }

        for (CodeTriple codeTriple : classifications) {
            Code code = codeTriple.getCode();

            if (!Utils.contains(code, goldStandardSet)) {
                sb.append("[extra]" + dlim + codeTriple.getTokenSet() + dlim + code.getDescription() + dlim);
            }
        }

        return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {

        writer.close();

    }
}
