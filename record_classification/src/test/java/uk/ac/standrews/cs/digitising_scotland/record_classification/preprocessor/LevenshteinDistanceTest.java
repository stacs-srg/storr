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
package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenshteinDistance;

/**
 * The Class LevenshteinDistanceTest tests the calculation of Levenshtein distances with various strings.
 */
public class LevenshteinDistanceTest {

    /**
     * Tests calculation of distance betweem two strings with distance of 4. Shorest string first.
     */
    @Test
    public void testDistanceOf4SmallFirst() {

        final String shortString = "This is a test String";
        final String longString = "This is not a test String";
        final int distance = LevenshteinDistance.computeEditDistance(shortString, longString);
        Assert.assertEquals(4, distance);
    }

    /**
     * Tests calculation of distance between two strings with distance of 4, longest string first.
     */
    @Test
    public void testDistanceOf4BigFirst() {

        final String shortString = "This is a test String";
        final String longString = "This is not a test String";
        final int distance = LevenshteinDistance.computeEditDistance(longString, shortString);
        Assert.assertEquals(4, distance);
    }

    /**
     * Tests calculation of distance between two strings with equall length string.
     */
    @Test
    public void testDistanceEqual() {

        final String testString1 = "This is a test String";
        final String testString2 = "This is a test String";
        final int distance = LevenshteinDistance.computeEditDistance(testString1, testString2);
        Assert.assertEquals(0, distance);
    }

    /**
     * Tests when similarity is equal.
     */
    @Test
    public void testSimilarityEqual() {

        final String testString1 = "This is a test String";
        final String testString2 = "This is a test String";
        final double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(1.00, distance, 0.0001);
    }

    /**
     * Tests when similarity is not equal.
     */
    @Test
    public void testSimilarityNotEqual() {

        final String testString1 = "1234";
        final String testString2 = "123";
        final double distance = LevenshteinDistance.similarity(testString1, testString2);
        Assert.assertEquals(0.75, distance, 0.0001);
    }

}
