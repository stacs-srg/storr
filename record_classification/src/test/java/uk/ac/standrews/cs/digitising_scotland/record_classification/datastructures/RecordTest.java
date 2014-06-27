package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.CODOrignalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;

/**
 * The Class RecordTest tests the creation of Records and their subclasses, parameters etc.
 * 
 */
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

        originalData = new OriginalData("original data test data", 2014, 1, "testFileName");
        record = new Record(originalData);
    }

    /**
     * Test constructor.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testConstructor() throws InputFormatException {

        Assert.assertEquals("original data test data", record.getOriginalData().getDescription());
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
        CodeFactory.getInstance().loadDictionary(codeFile);
        Set<CodeTriple> codeTripleSet = new HashSet<>();

        Code codeTest = CodeFactory.getInstance().getCode("2100");

        CodeTriple codeTriple = new CodeTriple(codeTest, new TokenSet("test String"), 1.0);
        codeTripleSet.add(codeTriple);
        record.addAllCodeTriples(codeTripleSet);

        Set<CodeTriple> classificationsFromRecord = record.getCodeTriples();

        CodeTriple clssfication = classificationsFromRecord.iterator().next();
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

        Record c = new Record(originalData);
        Assert.assertFalse(c.isCoDRecord());
        OriginalData codOriginalData = new CODOrignalData("original data test data", 2014, 1, 0, 0, "testFileName");
        c = new Record(codOriginalData);

        Assert.assertTrue(c.isCoDRecord());

    }

    /**
     * Test equals symmetric.
     */
    @Test
    public void testEqualsSymmetric() {

        Record x = new Record(originalData);
        Record y = new Record(originalData);
        assertTheSame(x, y);
    }

    /**
     * Test equals() different with classification sets where one is null.
     */
    @Test
    public void testEqualsSymmetricDifferentClassificationSetsNull() {

        Record x = new Record(originalData);
        Record y = new Record(originalData);
        CodeTriple codeTriple = null;
        x.addCodeTriples(codeTriple);
        assertTheSame(x, y);
    }

    /**
     * Test equals symmetric different vectors.
     */
    @Test
    public void testEqualsSymmetricDifferentVectors() {

        Record x = new Record(originalData);
        Record y = new Record(originalData);
        x.setCleanedDescription("dis");
        y.setCleanedDescription("dis");
        assertTheSame(x, y);
    }

    /**
     * Test equals symmetric3.
     */
    @Test
    public void testEqualsSymmetric3() {

        Record x = new Record(originalData);
        Record y = new Record(originalData);

        assertTheSame(x, y);
        //make them different
        x.setCleanedDescription("different");
        assertDifferent(x, y);
        //make the same again
        y.setCleanedDescription("different");
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
