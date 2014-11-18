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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.SpellingMistakeFactory;

/**
 * Runs tests on the SpellingMistakeFactory class.
 * @author jkc25
 *
 */
public class SpellingMistakeFactoryTest {

    /**
     * Tests to make sure that the input and output strings are different,
     * ie, that they contain a newly introduced mistake.
     */
    @Test
    public void testDifferent() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        for (int i = 0; i < 10; i++) {
            assertTrue(!spf.addMistakeSwap("spelling mistakes are easy to make").equalsIgnoreCase("spelling mistakes are easy to make"));
        }
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testTwoLetter() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        assertTrue(spf.addMistakeSwap("to").equalsIgnoreCase("ot"));
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testSpellingMistakeComplex() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        spf.addMistakeTypo("input");

        for (int i = 0; i < 10; i++) {
            assertTrue(!spf.addMistakeTypo("spelling mistakes are easy to make").equalsIgnoreCase("spelling mistakes are easy to make"));
        }
    }

    /**
     * Tests to check that the rules governing to letter words work.
     */
    @Test
    public void testSpellingMistakeComplexOneLetter() {

        SpellingMistakeFactory spf = new SpellingMistakeFactory();
        assertTrue(!spf.addMistakeTypo("a").equalsIgnoreCase("a"));
    }
}
