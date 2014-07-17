package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.IOException;
import java.util.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 *
 * @author jkc25, frjd2
 */
public class LevenShteinCleaner extends AbstractDataCleaner {

    /**
     * The Constant SIMILARITY.
     */
    private static double SIMILARITY = 0.85;


    public static void main(final String... args) throws IOException, InputFormatException {
        LevenShteinCleaner cleaner = new LevenShteinCleaner();
        setSimilarity(args);
        cleaner.runOnFile(args);
    }

    private static void setSimilarity(String... args) {
        try{
            SIMILARITY = Double.parseDouble(args[3]);
            System.out.println("SIMILARITY set to " + SIMILARITY);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No SIMILARITY argument. Default is " + SIMILARITY);
        }
    }


    /**
     * Corrects a token to the most similar higher occurrence term.
     *
     * @param token the token to correct
     * @return the highest similarity match
     */
    @Override
    public String correct(final String token) {
        AbstractStringMetric metric = new Levenshtein();
        List<Pair<String, Float>> possibleMatches = getPossibleMatches(token, metric);
        sortPossibleMatches(possibleMatches);
        String bestMatch = getBestMatch(token, possibleMatches);
        printDebugInfo(token, bestMatch);
        return bestMatch;
    }


    private void printDebugInfo(final String token, final String bestMatch) {

        if (!token.equals(bestMatch)) {
            System.out.println("Original token: " + token + " Corrected token: " + bestMatch);
        }
    }


    /**
     * Gets the possible matches.
     *
     * @param token  the token
     * @param metric the metric
     * @return the possible matches
     */
    private static List<Pair<String, Float>> getPossibleMatches(final String token, final AbstractStringMetric metric) {

        List<Pair<String, Float>> possibleMatches = new ArrayList<>();
        Set<String> allWords = wordMultiset.elementSet();

        for (String string : allWords) {
            if (wordMultiset.count(string) > wordMultiset.count(token)) {
                float result = metric.getSimilarity(string, token);
                if (result > SIMILARITY) {
                    possibleMatches.add(new Pair<>(string, result));
                }
            }
        }
        return possibleMatches;
    }

    /**
     * Gets the best match.
     *
     * @param token           the token
     * @param possibleMatches the possible matches
     * @return the best match
     */
    private static String getBestMatch(final String token, final List<Pair<String, Float>> possibleMatches) {

        String bestMatch;
        if (!possibleMatches.isEmpty()) {
            bestMatch = possibleMatches.get(possibleMatches.size() - 1).getLeft();
        } else {
            bestMatch = token;
        }
        return bestMatch;
    }

    /**
     * Sort possible matches.
     *
     * @param possibleMatches the possible matches
     */
    private static void sortPossibleMatches(final List<Pair<String, Float>> possibleMatches) {

        Comparator<Pair<String, Float>> c = new Comparator<Pair<String, Float>>() {

            @Override
            public int compare(final Pair<String, Float> o1, final Pair<String, Float> o2) {

                return o1.getRight() < o2.getRight() ? -1 : o1.getRight() > o2.getRight() ? 1 : 0;
            }
        };

        Collections.sort(possibleMatches, c);
    }
}
