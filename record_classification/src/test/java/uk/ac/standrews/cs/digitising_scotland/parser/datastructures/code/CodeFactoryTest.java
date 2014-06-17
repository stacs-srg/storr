package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeNotValidException;

public class CodeFactoryTest {

    //TODO more comprehensive tests
    @Before
    public void setUp() throws Exception {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
    }

    @Test
    public void testIDGeneration() throws IOException, CodeNotValidException {

        Assert.assertEquals(0, CodeFactory.getInstance().getCode("2100").getID());
        Assert.assertEquals(1, CodeFactory.getInstance().getCode("2200").getID());
        Assert.assertEquals(2, CodeFactory.getInstance().getCode("3000").getID());
        Assert.assertEquals(3, CodeFactory.getInstance().getCode("4215").getID());
        Assert.assertEquals(4, CodeFactory.getInstance().getCode("6700").getID());
    }

    @Test
    public void testMapInitAndCodeRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100", CodeFactory.getInstance().getCode("2100").getCodeAsString());
    }

    @Test
    public void testMapInitAndDescriptionRecall() throws IOException, CodeNotValidException {

        Assert.assertEquals("2100 Architects and Town Planners", CodeFactory.getInstance().getCode("2100").getDescription());
    }

}
