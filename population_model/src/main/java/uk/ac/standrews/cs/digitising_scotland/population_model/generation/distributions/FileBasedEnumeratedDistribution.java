package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.util.FileDistributionGenerator;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A distribution of strings specified in a file mapping Strings to probabilities.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class FileBasedEnumeratedDistribution extends EnumeratedDistribution {

    private static final String TAB = "\t";

    public FileBasedEnumeratedDistribution(final String path_string, final Random random) throws IOException, InconsistentWeightException {

        super(random);

        final Map<String, Double> item_probabilities = new HashMap<>();

        System.out.println("path_string: " + path_string);
        Path path = Paths.get(path_string);
        System.out.println("reading distribution from: " + path.toAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(path, FileManipulation.FILE_CHARSET)) {

            String line = reader.readLine();
            if (line.startsWith(FileDistributionGenerator.COMMENT_INDICATOR)) line = reader.readLine();

            try {
                while (line != null) {

                    final String[] strings = line.split(TAB);

                    double probability = Double.parseDouble(strings[1]);
                    item_probabilities.put(strings[0], probability);
                    line = reader.readLine();
                }
            } catch (final Exception e) {
                ErrorHandling.exceptionError(e, "Could not process line:" + line);
            }
        }

        configureProbabilities(item_probabilities);
    }
}
