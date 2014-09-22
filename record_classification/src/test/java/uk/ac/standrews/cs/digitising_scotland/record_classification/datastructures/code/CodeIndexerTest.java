package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datareaders.LongFormatConverter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class CodeIndexerTest {

    CodeIndexer index;
    Bucket bucket;
    CodeDictionary codeDictionary;

    @Before
    public void setUp() throws Exception {

        codeDictionary = new CodeDictionary(new File(getClass().getResource("/CodeCheckerTest.txt").getFile()));
        index = new CodeIndexer();
        List<Record> listOfRecords = createRecords();
        bucket = new Bucket(listOfRecords);
    }

    private List<Record> createRecords() throws IOException, InputFormatException, CodeNotValidException {

        LongFormatConverter lfc = new LongFormatConverter();
        File inputFile = new File(getClass().getResource("/multipleCauseRecordsTest.csv").getFile());
        List<Record> listOfRecords = lfc.convert(inputFile, codeDictionary);
        return listOfRecords;
    }

    @Test
    public void numberOfOutputClassesTest() throws CodeNotValidException {

        index.addGoldStandardCodes(bucket);
        Assert.assertEquals(7, index.getNumberOfOutputClasses());
    }

}
