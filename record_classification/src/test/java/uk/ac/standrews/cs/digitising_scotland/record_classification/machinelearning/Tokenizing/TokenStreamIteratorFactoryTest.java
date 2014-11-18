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
package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.Tokenizing;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIterator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.TokenStreamIteratorFactory;

/**
 * The Class TokenStreamIteratorFactoryTest unit tests the {@link TokenStreamIterator}.
 */
public class TokenStreamIteratorFactoryTest {

    /** The expected exception. */
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Tests the {@link TokenStreamIteratorFactory} by creating a {@link TokenStreamIterator} and asserting that output is
     * as expected.
     */
    @Test
    public void testFactory() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("brown", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("fox", it.next().toString());
        }

    }

    /**
     * Tests that getting an index from the {@link TokenStreamIterator} returns the correct token.
     */
    @Test
    public void testGetIndex() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        Assert.assertEquals(-1, it.getIndex());
        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
            Assert.assertEquals(0, it.getIndex());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
            Assert.assertEquals(1, it.getIndex());

        }

    }

    /**
     * Tests that getting the next token from the {@link TokenStreamIterator} returns the correct token.
     */
    @Test
    public void testHasNext() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("brown", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("fox", it.next().toString());
        }

        Assert.assertTrue(!it.hasNext());

    }

    /**
     * Tests removing a token and checks that iterator subsequently returns correct tokens.
     */
    @Test
    public void testRemove() {

        expectedEx.expect(UnsupportedOperationException.class);

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
            it.remove();
        }

    }
}
