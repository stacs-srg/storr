package uk.ac.standrews.cs.usp.parser.classifiers.lookup;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * 
 * Tokenizes and cleans a string.
 * never gets instantiated - utility class
 * @author frjd2
 *TODO pluggable tokenizer?
 *
 */
public final class TokenizerCleaner {

    private TokenizerCleaner() {

    }

    /**
     * Tokenise the specified String, clean it and concatenate tokens to form a single string.
     * @param description string to be cleaned
     * @return cleaned string
     */
    public static String clean(final String description) {

        String concatenatedTokens = "";
        try {
            concatenatedTokens = tokeniseAndConcatenateTokens(description);
        }
        catch (IOException e) {
            System.out.println("Error: Unable to clean description.");
            e.printStackTrace();
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
