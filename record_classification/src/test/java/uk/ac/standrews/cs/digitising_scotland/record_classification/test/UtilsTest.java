package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.fileutils.DSStoreRemover;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
        assertEquals("word1", testStringArr[0]);
        assertEquals("word2", testStringArr[1]);
    }

    /**
     * Test write and number of lines.
     */
    @Test
    public void testWriteAndNumberOfLines() throws IOException {

        // File testNumberOfLines = new File(getClass().getResource("testNumberOfLines.txt").getFile());
        File testNumberOfLines = new File("testNumberOfLines.txt");
        Utils.writeToFile("line1 \n" + "line2 \n" + "line3 +\n", "testNumberOfLines.txt");
        assertEquals(3, Utils.getNumberOfLines(testNumberOfLines));
        Utils.writeToFile(("line1 \n" + "line2 \n" + "line3 \n"), "testNumberOfLines.txt", true);

        assertEquals(6, Utils.getNumberOfLines(testNumberOfLines));
        if (!testNumberOfLines.delete()) {
            System.err.println("Could not delete " + testNumberOfLines.getAbsolutePath());
        }
    }

    /**
     * Test write and number of lines arr.
     */
    @Test
    public void testWriteAndNumberOfLinesArr() throws IOException {

        String name = "testNumberOfLines.txt";
        File testNumberOfLines = new File(name);
        int[][] data = {{0, 0, 0}, {1, 1, 1}};
        Utils.writeToFile(data, name);

        assertEquals(2, Utils.getNumberOfLines(testNumberOfLines));
    }

    /**
     * Test counting number of files recursively.
     */
    @Test
    public void testCountFiles() {

        String testFolder = getClass().getResource("/TestDistributionFolder").getFile();
        int count = 0;
        DSStoreRemover dsr = new DSStoreRemover();
        dsr.remove(new File(testFolder));
        assertEquals(108, Utils.countFiles(testFolder, count));
    }
}
