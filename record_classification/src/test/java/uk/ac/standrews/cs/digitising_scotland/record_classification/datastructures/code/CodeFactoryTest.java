package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class CodeFactoryTest.
 */
public class CodeFactoryTest {

    CodeIndexer codeFactory;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeDictionary codeChecker = new CodeDictionary(codeFile);
        codeFactory = new CodeIndexer(codeChecker);
    }

    /**
     * Test id generation.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testIDGeneration() throws IOException, CodeNotValidException {

        Assert.assertEquals(0, codeFactory.getCode("2100").getID());
        Assert.assertEquals(1, codeFactory.getCode("2200").getID());
        Assert.assertEquals(2, codeFactory.getCode("3000").getID());
        Assert.assertEquals(3, codeFactory.getCode("4215").getID());
        Assert.assertEquals(4, codeFactory.getCode("6700").getID());
    }

    /**
     * Test map init and code recall.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testMapInitAndCodeRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100", CodeIndexer.getInstance().getCode("2100").getCodeAsString());
    }

    /**
     * Test map init and description recall.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testMapInitAndDescriptionRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100 Architects and Town Planners", CodeIndexer.getInstance().getCode("2100").getDescription());
    }

    @Test
    public void serliazationTest() throws IOException, ClassNotFoundException {

        File path = new File("target/codeFactoryTest");
        CodeIndexer cf1 = CodeIndexer.getInstance();
        cf1.writeCodeFactory(path);

        CodeIndexer cf2 = CodeIndexer.getInstance().readCodeFactory(path);
        Assert.assertTrue(cf1.equals(cf2));
        Assert.assertTrue(CodeIndexer.getInstance().equals(cf2));

    }

    @Test
    public void gettingExpandedCodeFactoryTest() {

    }
}
