package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * http://stackoverflow.com/questions/955110/similarity-string-comparison-in-java.
 *
 */
public final class LevenshteinDistance {

    private static final Logger LOGGER = LoggerFactory.getLogger(LevenshteinCleaner.class);

    /**
     * Instantiates a new levenshtein distance.
     */
    private LevenshteinDistance() {

        //private constructor - utility class
    }

    /**
     * Calculates the similarity between two strings.This is based on edit distance.
     * 
     * Similarity is calculated as the difference in the longest string and edit distance over the longest string.
     * @param s1 string 1
     * @param s2 string 2
     * @return the similarity between s1 and s2.
     */
    public static double similarity(final String s1, String s2) {

        if (s1.length() < s2.length()) {
            String swap = s1;
            s1 = s2;
            s2 = swap;
        }
        int bigLen = s1.length();
        if (bigLen == 0) { return 1.0; }
        return (bigLen - computeEditDistance(s1, s2)) / (double) bigLen;
    }

    /**
     * Calculates edit distance between two strings.
     * @param string1 String 1
     * @param string2 String 2
     * @return edit distance between string1 and string2
     */
    public static int computeEditDistance(final String string1, final String string2) {

        String s1 = string1.toLowerCase();
        String s2 = string2.toLowerCase();
        int[] costs = new int[s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                }
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    /**
     * Prints to screen the distance between the two input parameters.
     * @param s1 String 1
     * @param s2 String 2
     */
    public static void printDistance(final String s1, final String s2) {

        LOGGER.info(s1 + "-->" + s2 + ": " + computeEditDistance(s1, s2) + " (" + similarity(s1, s2) + ")");
    }

    /**
     * Main method. Currently runs as a test for the string similarity method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {

        printDistance("", "");
        printDistance("1234567890", "1");
        printDistance("1234567890", "12");
        printDistance("1234567890", "123");
        printDistance("1234567890", "1234");
        printDistance("1234567890", "12345");
        printDistance("1234567890", "123456");
        printDistance("1234567890", "1234567");
        printDistance("1234567890", "12345678");
        printDistance("1234567890", "123456789");
        printDistance("1234567890", "1234567890");
        printDistance("1234567890", "1234567980");

        printDistance("47/2010", "472010");
        printDistance("47/2010", "472011");

        printDistance("47/2010", "AB.CDEF");
        printDistance("47/2010", "4B.CDEFG");
        printDistance("47/2010", "AB.CDEFG");

        printDistance("The quick fox jumped", "The fox jumped");
        printDistance("The quick fox jumped", "The fox");
        printDistance("The quick fox jumped", "The quick fox jumped off the balcany");
        printDistance("The quick fox jumped", "The quick fox jumped");

        printDistance("kitten", "sitting");
        printDistance("rosettacode", "raisethysword");
        printDistance(new StringBuilder("rosettacode").reverse().toString(), new StringBuilder("raisethysword").reverse().toString());

        for (int i = 1; i < args.length; i += 2) {
            printDistance(args[i - 1], args[i]);
        }
    }
}
