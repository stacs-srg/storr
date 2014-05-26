package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.ProgressIndicator;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToDB;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.nds.util.Diagnostic;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Generates a population in a series of independent batches, and exports to the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GeneratePopulation {

    private static final String BATCH_SIZE_FLAG = "-b";
    private static final String NUMBER_OF_BATCHES_FLAG = "-n";
    private static final String NUMBER_OF_PROGRESS_UPDATES_FLAG = "-u";

    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_NUMBER_OF_BATCHES = 1;
    public static final int DEFAULT_NUMBER_OF_PROGRESS_UPDATES = 10;

    public static void main(final String[] args) throws IOException, InconsistentWeightException, SQLException, NegativeDeviationException, NegativeWeightException {

        final int batch_size = CommandLineArgs.extractIntFromCommandLineArgs(args, BATCH_SIZE_FLAG, DEFAULT_BATCH_SIZE);
        final int number_of_batches = CommandLineArgs.extractIntFromCommandLineArgs(args, NUMBER_OF_BATCHES_FLAG, DEFAULT_NUMBER_OF_BATCHES);
        final int number_of_progress_updates = CommandLineArgs.extractIntFromCommandLineArgs(args, NUMBER_OF_PROGRESS_UPDATES_FLAG, DEFAULT_NUMBER_OF_PROGRESS_UPDATES);

        if (batch_size > 0 && number_of_batches > 0 && number_of_progress_updates > 0) {

            showInfo(batch_size, number_of_batches);

            for (int batch_number = 0; batch_number < number_of_batches; batch_number++) {
                generateBatch(batch_size, batch_number, number_of_progress_updates);
            }

            showInfo(batch_size, number_of_batches);
        }
        else usage();
    }

    private static void showInfo(int batch_size, int number_of_batches) {

        Diagnostic.traceNoSource(number_of_batches + " batch" + (number_of_batches > 1 ? "es" : "") + " of population size " + batch_size);
    }

    private static void generateBatch(int batch_size, int batch_number, int number_of_progress_updates) throws IOException, InconsistentWeightException, SQLException, NegativeDeviationException, NegativeWeightException {

        Diagnostic.traceNoSource("Generating batch " + (batch_number + 1));

        final CompactPopulation population = new CompactPopulation(batch_size, getProgressIndicator(number_of_progress_updates));
        final PopulationToDB exporter = new PopulationToDB(population, getProgressIndicator(number_of_progress_updates));

        Diagnostic.traceNoSource("Exporting to database");
        exporter.export();
    }

    private static ProgressIndicator getProgressIndicator(int number_of_progress_updates) {

        return new ProgressIndicator(number_of_progress_updates) {
            @Override
            public void indicateProgress(double proportion_complete) {
                Diagnostic.traceNoSource(Math.round(proportion_complete * 100) + "%");
            }
        };
    }

    private static void usage() {

        System.out.println("Usage: java " + GeneratePopulation.class.getSimpleName() + " " +
                BATCH_SIZE_FLAG + "<batch size> " +
                NUMBER_OF_BATCHES_FLAG + "<number of batches> " +
                NUMBER_OF_PROGRESS_UPDATES_FLAG + "<number of progress updates>");
    }
}
