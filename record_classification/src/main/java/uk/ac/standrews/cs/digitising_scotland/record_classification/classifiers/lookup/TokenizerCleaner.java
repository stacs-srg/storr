package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Tokenizes and cleans a string. never gets instantiated - utility class
 * 
 * @author frjd2 TODO pluggable tokenizer?
 * 
 */
public final class TokenizerCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerCleaner.class);

    private TokenizerCleaner() {

    }

    /**
     * Tokenise the specified String, clean it and concatenate tokens to form a
     * single string.
     * 
     * @param description
     *            string to be cleaned
     * @return cleaned string
     */
    public static String clean(final String description) {

        String concatenatedTokens = null;
        try {
            concatenatedTokens = tokeniseAndConcatenateTokens(description);
        }
        catch (IOException e) {
            LOGGER.error("Error: Unable to clean description.", e);
            //            System.out.println();
            //            e.printStackTrace();
        }
        return concatenatedTokens;
    }

    private static String tokeniseAndConcatenateTokens(final String description) throws IOException {

        TokenStream streamForCleaning = new StandardTokenizer(Version.LUCENE_36, new StringReader(description));
        String[] stopWords = {"chronic", "acute", "1", "2", "after", "before", "dead", "death"};
        CharArraySet stopSet = StopFilter.makeStopSet(Version.LUCENE_36, stopWords, true);
        StopFilter filter = new StopFilter(Version.LUCENE_36, streamForCleaning, stopSet);
        String concatenatedTokens = Concatenator.concatenate(filter, CharTermAttribute.class, " ");
        streamForCleaning.close();
        return concatenatedTokens;
    }

}
