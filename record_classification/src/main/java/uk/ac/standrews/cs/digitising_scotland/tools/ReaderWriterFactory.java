package uk.ac.standrews.cs.digitising_scotland.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.*;

/**
 * Factory for creating buffered readers and writers from Files.
 * Created by fraserdunlop on 13/10/2014 at 09:55.
 */
public class ReaderWriterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * Constructs a {@link java.io.BufferedReader} with the default File_Charset specified in {@link uk.ac.standrews.cs.digitising_scotland.util.FileManipulation}.
     * @param inputFile the file to create the reader for
     * @return BufferedReader for the specified file
     */
    public static BufferedReader createBufferedReader(final File inputFile) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), FileManipulation.FILE_CHARSET));
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
