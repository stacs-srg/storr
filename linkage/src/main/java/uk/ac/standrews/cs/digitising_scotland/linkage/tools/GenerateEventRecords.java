package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import uk.ac.standrews.cs.digitising_scotland.linkage.database.BirthRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.linkage.database.DeathRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.linkage.database.MarriageRecordIterator;
import uk.ac.standrews.cs.digitising_scotland.linkage.database.RecordIterator;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.PercentageProgressIndicator;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;
import uk.ac.standrews.cs.digitising_scotland.util.TimeManipulation;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;
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

    public static final int DEFAULT_NUMBER_OF_PROGRESS_UPDATES = 10;
    private static final String NUMBER_OF_PROGRESS_UPDATES_FLAG = "-u";

    public static void main(final String[] args) throws IOException, SQLException {

        generateEventRecords(args);
    }

    private static void generateEventRecords(final String[] args) throws SQLException, IOException {

        final int number_of_progress_updates = CommandLineArgs.extractIntFromCommandLineArgs(args, NUMBER_OF_PROGRESS_UPDATES_FLAG, DEFAULT_NUMBER_OF_PROGRESS_UPDATES);

        final long start_time = System.currentTimeMillis();

        // TODO use standard logging.
        Diagnostic.traceNoSource("Generating birth records");
        exportRecords(new BirthRecordIterator(), BIRTH_RECORDS_PATH, number_of_progress_updates);
        TimeManipulation.reportElapsedTime(start_time);

        Diagnostic.traceNoSource("Generating death records");
        exportRecords(new DeathRecordIterator(), DEATH_RECORDS_PATH, number_of_progress_updates);
        TimeManipulation.reportElapsedTime(start_time);

        Diagnostic.traceNoSource("Generating marriage records");
        exportRecords(new MarriageRecordIterator(), MARRIAGE_RECORDS_PATH, number_of_progress_updates);

        TimeManipulation.reportElapsedTime(start_time);
    }

    private static void exportRecords(final RecordIterator<?> records, final String records_path_string, final int number_of_progress_updates) throws IOException {

        Path records_path = Paths.get(records_path_string);
        FileManipulation.createParentDirectoryIfDoesNotExist(records_path);

        ProgressIndicator progress_indicator = new PercentageProgressIndicator(number_of_progress_updates);
        progress_indicator.setTotalSteps(records.size());

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(records_path, FileManipulation.FILE_CHARSET))) {

            for (final Object record : records) {
                writer.println(record);
                progress_indicator.progressStep();
            }
        }

        if (progress_indicator.getProportionComplete() < 1.0) {
            progress_indicator.indicateProgress(1.0);
        }

        records.close();
    }
}
