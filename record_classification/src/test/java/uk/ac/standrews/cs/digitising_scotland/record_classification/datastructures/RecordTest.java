package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

/**
 * The Class RecordTest tests the creation of Records and their subclasses, parameters etc.
 * 
 */
@Ignore("Needs to be updated to new CodeIndex/DictionaryFormat")
//FIXME
public class RecordTest {

    /** The record. */
    private Record record;

    /** The original data. */
    private OriginalData originalData;

    /**
     * Sets up the originalData being used in theses tests. Run before each Test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        ArrayList<String> descList = new ArrayList();
        final String desc = "A test Description";
        descList.add(desc);

        int id = (int) Math.rint(Math.random() * 1000);

        originalData = new OriginalData(descList, 2014, 1, "testFileName");
        record = new Record(id, originalData);
    }

    /**
     * Test constructor.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testConstructor() throws InputFormatException {

        Assert.assertEquals("A test Description", record.getOriginalData().getDescription().get(0));
    }

    /**
     * Test add classification set.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testAddCodeTriples() throws IOException, CodeNotValidException {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeDictionary cd = new CodeDictionary(codeFile);
        Set<Classification> codeTripleSet = new HashSet<>();

        Code codeTest = cd.getCode("2100");

        Classification codeTriple = new Classification(codeTest, new TokenSet("test String"), 1.0);
        codeTripleSet.add(codeTriple);
        record.addAllCodeTriples(codeTripleSet);

        Set<Classification> classificationsFromRecord = record.getCodeTriples();

        Classification clssfication = classificationsFromRecord.iterator().next();
        Assert.assertEquals("2100", clssfication.getCode().getCodeAsString());
        Assert.assertEquals("test string", clssfication.getTokenSet().toString());

    }

    /**
     * Test is cod method.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testIsCodMethod() throws InputFormatException {

        ArrayList<String> descList = new ArrayList();
        final String desc = "A test Description";
        descList.add(desc);
        int id = (int) Math.rint(Math.random() * 1000);

        Record c = new Record(id, originalData);
        Assert.assertFalse(c.isCoDRecord());
        OriginalData codOriginalData = new CODOrignalData(descList, 2014, 1, 0, 0, "testFileName");
        c = new Record(id, codOriginalData);

        Assert.assertTrue(c.isCoDRecord());

    }

    /**
     * Test equals symmetric.
     */
    @Test
    public void testEqualsSymmetric() {

        int id = (int) Math.rint(Math.random() * 1000);
        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        assertTheSame(x, y);
    }

    /**
     * Test equals() different with classification sets where one is null.
     */
    @Test
    public void testEqualsSymmetricDifferentClassificationSetsNull() {

        int id = (int) Math.rint(Math.random() * 1000);
        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        Classification codeTriple = null;
        x.addCodeTriples(codeTriple);
        assertTheSame(x, y);
    }

    /**
     * Test equals symmetric different vectors.
     */
    @Test
    public void testEqualsSymmetricDifferentVectors() {

        int id = (int) Math.rint(Math.random() * 1000);

        Record x = new Record(id, originalData);
        Record y = new Record(id, originalData);
        assertTheSame(x, y);
    }

    /**
     * Assert different.
     *
     * @param x the x
     * @param y the y
     */
    private void assertDifferent(final Record x, final Record y) {

        Assert.assertTrue(!x.equals(y) && !y.equals(x));
        Assert.assertTrue(x.hashCode() != y.hashCode());
    }

    /**
     * Assert the same.
     *
     * @param x the x
     * @param y the y
     */
    private void assertTheSame(final Record x, final Record y) {

        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertTrue(x.hashCode() == y.hashCode());
    }
}
