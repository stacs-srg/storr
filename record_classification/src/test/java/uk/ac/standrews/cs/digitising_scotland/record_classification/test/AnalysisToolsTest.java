/*
 * | ______________________________________________________________________________________________ | Understanding
 * Scotland's People (USP) project. | | The aim of the project is to produce a linked pedigree for all publicly | |
 * available Scottish birth/death/marriage records from 1855 to the present day. | | | | Digitization of the records is
 * being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National
 * Records of Scotland. | | | | The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. | |
 * The other project members are Lee Williamson (also at the Longitudinal Studies Centre) | | Graham Kirby, Alan Dearle
 * and Jamie Carson at the School of Computer Science at St Andrews; | | and Eilidh Garret and Alice Reid at the
 * Department of Geography at Cambridge. | | | |
 * ______________________________________________________________________________________________
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.analysis.AnalysisTools;

/**
 * The Class AnalysisToolsTest.
 */
public class AnalysisToolsTest {

    /** The Constant PRECISION. */
    static final double PRECISION = 0.001;

    /** The at0. */
    private AnalysisTools at0;

    /** The at. */
    private AnalysisTools at;

    /** The at1. */
    private AnalysisTools at1;

    /**
     * Setup.
     */
    @Before
    public void setup() {

        at0 = new AnalysisTools(new File(getClass().getResource("/AnalysisToolsTestFile3.csv").getFile()));
        at = new AnalysisTools((new File(getClass().getResource("/AnalysisToolsTestFile1.csv").getFile())));
        at1 = new AnalysisTools((new File(getClass().getResource("/AnalysisToolsTestFile2.csv").getFile())));

    }

    /**
     * Test get.
     */
    @Test
    public void testGet() {

        System.out.println(at0.countUniqueWords());
        at0.listWordsOrderedByFrequency();

    }

    /**
     * Testing on the simple test file. Unique words should be 4 as in the file.
     */
    @Test
    public void testSimple() {

        File simpleTest = new File(getClass().getResource("/AnalysisToolsTestFile1.csv").getFile());
        AnalysisTools at = new AnalysisTools(simpleTest);
        System.out.println(at.countUniqueWords());
        assertEquals(4, at.countUniqueWords());
    }

    /**
     * Test word count.
     */
    @Test
    public void testWordCount() {

        File simpleTest = new File(getClass().getResource("/AnalysisToolsTestFile1.csv").getFile());
        AnalysisTools at = new AnalysisTools(simpleTest);
        at.listWordsOrderedByFrequency();
        assertEquals(12, at.getWordTotal("Cows"));
        assertEquals(9, at.getWordTotal("Dogs"));
        assertEquals(6, at.getWordTotal("Cats"));
        assertEquals(3, at.getWordTotal("Mouse"));
    }

    /**
     * Test class count.
     */
    @Test
    public void testClassCount() {

        System.out.println(at.totalNumberOfClasses());
        assertEquals(4, at.totalNumberOfClasses());
    }

    /**
     * Test class count invididual.
     */
    @Test
    public void testClassCountInvididual() {

        System.out.println(at.totalNumberOfClasses());
        assertEquals(12, at.totalNumberInEachClass("Cows"));
        assertEquals(9, at.totalNumberInEachClass("Dogs"));
        assertEquals(6, at.totalNumberInEachClass("Cats"));
        assertEquals(3, at.totalNumberInEachClass("Mouse"));

    }

    /**
     * Test word count2.
     */
    @Test
    public void testWordCount2() {

        at1.listWordsOrderedByFrequency();
        assertEquals(12, at1.getWordTotal("Cows"));
        assertEquals(10, at1.getWordTotal("Dogs"));
        assertEquals(9, at1.getWordTotal("Cats"));
        assertEquals(3, at1.getWordTotal("Mouse"));
    }

    /**
     * Test class coun2t.
     */
    @Test
    public void testClassCoun2t() {

        System.out.println(at1.totalNumberOfClasses());
        assertEquals(4, at1.totalNumberOfClasses());
    }

    /**
     * Test class count invididual2.
     */
    @Test
    public void testClassCountInvididual2() {

        System.out.println(at1.totalNumberOfClasses());
        assertEquals(12, at1.totalNumberInEachClass("Cows"));
        assertEquals(9, at1.totalNumberInEachClass("Dogs"));
        assertEquals(6, at1.totalNumberInEachClass("Cats"));
        assertEquals(3, at1.totalNumberInEachClass("Mouse"));

    }

