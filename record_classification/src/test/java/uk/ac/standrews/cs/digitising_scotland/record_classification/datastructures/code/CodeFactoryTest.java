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

    //TODO more comprehensive tests
    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
    }

    /**
     * Test id generation.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testIDGeneration() throws IOException, CodeNotValidException {

        Assert.assertEquals(0, CodeFactory.getInstance().getCode("2100").getID());
        Assert.assertEquals(1, CodeFactory.getInstance().getCode("2200").getID());
        Assert.assertEquals(2, CodeFactory.getInstance().getCode("3000").getID());
        Assert.assertEquals(3, CodeFactory.getInstance().getCode("4215").getID());
        Assert.assertEquals(4, CodeFactory.getInstance().getCode("6700").getID());
    }

    /**
     * Test map init and code recall.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testMapInitAndCodeRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100", CodeFactory.getInstance().getCode("2100").getCodeAsString());
    }

    /**
     * Test map init and description recall.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CodeNotValidException the code not valid exception
     */
    @Test
    public void testMapInitAndDescriptionRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100 Architects and Town Planners", CodeFactory.getInstance().getCode("2100").getDescription());
    }

    @Test
    public void serliazationTest() throws IOException, ClassNotFoundException {

        File path = new File("target/codeFactoryTest");
        CodeFactory cf1 = CodeFactory.getInstance();
        cf1.writeCodeFactory(path);

        CodeFactory cf2 = CodeFactory.getInstance().readCodeFactory(path);
        Assert.assertTrue(cf1.equals(cf2));
        Assert.assertTrue(CodeFactory.getInstance().equals(cf2));

    }

}
