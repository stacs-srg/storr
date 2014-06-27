package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.fileutils.DSStoreRemover;

/**
 * The Class UtilsTest.
 *
 * @author jkc25
 */
public class UtilsTest {

    /**
     * Test get comma.
     */
    @Test
    public void testGetComma() {

        String regex = Utils.getCSVComma();
        String testString = "word1, word2";
        String[] testStringArr = testString.split(regex);
        Assert.assertEquals("word1", testStringArr[0]);
        Assert.assertEquals("word2", testStringArr[1]);

    }

    /**
     * Test write and number of lines.
     */
    @Test
    public void testWriteAndNumberOfLines() {

        File testNumberOfLines = new File(getClass().getResource("/testNumberOfLines.txt").getFile());
        Utils.writeToFile("line1 \n" + "line2 \n" + "line3 +\n", "target/test-classes/testNumberOfLines.txt");
        try {
            Assert.assertEquals(3, Utils.getNumberOfLines(testNumberOfLines));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test write and number of lines append.
     */
    @Test
    public void testWriteAndNumberOfLinesAppend() {

        String filePath = getClass().getResource("/testNumberOfLines.txt").getFile();
        File testNumberOfLines = new File(filePath);
        Utils.writeToFile(("line1 \n" + "line2 \n" + "line3 \n"), "target/test-classes/testNumberOfLines.txt", true);
        try {
            Assert.assertEquals(6, Utils.getNumberOfLines(testNumberOfLines));
            if (!testNumberOfLines.delete()) {
                System.err.println("Could not delete " + testNumberOfLines.getAbsolutePath());
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test write and number of lines arr.
     */
    @Test
    public void testWriteAndNumberOfLinesArr() {

        String name = "target/test-classes/testNumberOfLines.txt";
        File testNumberOfLines = new File(name);
        int[][] data = {{0, 0, 0}, {1, 1, 1}};
        Utils.writeToFile(data, name);
        try {
            Assert.assertEquals(2, Utils.getNumberOfLines(testNumberOfLines));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test counting number of files recursviely.
     */
    @Test
    public void testCountFiles() {

        String testFolder = getClass().getResource("/TestDistributionFolder").getFile();
        int count = 0;
        DSStoreRemover dsr = new DSStoreRemover();
        dsr.remove(new File(testFolder));
        Assert.assertEquals(108, Utils.countFiles(testFolder, count));

    }
}