    /**
     * Test precision1.
     */
    @Test
    public void testPrecision1() {

        HashMap<String, Integer> map = at.buildPrecisionMap();
        assertEquals(12, (int) map.get("Cows"));
        assertEquals(9, (int) map.get("Dogs"));
        assertEquals(6, (int) map.get("Cats"));
        assertEquals(3, (int) map.get("Mouse"));

        System.out.println(map.get("Cows"));
        System.out.println(map.get("Dogs"));
        System.out.println(map.get("Cats"));
        System.out.println(map.get("Mouse"));

    }

    /**
     * Test precision2.
     */
    @Test
    public void testPrecision2() {

        HashMap<String, Integer> map = at1.buildPrecisionMap();

        assertEquals(12, (int) map.get("Cows"));
        assertEquals(6, (int) map.get("Dogs"));
        assertEquals(5, (int) map.get("Cats"));
        assertEquals(3, (int) map.get("Mouse"));

        System.out.println(map.get("Cows"));
        System.out.println(map.get("Dogs"));
        System.out.println(map.get("Cats"));
        System.out.println(map.get("Mouse"));
    }

    /**
     * Test total number in each prediction.
     */
    @Test
    public void testTotalNumberInEachPrediction() {

        assertEquals(12, at1.totalNumberInEachPrediction("Cows"));
        assertEquals(7, at1.totalNumberInEachPrediction("Dogs"));
        assertEquals(8, at1.totalNumberInEachPrediction("Cats"));
        assertEquals(3, at1.totalNumberInEachPrediction("Mouse"));

        System.out.println(at1.totalNumberInEachPrediction("Cows"));
        System.out.println(at1.totalNumberInEachPrediction("Dogs"));
        System.out.println(at1.totalNumberInEachPrediction("Cats"));
        System.out.println(at1.totalNumberInEachPrediction("Mouse"));
    }

    /**
     * Test precision ofsimple test2.
     */
    @Test
    public void testPrecisionOfsimpleTest2() {

        System.out.println("Precision of Cows: " + at1.getPrecision("Cows"));
        System.out.println("Precision of Dogs: " + at1.getPrecision("Dogs"));
        System.out.println("Precision of Cats: " + at1.getPrecision("Cats"));
        System.out.println("Precision of Mouse: " + at1.getPrecision("Mouse"));

        assertEquals(1.0, at1.getPrecision("Cows"), PRECISION);
        assertEquals(0.857, at1.getPrecision("Dogs"), PRECISION);
        assertEquals(0.625, at1.getPrecision("Cats"), PRECISION);
        assertEquals(1.0, at1.getPrecision("Mouse"), PRECISION);

    }

    /**
     * Test recall ofsimple test2.
     */
    @Test
    public void testRecallOfsimpleTest2() {

        System.out.println("recall of Cows:" + at1.getRecall("Cows"));
        System.out.println("recall of Dogs:" + at1.getRecall("Dogs"));
        System.out.println("recall of Cats:" + at1.getRecall("Cats"));
        System.out.println("recall of Mouse:" + at1.getRecall("Mouse"));

        assertEquals(1.0, at1.getRecall("Cows"), PRECISION);
        assertEquals(0.666, at1.getRecall("Dogs"), PRECISION);
        assertEquals(0.833, at1.getRecall("Cats"), PRECISION);
        assertEquals(1.0, at1.getRecall("Mouse"), PRECISION);

    }

    /**
     * Test accuracy metrics2 recall.
     */
    @Test
    public void testAccuracyMetrics2Recall() {

        System.out.println("recall of Cows:" + at1.getRecall("Cows"));
        System.out.println("recall of Dogs:" + at1.getRecall("Dogs"));
        System.out.println("recall of Cats:" + at1.getRecall("Cats"));
        System.out.println("recall of Mouse:" + at1.getRecall("Mouse"));

        System.out.println(at1.getClassificationMap().get("Cows").getRecall());
        System.out.println(at1.getClassificationMap().get("Dogs").getRecall());
        System.out.println(at1.getClassificationMap().get("Cats").getRecall());
        System.out.println(at1.getClassificationMap().get("Mouse").getRecall());

        assertEquals(1.0, at1.getRecall("Cows"), PRECISION);
        assertEquals(0.666, at1.getRecall("Dogs"), PRECISION);
        assertEquals(0.833, at1.getRecall("Cats"), PRECISION);
        assertEquals(1.0, at1.getRecall("Mouse"), PRECISION);

    }

