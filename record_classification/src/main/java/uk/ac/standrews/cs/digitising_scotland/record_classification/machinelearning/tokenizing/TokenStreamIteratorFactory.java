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
