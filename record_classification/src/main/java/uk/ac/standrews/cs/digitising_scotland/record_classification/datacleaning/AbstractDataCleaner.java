/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.AbstractFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataCleaner.class);

    private AbstractFormatConverter formatConverter = new LongFormatConverter();

    /**
     * The Constant TOKENLIMIT. The default is 4 but this can be changed by calling setTokenLimit.
     */
    private static int tokenLimit = 4;

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
     * @throws CodeNotValidException
     */
    public void runOnFile(final String... args) throws IOException, InputFormatException, CodeNotValidException {

        File file = new File(args[0]);
        File correctedFile = new File(args[1]);
        setTokenLimit(args);
        File codeDictionaryFile = new File(args[3]);
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
        List<Record> records = formatConverter.convert(file, codeDictionary);
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
            for (String description : record.getDescription()) {
                TokenSet tokenSet = new TokenSet(description);
                addToCorrectionMap(tokenSet);
            }

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
            Set<Classification> set = r.getGoldStandardClassificationSet();
            for (Classification codeTriple : set) {
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

        BufferedReader br = ReaderWriterFactory.createBufferedReader(file);
        Writer bw = ReaderWriterFactory.createBufferedWriter(correctedFile.getAbsoluteFile());
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
            LOGGER.info("TOKENLIMIT set to " + tokenLimit);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("No TOKENLIMIT argument. Default is " + tokenLimit);
            LOGGER.error(e.toString());
        }
    }

    /**
     * Overrides the default tokenLimit and sets it to the supplied value.
     * @param tokenLimit new tokenLimit
     */
    public static void setTokenLimit(final int tokenLimit) {

        AbstractDataCleaner.tokenLimit = tokenLimit;
        LOGGER.info("TOKENLIMIT set to " + tokenLimit);

    }

    /**
     * Gets the word Multiset contains the word counts.
     * @return  Multiset<String> word multiset
     */
    public static Multiset<String> getWordMultiset() {

        return wordMultiset;
    }
}
