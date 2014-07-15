package uk.ac.standrews.cs.digitising_scotland.linkage.event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
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

/**
 * Generates birth/death/marriage records for all the people in the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (al@st-andrews.ac.uk)
 */
public class RecordGenerator {

    public static final String BIRTH_RECORDS_PATH = "output/birth_records.txt";
    public static final String DEATH_RECORDS_PATH = "output/death_records.txt";
    public static final String MARRIAGE_RECORDS_PATH = "output/marriage_records.txt";

    // TODO allow output file paths to be configured
    // TODO add -i option to output to console

    public static final int DEFAULT_NUMBER_OF_PROGRESS_UPDATES = 10;
    private static final String NUMBER_OF_PROGRESS_UPDATES_FLAG = "-u";

    private IPopulation population;

    public RecordGenerator(IPopulation population) {

        this.population = population;
    }

    public void generateEventRecords(final String[] args) throws Exception {

        final long start_time = System.currentTimeMillis();

        final int number_of_progress_updates = CommandLineArgs.extractIntFromCommandLineArgs(args, NUMBER_OF_PROGRESS_UPDATES_FLAG, DEFAULT_NUMBER_OF_PROGRESS_UPDATES);

        // TODO use standard logging.
        Diagnostic.traceNoSource("Generating birth records");
        exportRecords(RecordIterator.getBirthRecordIterator(population), BIRTH_RECORDS_PATH, population.getNumberOfPeople(), number_of_progress_updates);
        TimeManipulation.reportElapsedTime(start_time);

        Diagnostic.traceNoSource("Generating death records");
        // The population size is an overestimate of the number of death records, but it doesn't really matter.
        exportRecords(RecordIterator.getDeathRecordIterator(population), DEATH_RECORDS_PATH, population.getNumberOfPeople(), number_of_progress_updates);
        TimeManipulation.reportElapsedTime(start_time);

        Diagnostic.traceNoSource("Generating marriage records");
        exportRecords(RecordIterator.getMarriageRecordIterator(population), MARRIAGE_RECORDS_PATH, population.getNumberOfPartnerships(), number_of_progress_updates);
        TimeManipulation.reportElapsedTime(start_time);
    }

    private static void exportRecords(final Iterable<? extends Record> records, final String records_path_string, int number_of_records, final int number_of_progress_updates) throws IOException {

        Path records_path = Paths.get(records_path_string);
        FileManipulation.createParentDirectoryIfDoesNotExist(records_path);

        ProgressIndicator progress_indicator = new PercentageProgressIndicator(number_of_progress_updates);
        progress_indicator.setTotalSteps(number_of_records);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(records_path, FileManipulation.FILE_CHARSET))) {

            for (final Record record : records) {
                writer.println(record);
                progress_indicator.progressStep();
            }
        }

        if (progress_indicator.getProportionComplete() < 1.0) {
            progress_indicator.indicateProgress(1.0);
        }
    }
}
