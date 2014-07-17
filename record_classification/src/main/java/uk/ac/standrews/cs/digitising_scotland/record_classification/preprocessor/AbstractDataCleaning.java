package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.FormatConverter;
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
 * AbstractDataCleaning class contains the general functionality for building a spelling corrector or data cleaner.
 * Extending subclasses should implement the correct() method.
 * @author frjd2, jkc25
 * Created by fraserdunlop on 16/07/2014 at 15:21.
 */
public abstract class AbstractDataCleaning {

    private static final int DEFAULT_TOKEN_LIMIT = 4;
    private static int tokenLimit = DEFAULT_TOKEN_LIMIT;

    /**
     * The word multiset.
     */
    protected static Multiset<String> wordMultiset;

    /** The correction map. */
    private static Map<String, String> correctionMap;

    /**
     * Correct.
     *
     * @param token the token
     * @return the string
     */
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

    /**
     * Run on file.
     *
     * @param args the args
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
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

    /**
     * Builds the correction map.
     *
     * @param bucket the bucket
     */
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

        Set<CodeTriple> set = record.getGoldStandardClassificationSet();
        for (CodeTriple codeTriple : set) {
            TokenSet ts = codeTriple.getTokenSet();
            addToCorrectionMap(ts);
        }
    }

    /**
     * Performs cleaning on a {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet}.
     *
     * @param tokenSet the tokenSet to clean.
     */
    private void addToCorrectionMap(final TokenSet tokenSet) {

        for (String token : tokenSet) {
            if (wordMultiset.count(token) < tokenLimit) {
                String correctedToken = correct(token);
                correctionMap.put(token, correctedToken);
            }
        }
    }

    /**
     * Builds a {@link com.google.common.collect.HashMultiset} of tokens to occurrences from all the words that appear in the gold standard
     * tokenSets.
     *
     * @param bucket to create word count map from
     */
    private void buildTokenOccurrenceMap(final Bucket bucket) {

        wordMultiset = HashMultiset.create();

        for (Record r : bucket) {
            Set<CodeTriple> set = r.getGoldStandardClassificationSet();
            for (CodeTriple codeTriple : set) {
                UniqueWordCounter.countWordsInLine(wordMultiset, codeTriple.getTokenSet());
            }
        }
    }

    /**
     * Correct tokens in file.
     *
     * @param file the file
     * @param correctedFile the corrected file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void correctTokensInFile(final File file, final File correctedFile) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter((new FileWriter(correctedFile)));
        String line;
        while ((line = br.readLine()) != null) {
            String correctedLine = correctLine(line);
            bw.write(correctedLine);
            bw.write("\n");
        }
        br.close();
        bw.close();
    }

    /**
     * Correct line.
     *
     * @param line the line
     * @return the string
     */
    private String correctLine(final String line) {

        String[] commaSplits = line.split(Utils.getCSVComma());
        StringBuilder sb = new StringBuilder();
        for (String str : commaSplits) {
            String cleanedString = tokenizeAndCleanString(str);
            sb.append(cleanedString).append(",");
        }
        String correctedLine = sb.toString();
        return correctedLine.subSequence(0, correctedLine.length() - 1).toString();
    }

    /**
     * Tokenize and clean string.
     *
     * @param str the str
     * @return the string
     */
    private String tokenizeAndCleanString(final String str) {

        StringBuilder sb = new StringBuilder();
        for (String token : new TokenSet(str)) {
            String correctedToken = correctionMap.get(token);
            if (correctedToken != null) {
                sb.append(correctedToken).append(" ");
            }
            else {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Sets the token limit.
     *
     * @param args the new token limit
     */
    private void setTokenLimit(final String... args) {

        try {
            tokenLimit = Integer.parseInt(args[2]);
            System.out.println("TOKENLIMIT set to " + tokenLimit);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No TOKENLIMIT argument. Default is " + tokenLimit);
        }
    }
}
