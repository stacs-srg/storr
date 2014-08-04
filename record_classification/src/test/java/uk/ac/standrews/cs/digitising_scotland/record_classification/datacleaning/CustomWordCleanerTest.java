package uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

public class CustomWordCleanerTest {

    @Test
    public void test() throws IOException, InputFormatException {

        CodeFactory.getInstance().loadDictionary(new File(getClass().getResource("/CodeFactoryCoDFile.txt").getFile()));

        CustomWordCleaner.getWordMultiset();

        String input = getClass().getResource("/customWordCleanerInput.txt").getFile();
        String actualOutput = "target/customCleaned.txt";
        String expectedOutput = getClass().getResource("/customWordCleanerExpectedOutput.txt").getFile();
        String[] args = {input, actualOutput};
        CustomWordCleaner.main(args);

    }
}
