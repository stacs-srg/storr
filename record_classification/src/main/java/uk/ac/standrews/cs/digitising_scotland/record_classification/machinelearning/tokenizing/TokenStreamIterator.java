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
package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * The Class TokenStreamIterator.
 *
 * @author frjd2 & Masih
 * @param <Attribute> the generic type
 */
public class TokenStreamIterator<Attribute extends CharTermAttribute> implements Iterator<Attribute>, Closeable {

    /** The tokenizer. */
    private TokenStream tokenizer;

    /** The attribute type. */
    private Class<Attribute> attributeType;

    /** The index. */
    private int index = -1;

    /**
     * Instantiates a new token stream iterator.
     *
     * @param tokenizer the tokenizer
     * @param attributeType the attribute type
     */
    public TokenStreamIterator(final TokenStream tokenizer, final Class<Attribute> attributeType) {

        this.tokenizer = tokenizer;
        this.attributeType = attributeType;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {

        try {
            return tokenizer.incrementToken();
        }
        catch (IOException e) {
            //Masih says bad practice - fix?
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Attribute next() {

        index++;
        return tokenizer.getAttribute(attributeType);
    }

    /**
     * Gets the index.
     *
     * @return the index
     */
    public int getIndex() {

        return index;
    }

    /**
     * Checks if is first time.
     *
     * @return true, if is first time
     */
    public boolean isFirstTime() {

        return index == 0;

    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {

        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {

        tokenizer.close();
    }
}
