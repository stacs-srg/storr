package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIterator;

/**
 * Utility class that provides concatenate with delimiter utility.
 * @author frjd2
 */
public final class Concatenator {

    private Concatenator() {

    }

    /**
     * Concatenates the tokens returned by the tokenizer together using the specified delimiter.
     * @param tokenizer - a {@link TokenStream} the attributes of which
     *  will be concatenated with a delimiter between tokens and no
     *  Delimiter before the start or after the end of the token stream
     * @param attributeType -  the attribute type to be concatenated (e.g. a String)
     * @param delimiter -  the character that separates the tokens
     * @return String - the tokens concatenated into a string
     * 
     */
    public static String concatenate(final TokenStream tokenizer, final Class<CharTermAttribute> attributeType, final CharSequence delimiter) {

        final StringBuilder sb = new StringBuilder();
        final TokenStreamIterator<CharTermAttribute> iterator = new TokenStreamIterator<CharTermAttribute>(tokenizer, attributeType);
        while (iterator.hasNext()) {
            final CharTermAttribute charTermAttribute = (CharTermAttribute) iterator.next();

            if (!iterator.isFirstTime()) {
                sb.append(delimiter);
            }
            sb.append(charTermAttribute);
        }
        closeIterator(iterator);
        return sb.toString();
    }

    /**
     * Closes an Iterator.
     * @param iterator
     */
    private static void closeIterator(final TokenStreamIterator<CharTermAttribute> iterator) {

        try {
            iterator.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
