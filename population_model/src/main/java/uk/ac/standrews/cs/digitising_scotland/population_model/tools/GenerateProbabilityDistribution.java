package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.util.FileDistributionGenerator;

import java.io.IOException;

/**
 * Reads in a text file, and generates a file containing the probability distribution of the lines in the input file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GenerateProbabilityDistribution {

    private static final String INPUT_FILE_FLAG = "-i";
    private static final String OUTPUT_FILE_FLAG = "-o";

    public static void main(final String[] args) throws IOException {

        String input_file_path = CommandLineArgs.getArg(args, INPUT_FILE_FLAG);
        String output_file_path = CommandLineArgs.getArg(args, OUTPUT_FILE_FLAG);

        if (input_file_path != null && output_file_path != null) {

            FileDistributionGenerator analyser = new FileDistributionGenerator();
            analyser.analyseData(input_file_path, output_file_path);
        } else {
            usage();
        }
    }

    private static void usage() {

        System.out.println("Usage: java " + GenerateProbabilityDistribution.class.getSimpleName() + " -i<input file path> -o<output file path>");
    }
}
