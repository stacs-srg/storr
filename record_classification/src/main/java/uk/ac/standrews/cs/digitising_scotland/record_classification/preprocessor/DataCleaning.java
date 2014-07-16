package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.UniqueWordCounter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 * @author jkc25, frjd2
 *
 */
public class DataCleaning {

    /** The Constant TOKENLIMIT. */
    private static final int TOKENLIMIT = 1;

    /** The Constant SIMILARITY. */
    private static final double SIMILARITY = 0.75;

    /** The word multiset. */
    private Multiset<String> wordMultiset;

    /**
     * Currently a dummy method.
     * Copies original string to cleaned string.
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

    /**
     * Performs cleaning on the goldstandard tokensets of a record.
     *
     * @param record the record to clean
     * @return the record after cleaning
     */
    private Record cleanRecord(final Record record) {

        Set<CodeTriple> set = record.getGoldStandardClassificationSet();
        for (CodeTriple codeTriple : set) {
            TokenSet ts = codeTriple.getTokenSet();
            TokenSet cleaned = clean(ts);
        }

        return record;
    }

    /**
     * Performs cleaning on a {@link TokenSet}.
     *
     * @param tokenSet the tokenSet to clean.
     * @return the token set after cleaning
     */
    private TokenSet clean(final TokenSet tokenSet) {

        TokenSet cleaned = new TokenSet();
        Iterator<String> it = tokenSet.iterator();
        while (it.hasNext()) {
            String token = it.next();
            if (wordMultiset.count(token) < TOKENLIMIT) {
                token = correct(token);
                cleaned.add(token);
            }
            else {
                cleaned.add(token);
            }
        }
        return cleaned;
    }

    /**
     * Corrects a token to the most similar higher occurrence term.
     *
     * @param token the token to correct
     * @return the highest similarity match
     */
    private String correct(final String token) {

        AbstractStringMetric metric = new Levenshtein();
        List<Pair<String, Float>> possibleMatches = getPossibleMatches(token, metric);
        sortPossibleMatches(possibleMatches);
        String bestMatch = getBestMatch(token, possibleMatches);
        printDebugInfo(token, bestMatch);
        return bestMatch;
    }

    private void printDebugInfo(final String token, final String bestMatch) {

        if (!token.equals(bestMatch)) {
            System.out.println(token + "\t corrected to \t" + bestMatch);
        }
    }

    /**
     * Gets the possible matches.
     *
     * @param token the token
     * @param metric the metric
     * @return the possible matches
     */
    private List<Pair<String, Float>> getPossibleMatches(final String token, final AbstractStringMetric metric) {

        List<Pair<String, Float>> possibleMatches = new ArrayList<>();
        Set<String> allWords = wordMultiset.elementSet();

        for (String string : allWords) {
            if (wordMultiset.count(string) > 3) {
                float result = metric.getSimilarity(string, token);
                if (result > SIMILARITY) {
                    possibleMatches.add(new Pair<String, Float>(string, result));
                }
            }
        }
        return possibleMatches;
    }

    /**
     * Gets the best match.
     *
     * @param token the token
     * @param possibleMatches the possible matches
     * @return the best match
     */
    private String getBestMatch(final String token, final List<Pair<String, Float>> possibleMatches) {

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
    private void sortPossibleMatches(final List<Pair<String, Float>> possibleMatches) {

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
     * @param bucket to create word count map from
     */
    protected void buildTokenOccurenceMap(final Bucket bucket) {

        wordMultiset = HashMultiset.create();
        String line;

        for (Record r : bucket) {
            Set<CodeTriple> set = r.getGoldStandardClassificationSet();
            for (CodeTriple codeTriple : set) {
                line = codeTriple.getTokenSet().toString();
                UniqueWordCounter.countWordsInLine(wordMultiset, line);
            }
        }

    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    public static void main(final String[] args) throws IOException, InputFormatException {

        File training = new File(args[0]);

        Iterable<Record> records;
        records = FormatConverter.convert(training);
        Bucket bucket = new Bucket();
        bucket.addCollectionOfRecords(records);
        DataCleaning cleaner = new DataCleaning();
        cleaner.buildTokenOccurenceMap(bucket);

        for (Record record : bucket) {
            Set<CodeTriple> ct = record.getGoldStandardClassificationSet();
            for (CodeTriple codeTriple : ct) {
                TokenSet tokens = new TokenSet(codeTriple.getTokenSet());
                tokens = cleaner.clean(tokens);
            }

        }

    }
}
