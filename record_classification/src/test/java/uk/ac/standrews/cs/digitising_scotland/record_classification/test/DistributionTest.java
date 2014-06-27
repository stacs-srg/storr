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

import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.analysis.ClassificationDistribution;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.Distribution;

/**
 * Tests the Distribution class.
 * 
 * @author jkc25
 */
public class DistributionTest {

    private File base;

    @Before
    public void setUp() {

        Class<? extends DistributionTest> class1 = getClass();
        String filePath = class1.getResource("/TestDistributionFolder").getFile();
        base = new File(filePath);

    }

    /**
     * Tests that the Distribution class accurately counts. the number of classes;
     */
    @Test
    public void testNoOfClasses() {

        Distribution d = new Distribution(base);
        assertEquals(d.getNoOfClasses(), 7);

    }

    /**
     * Tests that class names are returned correctly.
     */
    @Test
    public void testGetClassNames() {

        Distribution d = new Distribution(base);
        assertEquals(d.getNoOfClasses(), 7);
        assertEquals(d.getClassNames()[0], "alcohol");
        assertEquals(d.getClassNames()[6], "gastritis");
    }

    /**
     * Tests that the number of values for each class is returned correctly.
     */
    @Test
    public void testNoOfValues() {

        Distribution d = new Distribution(base);
        assertEquals(d.getNoOfClasses(), 7);
        assertEquals(d.getClassNames()[0], "alcohol");
        assertEquals(d.getClassNames()[6], "gastritis");
        assertEquals(d.getNoOfValues()[0], 8);
        assertEquals(d.getNoOfValues()[6], 2);
    }

    /**
     * Tests that the total number of values are returned correctly.
     */
    @Test
    public void testTotalValues() {

        Distribution d = new Distribution(base);
        assertEquals(d.getNoOfClasses(), 7);
        assertEquals(d.getClassNames()[0], "alcohol");
        assertEquals(d.getClassNames()[6], "gastritis");
        assertEquals(d.getNoOfValues()[0], 8);
        assertEquals(d.getNoOfValues()[6], 2);
        assertEquals(d.getNoOfTotalValues(), 101);
    }

    /**
     * Tests that the total number of values are returned correctly.
     */
    @Test
    public void testValuesInClass() {

        testTotalValues();
        Distribution d = new Distribution(base);
        assertEquals(d.getValueForName("alcohol"), 8);
        assertEquals(d.getValueForName("gastritis"), 2);
    }

    /**
     * Tests that the total number of values are returned correctly.
     */
    @Test
    public void testValuesInClassNone() {

        testTotalValues();
        Distribution d = new Distribution(base);
        assertEquals(d.getValueForName("NOTHERE"), -1);
        System.out.println(d.toString());
    }

    /**
     * Tests that the total number of values are returned correctly.
     */
    @Test
    public void testSort() {

        Distribution d = new Distribution(base);
        ClassificationDistribution[] cd = d.getOrderedClass();

        for (int i = 0; i < cd.length; i++) {
            System.out.println(cd[i].getName() + " " + cd[i].getNumberOfFeatures() + " " + cd[i].getPercentage());
        }

        assertEquals(cd[0].getName(), "brain disease");
        assertEquals(cd[0].getNumberOfFeatures(), 41);
        assertEquals(cd[6].getName(), "cholera");
        assertEquals(cd[6].getNumberOfFeatures(), 1);

    }

}
