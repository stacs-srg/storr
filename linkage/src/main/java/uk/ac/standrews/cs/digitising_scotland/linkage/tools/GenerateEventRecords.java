package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import uk.ac.standrews.cs.digitising_scotland.population_model.database.EventIterator;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.Diagnostic;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Generates birth/death/marriage records for all the people in the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (al@st-andrews.ac.uk)
 */
public class GenerateEventRecords {

    public static final String BIRTH_RECORDS_PATH = "linkage/output/birth_records.txt";
    public static final String DEATH_RECORDS_PATH = "linkage/output/death_records.txt";
    public static final String MARRIAGE_RECORDS_PATH = "linkage/output/marriage_records.txt";

    public static void main(final String[] args) throws IOException, SQLException {

        generateEventRecords();
    }

    private static void generateEventRecords() throws SQLException, IOException {

        // TODO add progress indicator.

        Diagnostic.traceNoSource("Generating birth records");
        exportRecords(EventIterator.getBirthRecords(), BIRTH_RECORDS_PATH);

        Diagnostic.traceNoSource("Generating death records");
        exportRecords(EventIterator.getDeathRecords(), DEATH_RECORDS_PATH);

        Diagnostic.traceNoSource("Generating marriage records");
        exportRecords(EventIterator.getMarriageRecords(), MARRIAGE_RECORDS_PATH);
    }

    private static void exportRecords(final Iterable<?> records, String records_path_string) throws IOException {

        Path records_path = Paths.get(records_path_string);
        FileManipulation.createParentDirectoryIfDoesNotExist(records_path);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(records_path, FileManipulation.FILE_CHARSET))) {

            for (final Object record : records) writer.println(record);
        }
    }
}
