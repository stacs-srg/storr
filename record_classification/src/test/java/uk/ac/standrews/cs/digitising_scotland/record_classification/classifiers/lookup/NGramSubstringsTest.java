package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor.DataCleaning;

/**
 * Tests the NGramSubstring class that grams are being produced correctly.
 * @author jkc25
 *
 */
public class NGramSubstringsTest {

    private NGramSubstrings nGramSubstrings;
    private NGramSubstrings nGramSubstrings2;
    private NGramSubstrings nGramSubstringsRecord;

    @Before
    public void setUp() throws Exception {

        nGramSubstrings = new NGramSubstrings("A test String");
        nGramSubstrings2 = new NGramSubstrings("A test String");

        OriginalData originalData = new OriginalData("A test Description", 2014, 1, "testFileName");
        Record record = new Record(originalData);
        Bucket bucketToClean = new Bucket();
        bucketToClean.addRecordToBucket(record);
        DataCleaning.cleanData(bucketToClean);
        nGramSubstringsRecord = new NGramSubstrings(record);
    }

    @Test
    public void testGetGrams() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        Assert.assertEquals(6, grams.size());
    }

    @Test
    public void testGetGramsForRecord() {

        List<TokenSet> grams = nGramSubstringsRecord.getGrams();
        Assert.assertEquals(6, grams.size());
    }

    @Test
    public void testEquals() {

        List<TokenSet> grams = nGramSubstrings.getGrams();
        List<TokenSet> grams2 = nGramSubstrings2.getGrams();
        Assert.assertEquals(nGramSubstrings, nGramSubstrings2);

        Assert.assertEquals(grams, grams2);
    }
}
