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
