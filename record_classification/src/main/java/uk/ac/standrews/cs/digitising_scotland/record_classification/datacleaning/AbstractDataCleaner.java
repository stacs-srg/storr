package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

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
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all data cleaners. Subclasses must implement a correct method which maps incorrect
 * spellings to correct spellings.
 * Created by fraserdunlop on 16/07/2014 at 15:21.
 */
public abstract class AbstractDataCleaner {
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
     *
     * @param args 1 is the input file path, 2 is the output file path, 3 (optional) sets TOKENLIMIT which
     *             states the frequency of occurrence below which we start correcting tokens.
     * @throws IOException
     * @throws InputFormatException
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
            if (wordMultiset.count(token) < TOKENLIMIT) {
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

    private void correctTokensInFile(File file, File correctedFile) throws IOException {
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

    private String correctLine(String line) {
        String[] commaSplits = line.split(Utils.getCSVComma());
        StringBuilder sb = new StringBuilder();
        for (String str : commaSplits) {
            String cleanedString = tokenizeAndCleanString(str);
            sb.append(cleanedString).append(",");
        }
        String correctedLine = sb.toString();
        return correctedLine.subSequence(0, correctedLine.length() - 1).toString();
    }

    private String tokenizeAndCleanString(String str) {
        StringBuilder sb = new StringBuilder();
        for (String token : new TokenSet(str)) {
            String correctedToken = correctionMap.get(token);
            if (correctedToken != null) {
                sb.append(correctedToken).append(" ");
            } else {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void setTokenLimit(String... args) {
        try{
            TOKENLIMIT = Integer.parseInt(args[2]);
            System.out.println("TOKENLIMIT set to " + TOKENLIMIT);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No TOKENLIMIT argument. Default is " + TOKENLIMIT);
        }
    }
}
