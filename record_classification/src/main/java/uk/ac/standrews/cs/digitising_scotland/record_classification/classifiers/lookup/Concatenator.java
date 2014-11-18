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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIterator;

/**
 * Utility class that provides concatenate with delimiter utility.
 * @author frjd2
 */
public final class Concatenator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Concatenator.class);

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
            LOGGER.error("Iterator could not be closed", e.getCause());
        }
    }
}