    /**
     * Test accuracy metrics2 precision.
     */
    @Test
    public void testAccuracyMetrics2Precision() {

        System.out.println(at1.getClassificationMap().get("Cows").getPrecision());
        System.out.println(at1.getClassificationMap().get("Dogs").getPrecision());
        System.out.println(at1.getClassificationMap().get("Cats").getPrecision());
        System.out.println(at1.getClassificationMap().get("Mouse").getPrecision());

        assertEquals(1.0, at1.getPrecision("Cows"), PRECISION);
        assertEquals(0.8571, at1.getPrecision("Dogs"), PRECISION);
        assertEquals(0.625, at1.getPrecision("Cats"), PRECISION);
        assertEquals(1.0, at1.getPrecision("Mouse"), PRECISION);

        assertEquals(at1.getClassificationMap().get("Cows").getPrecision(), at1.getPrecision("Cows"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Dogs").getPrecision(), at1.getPrecision("Dogs"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Cats").getPrecision(), at1.getPrecision("Cats"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Mouse").getPrecision(), at1.getPrecision("Mouse"), PRECISION);

        System.out.println("Dogs TP: " + at1.getClassificationMap().get("Dogs").getTP());
        System.out.println("Dogs FP: " + at1.getClassificationMap().get("Dogs").getFP());
        System.out.println("Dogs TN: " + at1.getClassificationMap().get("Dogs").getTN());
        System.out.println("Dogs FN: " + at1.getClassificationMap().get("Dogs").getFN());

    }

    /**
     * Test accuracy metrics2 recall2.
     */
    @Test
    public void testAccuracyMetrics2Recall2() {

        System.out.println("recall of Cows:" + at1.getRecall("Cows"));
        System.out.println("recall of Dogs:" + at1.getRecall("Dogs"));
        System.out.println("recall of Cats:" + at1.getRecall("Cats"));
        System.out.println("recall of Mouse:" + at1.getRecall("Mouse"));

        System.out.println(at1.getClassificationMap().get("Cows").getRecall());
        System.out.println(at1.getClassificationMap().get("Dogs").getRecall());
        System.out.println(at1.getClassificationMap().get("Cats").getRecall());
        System.out.println(at1.getClassificationMap().get("Mouse").getRecall());

        assertEquals(at1.getClassificationMap().get("Cows").getRecall(), at1.getRecall("Cows"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Dogs").getRecall(), at1.getRecall("Dogs"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Cats").getRecall(), at1.getRecall("Cats"), PRECISION);
        assertEquals(at1.getClassificationMap().get("Mouse").getRecall(), at1.getRecall("Mouse"), PRECISION);

    }

    /**
     * Tests calculating micro and macro precision and recall.
     */
    @Test
    public void testMicroAndMacroPrecisionAndRecall() {

        File fileToBeAnalysed = new File(getClass().getResource("/AnalysisToolsTestFile.csv").getFile());
        AnalysisTools atreal = new AnalysisTools(fileToBeAnalysed);
        Object[] classes = atreal.getClassificationMap().keySet().toArray();

        for (int i = 0; i < classes.length; i++) {
            double recall = atreal.getClassificationMap().get(classes[i].toString()).getRecall();
            double precision = atreal.getClassificationMap().get(classes[i].toString()).getPrecision();
            double accuracy = atreal.getClassificationMap().get(classes[i].toString()).getAccuracy();
            double f1 = atreal.getClassificationMap().get(classes[i].toString()).getF1();

            System.out.println(classes[i].toString() + " Precision " + precision + "  Recall " + recall + " f ratio " + f1 + " accuracy " + accuracy);

        }
        double macroRecall = atreal.getMacroAverageRecall();
        double macroPrecision = atreal.getMacroAveragePrecision();
        double microRecall = atreal.getMicroAverageRecall();
        double microPrecision = atreal.getMicroAveragePrecision();
        System.out.println("Micro precision " + microPrecision + " Micro recall " + microRecall);
        System.out.println("Macro precision " + macroPrecision + " Macro recall " + macroRecall);

    }

}
