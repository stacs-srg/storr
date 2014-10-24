package uk.ac.standrews.cs.digitising_scotland.record_classification.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Analyses the distribution of unique strings within a file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @see <a href="http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/scripts/analyse_unique_records.html">Launch script documentation</a>
 */
public class AnalyseUniqueRecords {

    private static Random random = new Random(3495792347L);

    private static Charset FILE_CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) throws IOException {

        if (argsValid(args)) {

            final String input_path_string = args[0];
            final String output_path_string = args[1];

            final int number_of_samples = Integer.parseInt(args[2]);
            final int number_of_repetitions = Integer.parseInt(args[3]);

            final int prefix_length = args.length > 4 ? Integer.parseInt(args[4]) : 0;
            final int suffix_length = args.length > 5 ? Integer.parseInt(args[5]) : 0;

            getFileEncoding(args);

            analyseStringsInFile(input_path_string, output_path_string, number_of_samples, number_of_repetitions, prefix_length, suffix_length);
        }
    }

    private static void getFileEncoding(String[] args) {

        if (args.length > 6) {
            FILE_CHARSET = Charset.forName(args[6]);
        }
    }

    private static boolean argsValid(String[] args) {

        if (args.length < 4) {
            System.err.println("Usage: java UniqueAnalysis <input file path> <output file path> <number of samples> <number of repetitions>");
            return false;
        }

        if (!Files.exists(Paths.get(args[0]))) {
            System.err.println("Error: can't open input file " + args[0] + " in directory " + System.getProperty("user.dir"));
            return false;
        }

        if (Files.exists(Paths.get(args[1]))) {
            System.err.println("Error: output file " + args[0] + " already exists in directory " + System.getProperty("user.dir"));
            return false;
        }

        return true;
    }

    private static void analyseStringsInFile(String input_path_string, String output_path_string, int number_of_samples, int number_of_repetitions, int prefix_length, int suffix_length) throws IOException {

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(output_path_string), FILE_CHARSET))) {

            long start = System.currentTimeMillis();

            int[][] results = new int[number_of_repetitions][];

            String[] strings = loadStringsFromFile(input_path_string, prefix_length, suffix_length);
            if (number_of_samples > strings.length) number_of_samples = strings.length;
            int sample_interval = strings.length / (number_of_samples - 1);

            for (int i = 0; i < number_of_repetitions; i++) {

//                String[] permuted_strings = permute(strings, permutation(strings.length, random));
                permute(strings, random);
//                results[i] = analyse(permuted_strings, number_of_samples, sample_interval);
                results[i] = analyse(strings, number_of_samples, sample_interval);
                System.out.println("Completed repetition " + (i + 1));
            }

            System.out.println("Completed processing in " + (System.currentTimeMillis() - start) / 1000 + "s");

            printAnalysis(results, sample_interval, strings.length, writer, input_path_string);
        }
    }

    public static int[] analyse(String[] strings, int number_of_samples, int sample_interval) {

        int result[] = new int[number_of_samples];

        Set<String> encountered = new HashSet<>();

        int j = 0;
        for (int i = 0; i < strings.length; i++) {

            encountered.add(strings[i]);
            if (i % sample_interval == 0) {
                result[j++] = encountered.size();
            }
        }

        if (j < result.length - 1) {
            result[j] = encountered.size();
        }

        return result;
    }

    private static void printAnalysis(int[][] results, int sample_interval, int number_of_strings, PrintWriter writer, String input_path_string) {

        writer.println(new Date());
        writer.println("Input file: " + input_path_string);
        writer.println("Number of strings: " + number_of_strings);
        writer.println("Number of samples: " + results[0].length);
        writer.println("Number of repetitions: " + results.length);
        writer.println();

        writer.print("position\t");
        for (int r = 1; r <= results.length; r++)
            writer.print("r" + r + "\t");
        writer.println();

        for (int i = 0; i < results[0].length; i++) {

            if (i < results[0].length - 1) {
                writer.print((i * sample_interval + 1) + "\t");
            }
            else {
                writer.print(number_of_strings + "\t");
            }

            for (int j = 0; j < results.length; j++) {

                writer.print(results[j][i] + "\t");
            }
            writer.println();
        }
    }

    private static String[] loadStringsFromFile(String path_string, int prefix_length, int suffix_length) throws IOException {

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(path_string), FILE_CHARSET)) {

            List<String> list = new ArrayList<>();

            String line = getLine(reader, prefix_length, suffix_length);
            int line_count = 0;
            int ignored_lines = 0;

            while (line != null) {
                if (notIgnored(line)) {
                    list.add(line);
                }
                else {
                    ignored_lines++;
                }

                line_count++;
                printProgress(line_count);
                line = getLine(reader, prefix_length, suffix_length);
            }

            System.out.println("Read " + line_count + " lines.");
            System.out.println("Ignored " + ignored_lines + " lines.");
            return list.toArray(new String[0]);
        }
    }

    private static String getLine(BufferedReader reader, int prefix_length, int suffix_length) throws IOException {

        final String line = reader.readLine();
        if (line == null) return null;
        final int length = line.length();
        return length > prefix_length + suffix_length ? line.substring(prefix_length, length - suffix_length) : "";
    }

    private static void printProgress(int line_count) {

        if (line_count >= 1000 && hasOneSigFig(line_count)) {
            System.out.println("Read " + line_count + " lines.");
        }
    }

    private static boolean hasOneSigFig(int n) {

        int i = n;
        int j = 1;
        while (i >= 10) {
            i = i / 10;
            j *= 10;
        }

        return i * j == n;
    }

    private static boolean notIgnored(String line) {

        return line.length() > 0;
    }

    private static int[] permutation(int size, Random random) {

        int[] result = new int[size];

        List<Integer> array_list = new ArrayList();
        for (int i = 0; i < size; i++) {
            array_list.add(i);
        }

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(size - i);
            result[i] = array_list.get(index);
            array_list.remove(index);
        }

        return result;
    }

    private static void permute(String[] strings, Random random) {

        final int length = strings.length;

        for (int i = 0; i < length; i++) {
            swap(strings, i, random.nextInt(length));
        }
    }

    private static void swap(String[] strings, int i, int j) {

        String temp = strings[i];
        strings[i] = strings[j];
        strings[j] = temp;
    }

    private static String[] permute(String[] strings, int[] permutation) {

        String[] result = new String[strings.length];

        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[permutation[i]];
        }

        return result;
    }
}
