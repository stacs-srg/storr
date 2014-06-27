package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.OccCode;

/**
 * The Class OccCodeTest contains unit tests for testing the creation and use of {@link OccCode}s.
 */
public class OccCodeTest {

    private Code code1;
    private Code code2;
    private Code code3;
    private Code code4;

    /** The expected exception. */
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Setup, run before each test. Sets the {@link CodeFactory} code map and sets code1 and code2 to use code 2100.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
        code1 = CodeFactory.getInstance().getCode("2100");
        code2 = CodeFactory.getInstance().getCode("2100");

    }

    /**
     * Tests that two codes are equal when they should be.
     *
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void checkEqualMethodtest() throws CodeNotValidException {

        Assert.assertEquals(code1, code2);
    }

    /**
     * Checks code 1 and code 2 are the same object as the CodeFactory should only keep one copy of each identical code.
     *
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void checkSameObject() throws CodeNotValidException {

        Assert.assertSame(code1, code2);
    }

    /**
     * Check error handling on non existent code.
     *
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void checkErrorHandlingOnNonExistentCode() throws CodeNotValidException {

        expectedEx.expect(CodeNotValidException.class);
        expectedEx.expectMessage("999999" + " is not a valid code, or is not in the code dictionary");
        code3 = CodeFactory.getInstance().getCodeFromMap("999999");
    }

    /**
     * Tests getting coding level in the hierarchy. Longer codes are deeper.
     */
    @Test
    public void testGetCodingLevel() {

        code4 = CodeFactory.getInstance().getCode("9500");
        Code code5 = CodeFactory.getInstance().getCode("95120");
        Assert.assertEquals(4, code4.getCodingLevel());
        Assert.assertEquals(5, code5.getCodingLevel());

    }

    /**
     * Tests that codes can tell if they are a descendant of another code correctly.
     */
    @Test
    public void testIsDescendent() {

        code4 = CodeFactory.getInstance().getCode("9500");
        code3 = CodeFactory.getInstance().getCode("952");
        Code code6 = CodeFactory.getInstance().getCode("95240");
        Assert.assertFalse(code1.isDescendant(code1));
        Assert.assertTrue(code6.isDescendant(code3));
        Assert.assertFalse(code3.isDescendant(code6));
        Assert.assertFalse(code6.isDescendant(code4));
        Assert.assertFalse(code4.isDescendant(code6));

    }

}
