package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.nds.util.CommandLineArgs;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.transform.PopulationToGraphviz;

import java.io.IOException;

/**
 * Manual test of Graphviz export.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GeneratePopulationInGraphvizFile {

    private static final String FILE_FLAG = "f";
    private static final String SIZE_FLAG = "s";

    public static void main(final String[] args) throws IOException, InconsistentWeightException, NegativeDeviationException, NegativeWeightException {

        String path_string = CommandLineArgs.getArg(args, FILE_FLAG);
        String population_size_string = CommandLineArgs.getArg(args, SIZE_FLAG);

        if (path_string != null && population_size_string != null) {

            int population_size = Integer.parseInt(population_size_string);
            final CompactPopulation population = new CompactPopulation(population_size);

            final PopulationToGraphviz exporter = new PopulationToGraphviz(population, path_string);
            System.out.println("exporting...");

            exporter.export();
            System.out.println("done");
        }
        else usage();
    }

    private static void usage() {

        System.out.println("Usage: java GeneratePopulationInGraphvizFile -f<file path> -s<population size>");
    }
}
