package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.analysis.Reporter;

/**
 * Tests the the Reporter class is working as expected.
 * @author jkc25
 *
 */
public class ReporterTest {

    /**
     * Tests that the singleton nature of the class works as expected.
     */
    @Test
    public void test() {

        Reporter r = Reporter.getInstance();
        System.out.println(r.toString());
        Reporter d = Reporter.getInstance();
        System.out.println(d.toString());
        assertEquals(r, d);
    }

}
