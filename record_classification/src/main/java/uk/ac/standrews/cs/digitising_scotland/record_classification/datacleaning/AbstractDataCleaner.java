package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
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
 * Base class for all data cleaners. Subclasses must implement a correct method which maps incorrect
 * spellings to correct spellings.
 * Created by fraserdunlop on 16/07/2014 at 15:21.
 */
public abstract class AbstractDataCleaner {

    /**
     * The Constant TOKENLIMIT. The default is 4 but this can be changed by calling setTokenLimit.
     */
    private static int tokenLimit = 4;

    AbstractFormatConverter formatConverter = new LongFormatConverter();

    /**
     * The word multiset.
     */
    private static Multiset<String> wordMultiset;

    /** The correction map. */
    private static Map<String, String> correctionMap;

    /**
     * Should perform a correction if applicable to the given token.
     * Exact behaviour is implemented by extending classes.
     * @param token String to clean
     * @return String corrected token
     */
    public abstract String correct(final String token);

    /**
     * Perform the data cleaning step on the file supplied in the arguments.
     * @param args 1 is the input file path, 2 is the output file path, 3 (optional) sets TOKENLIMIT which
     *             states the frequency of occurrence below which we start correcting tokens.
     * @throws IOException Indicates an IO Error
     * @throws InputFormatException Indicates an error with the input file format
     */
    public void runOnFile(final String... args) throws IOException, InputFormatException {

        File file = new File(args[0]);
        File correctedFile = new File(args[1]);
        setTokenLimit(args);
        List<Record> records = formatConverter.convert(file);
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
            TokenSet tokenSet = new TokenSet(record.getOriginalData().getDescription());
            addToCorrectionMap(tokenSet);
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

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(correctedFile.getAbsoluteFile()), "UTF-8"));
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

    /**
     * Overrides the default tokenLimit and sets it to the supplied value.
     * @param tokenLimit new tokenLimit
     */
    public static void setTokenLimit(final int tokenLimit) {

        AbstractDataCleaner.tokenLimit = tokenLimit;
        System.out.println("TOKENLIMIT set to " + tokenLimit);

    }

    /**
     * Gets the word Multiset contains the word counts.
     * @return  Multiset<String> word multiset
     */
    public static Multiset<String> getWordMultiset() {

        return wordMultiset;
    }
}
