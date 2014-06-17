package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeNotValidException;

public class OccCodeTest {

    private Code code1;
    private Code code2;
    private Code code3;
    private Code code4;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
        code1 = CodeFactory.getInstance().getCode("2100");
        code2 = CodeFactory.getInstance().getCode("2100");

    }

    @Test
    public void checkEqualMethodtest() throws CodeNotValidException {

        Assert.assertEquals(code1, code2);
    }

    @Test
    public void checkSameObject() throws CodeNotValidException {

        Assert.assertSame(code1, code2);
    }

    @Test
    public void checkErrorHandlingOnNonExistantCode() throws CodeNotValidException {

        expectedEx.expect(CodeNotValidException.class);
        expectedEx.expectMessage("999999" + " is not a valid code, or is not in the code dictionary");
        code3 = CodeFactory.getInstance().getCodeFromMap("999999");
    }

    @Test
    public void testGetCodingLevel() {

        code4 = CodeFactory.getInstance().getCode("9500");
        Code code5 = CodeFactory.getInstance().getCode("95120");
        Assert.assertEquals(4, code4.getCodingLevel());
        Assert.assertEquals(5, code5.getCodingLevel());

    }

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
