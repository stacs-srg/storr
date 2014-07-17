package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.FormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.UniqueWordCounter;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fraserdunlop on 16/07/2014 at 15:21.
 */
public abstract class AbstractDataCleaning {
    /**
     * The Constant TOKENLIMIT.
     */
    private static int TOKENLIMIT = 4;

    /**
     * The word multiset.
     */
    protected static Multiset<String> wordMultiset;
    private static Map<String, String> correctionMap;

    public abstract String correct(final String token);

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

    public void runOnFile(final String... args) throws IOException, InputFormatException {
        File file = new File(args[0]);
        File correctedFile = new File(args[1]);
        setTokenLimit(args);
        List<Record> records = FormatConverter.convert(file);
        Bucket bucket = new Bucket(records);
        buildTokenOccurrenceMap(bucket);
        buildCorrectionMap(bucket);
        correctTokensInFile(file, correctedFile);
    }

    private void buildCorrectionMap(final Bucket bucket) {
        correctionMap = new HashMap<>();
        for (Record record : bucket) {
            addToCorrectionMap(record);
        }
    }

    /**
     * Performs cleaning on the goldstandard tokensets of a record.
     *
     * @param record the record to clean
     */
    private void addToCorrectionMap(final Record record) {
        final Set<CodeTriple> set = record.getGoldStandardClassificationSet();
        for (final CodeTriple codeTriple : set) {
            addToCorrectionMap(codeTriple.getTokenSet());
        }
    }

    /**
     * Performs cleaning on a {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet}.
     *
     * @param tokenSet the tokenSet to clean.
     */
    private void addToCorrectionMap(final TokenSet tokenSet) {
        for (final String token : tokenSet) {
            if (wordMultiset.count(token) < TOKENLIMIT) {
                correctionMap.put(token, correct(token));
            }
        }
    }

    /**
     * Builds a {@link com.google.common.collect.HashMultiset} of tokens to occurrences from all the words that appear in the gold standard
     * tokenSets.
     *
     * @param bucket to create word count map from
     */
    private static void buildTokenOccurrenceMap(final Bucket bucket) {

        wordMultiset = HashMultiset.create();

        for (final Record r : bucket) {
            final Set<CodeTriple> set = r.getGoldStandardClassificationSet();
            for (final CodeTriple codeTriple : set) {
                UniqueWordCounter.countWordsInLine(wordMultiset, codeTriple.getTokenSet());
            }
        }
    }

    private static void correctTokensInFile(final File file, final File correctedFile) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(fileToPath(file), FileManipulation.FILE_CHARSET)) {
            try (final BufferedWriter writer = Files.newBufferedWriter(fileToPath(correctedFile), FileManipulation.FILE_CHARSET)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(correctLine(line));
                    writer.write("\n");
                }
            }
        }
    }

    private static Path fileToPath(final File file) {

        return Paths.get(file.getAbsolutePath());
    }

    private static String correctLine(final String line) {
        final String[] commaSplits = line.split(Utils.getCSVComma());
        final StringBuilder sb = new StringBuilder();
        for (final String str : commaSplits) {
            sb.append(tokenizeAndCleanString(str)).append(',');
        }
        final String correctedLine = sb.toString();
        return correctedLine.subSequence(0, correctedLine.length() - 1).toString();
    }

    private static String tokenizeAndCleanString(final String str) {
        final StringBuilder sb = new StringBuilder();
        for (final String token : new TokenSet(str)) {
            final String correctedToken = correctionMap.get(token);
            if (correctedToken != null) {
                sb.append(correctedToken).append(' ');
            } else {
                sb.append(token).append(' ');
            }
        }
        return sb.toString().trim();
    }

    private static void setTokenLimit(final String... args) {
        try{
            TOKENLIMIT = Integer.parseInt(args[2]);
            System.out.println("TOKENLIMIT set to " + TOKENLIMIT);
        } catch (final ArrayIndexOutOfBoundsException e) {
            System.out.println("No TOKENLIMIT argument. Default is " + TOKENLIMIT);
        }
    }
}
