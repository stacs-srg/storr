/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

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

}
