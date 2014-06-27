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

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.analysis.ClassCounter;

/**
 * JUnit tests for the ClassCounter class.
 * 
 * @author jkc25
 */
public class ClassCounterTest {

    /**
     * Checks on small number of input.
     */
    @Test
    public void test116() {

        File input = new File(getClass().getResource("/ClassCounterTestFile116.txt").getFile());
        System.out.println(input.exists());
        ClassCounter classcounter = new ClassCounter(input);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        assertEquals((116), classcounter.count());
    }

    /**
     * Checks on medium file.
     */
    @Test
    public void test89() {

        // mass
        File input = new File(getClass().getResource("/ClassCounterTestFile1.txt").getFile());
        ClassCounter classcounter = new ClassCounter(input);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());

        assertEquals(89, classcounter.count());
    }

    /**
     * Checks on medium file.
     */
    @Test
    public void testonInput11() {

        // mass
        File input = new File(getClass().getResource("/ClassCounterTestFile89.csv").getFile());
        ClassCounter classcounter = new ClassCounter(input);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        assertEquals(89, classcounter.count());
    }

    /**
     * Checks on large file.
     */
    @Test
    public void test1581() {

        // tasAllCasue
        File input = new File(getClass().getResource("/ClassCounterTestFile1581.txt").getFile());
        ClassCounter classcounter = new ClassCounter(input);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        assertEquals(1581, classcounter.count());
    }

    /**
     * Test to check that counting works when in a non 0 (default) column.
     */
    @Test
    public void testOtherCol() {

        File input = new File(getClass().getResource("/ClassCounterTestFile62.txt").getFile());
        ClassCounter classcounter = new ClassCounter(input, 1);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        assertEquals(62, classcounter.count());
    }

    /**
     * Test to check that counting works when in a non 0 (default) column.
     */
    @Test
    public void testOtherColCSV() {

        File input = new File(getClass().getResource("/ClassCounterTestFile319.csv").getFile());
        ClassCounter classcounter = new ClassCounter(input);
        System.out.println(classcounter.count() + " classes in " + input.getAbsolutePath());
        assertEquals(319, classcounter.count());
    }

}
