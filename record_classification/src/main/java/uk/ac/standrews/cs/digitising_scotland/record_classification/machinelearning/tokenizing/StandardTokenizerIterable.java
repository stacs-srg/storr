package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing;

import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Created by fraserdunlop on 24/04/2014 at 14:08.
 * TODO @Fraser documentation
 */
public class StandardTokenizerIterable implements Iterable<CharTermAttribute> {

    /** The tokenizer. */
    private StandardTokenizer tokenizer;

    /**
     * Instantiates a new standard tokenizer iterable.
     *
     * @param matchVersion the match version
     * @param input the input
     */
    public StandardTokenizerIterable(final Version matchVersion, final Reader input) {

        this.tokenizer = new StandardTokenizer(matchVersion, input);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<CharTermAttribute> iterator() {

        return new TokenStreamIterator<CharTermAttribute>(tokenizer, CharTermAttribute.class);
    }
}
