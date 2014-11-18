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
package uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.hmm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import cc.mallet.types.Alphabet;

public class HiddenMarkovModelTest {

    private static final String outputFilePath = "target/hmmOutput.txt";

    @After
    public void tearDown() {

        File outputFile = new File(outputFilePath);
        FileUtils.deleteQuietly(outputFile);
        FileUtils.deleteQuietly(new File("target/alphabet"));

    }

    @Test
    public void test() throws IOException {

        String trainingFilename = getClass().getResource("/hmmTrainingTest.txt").getFile();
        String testingFilename = getClass().getResource("/occupationTestFormatPipe.txt").getFile();
        HiddenMarkovModel hmm = new HiddenMarkovModel(trainingFilename, testingFilename);
        Alphabet alphabet = hmm.generateAlphabet(trainingFilename);
        Object[] arr = convertAplhabetToSortedArray(alphabet);

        String[] expected = {"", "; SP", "hemiplegia C1", "phthisis C2", "lung C1", "of C1", "congestion C2", "inanition O", "pseudo-hypertrophic O", "brain C1", "tubercle C1", "diarrhoea C1", "incipient SP", "both SP", "of O", "paralysis C1", "haemoptysis C2"};
        Arrays.sort(expected);

        Assert.assertArrayEquals(expected, arr);
    }

    private Object[] convertAplhabetToSortedArray(final Alphabet alphabet) {

        Object[] arr = alphabet.toArray();
        Arrays.asList(arr).toArray(new String[arr.length]);
        Arrays.sort(arr);
        return arr;
    }

    @Test
    public void testTrain() throws IOException {

        String trainingFilename = getClass().getResource("/hmmTrainingTest.txt").getFile();
        String testingFilename = getClass().getResource("/hmmOutputTest.txt").getFile();
        HiddenMarkovModel hmm = new HiddenMarkovModel(trainingFilename, testingFilename);
        hmm.trainHmm(outputFilePath);
    }
}
