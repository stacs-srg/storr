package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.util;

import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Logic to read in a text file, and generate a file containing the probability distribution of the lines in the input file.
 * Each line of the output file contains a string and a probability, separated by a tab character.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class FileDistributionGenerator {

    public static final String COMMENT_INDICATOR = "%";
    private static final String TAB = "\t";

    public void analyseData(Path input_data_file_path, Path output_file_path) throws IOException {

        recordProbabilities(getStringCounts(input_data_file_path), input_data_file_path, output_file_path);
    }

    public void analyseData(String input_data_file_path, String output_file_path) throws IOException {

        analyseData(Paths.get(input_data_file_path), Paths.get(output_file_path));
    }

    private Map<String, Integer> getStringCounts(Path input_data_file_path) throws IOException {

        Map<String, Integer> string_counts = new TreeMap<>();

        try (final BufferedReader reader = Files.newBufferedReader(input_data_file_path, FileManipulation.FILE_CHARSET)) {

            String line = reader.readLine();

            while (line != null) {

                recordString(line, string_counts);
                line = reader.readLine();
            }
        }

        return string_counts;
    }

    private void recordString(final String s, final Map<String, Integer> string_counts) {

        try {
            String processed_string = s;
            int count = 1;

            // Allow for a possible count at the end of the string.
            final String[] split = s.split(TAB);

            if (split.length > 1) {
                String count_string = split[split.length - 1];

                count = Integer.parseInt(count_string);
                processed_string = s.substring(0, s.length() - count_string.length() - 1);
            }

            int previous_count = string_counts.containsKey(processed_string) ? string_counts.get(processed_string) : 0;
            string_counts.put(processed_string, previous_count + count);

        } catch (NumberFormatException e) {
            ErrorHandling.exceptionError(e, "invalid count on line: " + s);
        }
    }

    private void recordProbabilities(Map<String, Integer> string_counts, Path input_file_path, Path output_file_path) throws IOException {

        int total_count = getTotalCount(string_counts);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output_file_path, FileManipulation.FILE_CHARSET))) {

            outputComment(writer, input_file_path);

            for (Map.Entry<String, Integer> entry : string_counts.entrySet()) {

                double probability = calculateProbability(entry.getValue(), total_count);
                outputProbability(writer, entry.getKey(), probability);
            }
        }
    }

    private int getTotalCount(Map<String, Integer> string_counts) {

        int count = 0;
        for (int i : string_counts.values()) count += i;
        return count;
    }

    private double calculateProbability(int string_count, int total_count) {

        return ((double) string_count) / ((double) total_count);
    }

    private void outputComment(PrintWriter writer, Path input_file_path) {

        writer.println(COMMENT_INDICATOR + " Generated from source file: " + input_file_path + " at " + getTimestamp() + ".");
    }

    private String getTimestamp() {

        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

    private void outputProbability(PrintWriter writer, String s, double probability) {

        writer.print(s);
        writer.print("\t");
        writer.println(probability);
    }
}
