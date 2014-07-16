package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.FormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.UniqueWordCounter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 *
 * @author jkc25, frjd2
 */
public class DataCleaning {

    /**
     * The Constant TOKENLIMIT.
     */
    private static final int TOKENLIMIT = 4;

    /**
     * The Constant SIMILARITY.
     */
    private static final double SIMILARITY = 0.85;

    /**
     * The word multiset.
     */
    private static Multiset<String> wordMultiset;
    private static Map<String, String> correctionMap;

    /**
     * Currently a dummy method.
     * Copies original string to cleaned string.
     *
     * @param bucketToClean bucket to perform cleaning on.
     * @return the bucket with cleaned records attached.
     */
    public static Bucket cleanData(final Bucket bucketToClean) {

        Bucket cleanedBucket = bucketToClean;
        //TODO dummy method, copies original data to cleaned data.
        for (Record record : cleanedBucket) {
            record.setCleanedDescription(record.getOriginalData().getDescription());
        }

        return cleanedBucket;
    }

    private static void buildCorrectionMap(final Bucket bucket) {

        correctionMap = new HashMap<>();
        for (Record record : bucket) {
            addToCorrectionMap(correctionMap, record);
        }
    }

    /**
     * Performs cleaning on the goldstandard tokensets of a record.
     *
     * @param correctionMap the correction map of tokens to corrected tokens.
     * @param record        the record to clean
     */
    private static void addToCorrectionMap(final Map<String, String> correctionMap, final Record record) {

        Set<CodeTriple> set = record.getGoldStandardClassificationSet();
        for (CodeTriple codeTriple : set) {
            TokenSet ts = codeTriple.getTokenSet();
            addToCorrectionMap(correctionMap, ts);
        }
    }

    /**
     * Performs cleaning on a {@link TokenSet}.
     *
     * @param tokenSet the tokenSet to clean.
     */
    private static void addToCorrectionMap(final Map<String, String> correctionMap, final TokenSet tokenSet) {

        for (String token : tokenSet) {
            if (wordMultiset.count(token) < TOKENLIMIT) {
                String correctedToken = correct(token);
                correctionMap.put(token, correctedToken);
            }
        }
    }

    /**
     * Corrects a token to the most similar higher occurrence term.
     *
     * @param token the token to correct
     * @return the highest similarity match
     */
    private static String correct(final String token) {

        AbstractStringMetric metric = new Levenshtein();
        List<Pair<String, Float>> possibleMatches = getPossibleMatches(token, metric);
        sortPossibleMatches(possibleMatches);
        String bestMatch = getBestMatch(token, possibleMatches);
        printDebugInfo(token, bestMatch);
        return bestMatch;
    }

    private static void printDebugInfo(final String token, final String bestMatch) {

        if (!token.equals(bestMatch)) {
            System.out.println(token + "\t corrected to \t" + bestMatch);
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
        }
        else {
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

    /**
     * Builds a {@link HashMultiset} of tokens to occurrences from all the words that appear in the gold standard
     * tokenSets.
     *
     * @param bucket to create word count map from
     */
    protected static void buildTokenOccurrenceMap(final Bucket bucket) {

        wordMultiset = HashMultiset.create();

        for (Record r : bucket) {
            Set<CodeTriple> set = r.getGoldStandardClassificationSet();
            for (CodeTriple codeTriple : set) {
                UniqueWordCounter.countWordsInLine(wordMultiset, codeTriple.getTokenSet());
            }
        }

    }

    public static void main(final String[] args) throws IOException, InputFormatException {

        File originalFile = new File(args[0]);
        File fileToWriteTo = new File(args[1]);
        runOnFile(originalFile, fileToWriteTo);
    }

    public static void runOnFile(final File file, final File correctedFile) throws IOException, InputFormatException {

        List<Record> records = FormatConverter.convert(file);
        Bucket bucket = new Bucket(records);
        buildTokenOccurrenceMap(bucket);
        buildCorrectionMap(bucket);
        correctTokensInFile(file, correctedFile);
    }

    private static void correctTokensInFile(File file, File correctedFile) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(correctedFile));
        String line;
        while ((line = br.readLine()) != null) {
            String correctedLine = correctLine(line);
            bw.write(correctedLine);
            bw.write("\n");
        }
        br.close();
        bw.close();
    }

    private static String correctLine(String line) {

        String[] commaSplits = line.split(Utils.getCSVComma());
        StringBuilder sb = new StringBuilder();
        for (String str : commaSplits) {
            String cleanedString = tokenizeAndCleanString(str);
            sb.append(cleanedString).append(",");
        }
        String correctedLine = sb.toString();
        return correctedLine.subSequence(0, correctedLine.length() - 1).toString();
    }

    private static String tokenizeAndCleanString(String str) {

        StringBuilder sb = new StringBuilder();
        for (String token : new TokenSet(str)) {
            String correctedToken = correctionMap.get(token);
            if (correctedToken != null) {
                sb.append(correctedToken).append(" ");
                if (!correctedToken.equals(token)) System.out.println("Original token: " + token + " Corrected token: " + correctedToken);
            }
            else {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }

}
