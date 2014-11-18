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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class CustomWordCleanerTest {

    @Ignore("Need to implement proper checking on this")
    //FIXME
    @Test
    public void test() throws IOException, InputFormatException, CodeNotValidException {

        String codeDictionary = getClass().getResource("/CodeFactoryCoDFile.txt").getFile();

        CustomWordCleaner.getWordMultiset();

        String input = getClass().getResource("/customWordCleanerInput.txt").getFile();
        String actualOutput = "target/customCleaned.txt";
        String expectedOutput = getClass().getResource("/customWordCleanerExpectedOutput.txt").getFile();
        String[] args = {input, actualOutput};
        CustomWordCleaner.main(args);

    }
}
