package uk.ac.standrews.cs.usp.parser.machinelearning.tokenizing;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 
 * @author frjd2 & Masih
 *
 * @param <Attribute>
 */
public class TokenStreamIterator<Attribute extends CharTermAttribute> implements Iterator<Attribute>, Closeable {

    private TokenStream tokenizer;
    private Class<Attribute> attributeType;
    private int index = -1;

    public TokenStreamIterator(final TokenStream tokenizer, final Class<Attribute> attributeType) {

        this.tokenizer = tokenizer;
        this.attributeType = attributeType;
    }

    @Override
    public boolean hasNext() {

        try {
            return tokenizer.incrementToken();
        }
        catch (IOException e) {
            return false; //Masih says bad practice - fix?
        }
    }

    @Override
    public Attribute next() {

        index++;
        return tokenizer.getAttribute(attributeType);
    }

    public int getIndex() {

        return index;
    }

    public boolean isFirstTime() {

        return index == 0;

    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException();

    }

    @Override
    public void close() throws IOException {

        tokenizer.close();
    }
}
