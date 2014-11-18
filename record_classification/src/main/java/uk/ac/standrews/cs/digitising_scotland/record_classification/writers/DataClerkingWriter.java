/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;

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

        writer = (BufferedWriter) ReaderWriterFactory.createBufferedWriter(outputPath);
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
