package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import uk.ac.standrews.cs.digitising_scotland.linkage.database.BirthRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.linkage.database.DeathRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.linkage.database.MarriageRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.PercentageProgressIndicator;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;
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

    public static final String BIRTH_RECORDS_PATH = "output/birth_records.txt";
    public static final String DEATH_RECORDS_PATH = "output/death_records.txt";
    public static final String MARRIAGE_RECORDS_PATH = "output/marriage_records.txt";

    public static void main(final String[] args) throws IOException, SQLException {

        generateEventRecords();
    }

    private static void generateEventRecords() throws SQLException, IOException {

        // TODO use standard logging.
        // TODO output time elapsed.

        Diagnostic.traceNoSource("Generating birth records");
        BirthRecordIterator birth_records = new BirthRecordIterator();
        int number_of_birth_records = birth_records.size();
        exportRecords(birth_records, number_of_birth_records, BIRTH_RECORDS_PATH);

        Diagnostic.traceNoSource("Generating death records");
        DeathRecordIterator death_records = new DeathRecordIterator();
        int number_of_death_records = death_records.size();
        exportRecords(death_records, number_of_death_records, DEATH_RECORDS_PATH);

        Diagnostic.traceNoSource("Generating marriage records");
        MarriageRecordIterator marriage_records = new MarriageRecordIterator();
        int number_of_marriage_records = marriage_records.size();
        exportRecords(marriage_records, number_of_marriage_records, MARRIAGE_RECORDS_PATH);

        birth_records.close();
    }

    private static void exportRecords(final Iterable<?> records, int size, String records_path_string) throws IOException {

        Path records_path = Paths.get(records_path_string);
        FileManipulation.createParentDirectoryIfDoesNotExist(records_path);

        ProgressIndicator progress_indicator = new PercentageProgressIndicator(10);
        progress_indicator.setTotalSteps(size);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(records_path, FileManipulation.FILE_CHARSET))) {

            for (final Object record : records) {
                writer.println(record);
                progress_indicator.progressStep();
            }
        }

        if (progress_indicator.getProportionComplete() < 1.0) progress_indicator.indicateProgress(1.0);
    }
}
