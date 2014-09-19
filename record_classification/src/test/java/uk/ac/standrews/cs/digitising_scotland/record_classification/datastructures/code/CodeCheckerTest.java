package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CodeCheckerTest {

    File correctCodes;

    @Before
    public void setUp() throws Exception {

        correctCodes = new File(getClass().getResource("/CodeCheckerTest.txt").getPath());
    }

    @Test
    public void initTest() throws IOException {

        CodeDictionary checker = new CodeDictionary(correctCodes);
        Assert.assertEquals(109, checker.getTotalNumberOfCodes());
    }

    @Test
    public void isValidTrueTest() throws IOException {

        CodeDictionary checker = new CodeDictionary(correctCodes);
        Assert.assertTrue(checker.isValidCode("A15"));
    }

    @Test
    public void isValidFalseTest() throws IOException {

        CodeDictionary checker = new CodeDictionary(correctCodes);
        Assert.assertFalse(checker.isValidCode("A1111"));
    }

}
