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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Tests the NGramSubstring class that grams are being produced correctly.
 * @author jkc25
 *
 */
public class NGramSubstringsTest {

    /** The n gram substrings. */
    private NGramSubstrings nGramSubstrings;

    /** The n gram substrings2. */
    private NGramSubstrings nGramSubstrings2;

    /** The n gram substrings record. */
    private NGramSubstrings nGramSubstringsRecord;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        nGramSubstrings = new NGramSubstrings("A test String");
        nGramSubstrings2 = new NGramSubstrings("A test String");
        int id = (int) Math.rint(Math.random() * 1000);
        ArrayList<String> descList = new ArrayList<>();
        final String desc = "A test Description";
        descList.add(desc);
        OriginalData originalData = new OriginalData(descList, 2014, 1, "testFileName");
        Record record = new Record(id, originalData);
        Bucket bucketToClean = new Bucket();
        bucketToClean.addRecordToBucket(record);
        nGramSubstringsRecord = new NGramSubstrings(record.getDescription().get(0));
    }

    /**
     * Test get grams.
     */
    @Test
    public void testGetGrams() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        Assert.assertEquals(6, grams.size());
    }

    /**
     * Test get grams for record.
     */
    @Test
    public void testGetGramsForRecord() {

        List<TokenSet> grams = nGramSubstringsRecord.getGrams();
        Assert.assertEquals(6, grams.size());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        List<TokenSet> grams2 = nGramSubstrings2.getGrams();
        Assert.assertEquals(nGramSubstrings, nGramSubstrings2);

        Assert.assertEquals(grams, grams2);
    }
}
