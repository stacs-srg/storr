package uk.ac.standrews.cs.usp.parser.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.standrews.cs.usp.tools.analysis.ClassificationDistribution;

/**
 * The Class ClassificationDistributionTest.
 */
public class ClassificationDistributionTest {

    /**
     * Test set up.
     */
    @Test
    public void testSetUp() {

        ClassificationDistribution d = new ClassificationDistribution("alcohol", 10, 5);
        assertEquals(d.getName(), "alcohol");
    }

    /**
     * Test set up1.
     */
    @Test
    public void testSetUp1() {

        ClassificationDistribution d = new ClassificationDistribution("alcohol", 10, 5);
        assertEquals(d.getPercentage(), 5, 0.1);
    }

    /**
     * Test set up2.
     */
    @Test
    public void testSetUp2() {

        ClassificationDistribution d = new ClassificationDistribution("alcohol", 10, 5);
        assertEquals(d.getNumberOfFeatures(), 10);
    }

    /**
     * Test sort.
     */
    @Test
    public void testSort() {

        ClassificationDistribution a = new ClassificationDistribution("alcohol", 10, 16);
        ClassificationDistribution b = new ClassificationDistribution("old age", 20, 33);
        ClassificationDistribution c = new ClassificationDistribution("debility", 30, 50);

        assertEquals(a.compareTo(b), 1);
        assertEquals(b.compareTo(b), 0);
        assertEquals(c.compareTo(a), -1);

    }

    /**
     * Test sort2.
     */
    @Test
    public void testSort2() {

        ClassificationDistribution a = new ClassificationDistribution("alcohol", 10, 16);
        ClassificationDistribution b = new ClassificationDistribution("old age", 20, 33);
        ClassificationDistribution c = new ClassificationDistribution("debility", 30, 50);

        ClassificationDistribution[] cd = {a, b, c};
        assertEquals(a.compareTo(b), 1);
        assertEquals(b.compareTo(b), 0);
        assertEquals(c.compareTo(a), -1);

        Arrays.sort(cd);
        for (int i = 0; i < cd.length; i++) {
            System.out.println(cd[i].getName());
        }
        assertEquals(cd[0].getName(), c.getName());
        assertEquals(cd[1].getName(), b.getName());
        assertEquals(cd[2].getName(), a.getName());

    }

}
