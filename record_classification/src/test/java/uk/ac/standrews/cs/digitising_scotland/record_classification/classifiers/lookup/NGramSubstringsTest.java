package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenshteinCleaner;

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
        OriginalData originalData = new OriginalData("A test Description", 2014, 1, "testFileName");
        Record record = new Record(id, originalData);
        Bucket bucketToClean = new Bucket();
        bucketToClean.addRecordToBucket(record);
        nGramSubstringsRecord = new NGramSubstrings(record);
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
