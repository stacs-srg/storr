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
package uk.ac.standrews.cs.digitising_scotland.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

/**
 * Factory for creating buffered readers and writers from Files.
 * Created by fraserdunlop on 13/10/2014 at 09:55.
 */
public class ReaderWriterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderWriterFactory.class);

    /**
     * Constructs a {@link java.io.BufferedReader} with the default File_Charset specified in {@link uk.ac.standrews.cs.digitising_scotland.util.FileManipulation}.
     * @param inputFile the file to create the reader for
     * @return BufferedReader for the specified file
     */
    public static BufferedReader createBufferedReader(final File inputFile) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile.getAbsolutePath()), FileManipulation.FILE_CHARSET));
        }
        catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return br;

    }

    /**
     * Constructs a {@link java.io.BufferedWriter} with the default File_Charset specified in {@link uk.ac.standrews.cs.digitising_scotland.util.FileManipulation}.
     * @param outputFile the file to create the writer for.
     * @return a BufferedWriter for the specified file
     */
    public static Writer createBufferedWriter(final File outputFile) {

        Writer bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), FileManipulation.FILE_CHARSET));
        }
        catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bw;
    }
}
