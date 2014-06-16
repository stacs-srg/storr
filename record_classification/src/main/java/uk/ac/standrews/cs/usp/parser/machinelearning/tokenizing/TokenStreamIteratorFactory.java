package uk.ac.standrews.cs.usp.parser.machinelearning.tokenizing;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Created by fraserdunlop on 24/04/2014 at 12:30.
 */
public abstract class TokenStreamIteratorFactory {

    //TODO allow instantiation of TokenStreamIteratorFactory with TokenStream class as a constructor argument

    /**
     * Creates a {@link TokenStreamIterator} from a String.
     * Uses Lucene version 36.
     * @param string to create iterator from.
     * @return TokenStreamIterator<CharTermAttribute> created from string
     */
    public static TokenStreamIterator<CharTermAttribute> newTokenStreamIterator(final String string) {

        TokenStream tokenizer = new StandardTokenizer(Version.LUCENE_36, new StringReader(string));
        TokenStreamIterator<CharTermAttribute> iterator = new TokenStreamIterator<CharTermAttribute>(tokenizer, CharTermAttribute.class);
        return iterator;
    }
}
