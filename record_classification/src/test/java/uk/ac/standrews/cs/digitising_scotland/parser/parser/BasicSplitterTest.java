package uk.ac.standrews.cs.digitising_scotland.parser.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.parser.BasicSplitter;

@Ignore
public class BasicSplitterTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSemiColon1() {

        BasicSplitter splitter = new BasicSplitter();
        String testLine = "Cause one; Cause two";
        String[] spltiLine = splitter.splitString(testLine);
        Assert.assertEquals("cause one", spltiLine[0]);
        Assert.assertEquals("cause two", spltiLine[1]);

    }

    @Test
    public void testNumbers() {

        BasicSplitter splitter = new BasicSplitter();
        String testLine = "1 Cause one 2 Cause two";
        String[] spltiLine = splitter.splitString(testLine);
        Assert.assertEquals("cause one", spltiLine[0]);
        Assert.assertEquals("cause two", spltiLine[1]);

    }

    @Test
    public void testBoth() {

        BasicSplitter splitter = new BasicSplitter();
        String testLine = "Both Cause one; Cause two";
        String[] spltiLine = splitter.splitString(testLine);
        Assert.assertEquals("cause one", spltiLine[0]);
        Assert.assertEquals("cause two", spltiLine[1]);

    }

    @Test
    public void testAll() {

        BasicSplitter splitter = new BasicSplitter();
        String testLine = "All Cause one; Cause two; cause 3";
        String[] spltiLine = splitter.splitString(testLine);
        Assert.assertEquals("cause one", spltiLine[0]);
        Assert.assertEquals("cause two", spltiLine[1]);
        Assert.assertEquals("cause 3", spltiLine[2]);

    }
}
