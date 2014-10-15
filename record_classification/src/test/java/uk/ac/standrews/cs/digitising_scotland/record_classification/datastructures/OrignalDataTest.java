package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;

/**
 * The Class OrignalDataTest tests the creation of OriginalData objects.
 */
public class OrignalDataTest {

    /** The custom exception that is being tested. */
    @Rule
    public ExpectedException customException = ExpectedException.none();

    /** The original data test. */
    private OriginalData originalDataTest;

    private ArrayList<String> descriptionList = new ArrayList<>();
    private ArrayList<String> descriptionList2 = new ArrayList<>();

    @Before
    public void setup() {

        String desc = "A test Description";
        descriptionList.add(desc);

        desc = "description";
        descriptionList2.add(desc);
    }

    /**
     * Tests creating originalData with a negative age group.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testNegativeAgeGroup() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("age group must be between 0 and 5");
        originalDataTest = new CODOrignalData(descriptionList, 1999, -6, 1, 0, "testFileName");

    }

    /**
     * Tests creating original data where the age group is too large. Must be between 1 and 5.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testTooBigAgeGroup() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("age group must be between 0 and 5");
        originalDataTest = new CODOrignalData(descriptionList, 1999, 7, 1, 0, "testFileName");

    }

    /**
     * Tests creating originalData with a negative age group.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testNegativeSex() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("-1 read for sex.\nsex must be 1 or 0. 1 is male, 0 is female");
        originalDataTest = new CODOrignalData(descriptionList, 1999, 1, -1, 0, "testFileName");

    }

    /**
     * Tests creating originalData with a negative age group.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testTooBigSex() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("2 read for sex.\nsex must be 1 or 0. 1 is male, 0 is female");
        originalDataTest = new CODOrignalData(descriptionList, 1999, 1, 2, 0, "testFileName");

    }

    /**
     * Tests creating originalData with imageQuality set larger than 1.
     *
     * @throws InputFormatException the input format exception
     */
    @Ignore("Need to refactor these these tests and check valid image quality identifiers")
    @Test
    public void testImageQualityTooBig() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("image quality must be 0 or 1");
        originalDataTest = new CODOrignalData(descriptionList, 1999, 5, 1, 2, "testFileName");

    }

    /**
     * Tests creating originalData with imageQuality set less than 1.
     *
     * @throws InputFormatException the input format exception
     */
    @Ignore("Need to refactor these these tests and check valid image quality identifiers")
    @Test
    public void testImageQualityTooSmall() throws InputFormatException {

        customException.expect(NumberFormatException.class);
        customException.expectMessage("image quality must be 0 or 1");
        originalDataTest = new CODOrignalData(descriptionList, 1999, 5, 11, -1, "testFileName");

    }

    /**
     * Tests creating originalData with imageQuality set to 1.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testImageQualityJustRight() throws InputFormatException {

        OriginalData originalDataTest = new CODOrignalData(descriptionList, 1999, 5, 1, 0, "testFileName");
        Assert.assertEquals(0, originalDataTest.getImageQuality());

    }

    /**
     * Test cause of death and original data constuctor.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCauseOfDeathAndOriginalDataConstuctor() throws InputFormatException {

        CODOrignalData codOrginalData = new CODOrignalData(descriptionList2, 2014, 3, 0, 1, "testFileName");
        Assert.assertEquals("description", codOrginalData.getDescription().get(0));
        Assert.assertEquals(2014, codOrginalData.getYear());
        Assert.assertEquals(3, codOrginalData.getAgeGroup());
        Assert.assertEquals(0, codOrginalData.getSex());
        Assert.assertEquals(1, codOrginalData.getImageQuality());

    }

    /**
     * Test cause of death and original data constuctor with null description.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCauseOfDeathAndOriginalDataConstuctorWithNullDescription() throws InputFormatException {

        customException.expect(InputFormatException.class);
        customException.expectMessage("description passed to constructor cannot be null");
        CODOrignalData codOrginalData = new CODOrignalData(null, 2014, 10, 5, 1, "testFileName");
        Assert.assertEquals(2014, codOrginalData.getYear());
        Assert.assertEquals(5, codOrginalData.getAgeGroup());
        Assert.assertEquals(1, codOrginalData.getImageQuality());

    }

    /**
     * Test this equals symmetric.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testThisEqualsSymmetric() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        assertTheSame(x, x);
    }

    /**
     * Test equals on CODOrignalData when using different classes.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testDiffClassEquals() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        OriginalData y = new OriginalData(descriptionList2, 2014, 1, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals when objects share all data.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetric() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        assertTheSame(x, y);
    }

    /**
     * Test equals on  CODOrignalData with different description.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferentDescription() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList, 2014, 1, 0, 0, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals with different year.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferntYear() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2010, 1, 0, 0, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals with different image quality.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferenImageQuality() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 0, 0, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals with different file names.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferentFileName() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "differnt/fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals symmetric different age groups.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferentAge() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 1, 0, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test CODOrignalData equals with different sex.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCODEqualsSymmetricDifferentSex() throws InputFormatException {

        CODOrignalData x = new CODOrignalData(descriptionList2, 2014, 1, 0, 1, "fileName");
        CODOrignalData y = new CODOrignalData(descriptionList2, 2014, 1, 0, 0, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test equals symmetric on OriginalData.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testEqualsSymmetric() throws InputFormatException {

        OriginalData x = new OriginalData(descriptionList2, 2014, 1, "fileName");
        OriginalData y = new OriginalData(descriptionList2, 2014, 1, "fileName");
        assertTheSame(x, y);
    }

    /**
     * Test equals symmetric different description.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testEqualsSymmetricDifferentDescription() throws InputFormatException {

        OriginalData x = new OriginalData(descriptionList2, 2014, 1, "fileName");
        OriginalData y = new OriginalData(descriptionList, 2014, 1, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test equals symmetric different year.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testEqualsSymmetricDifferntYear() throws InputFormatException {

        OriginalData x = new OriginalData(descriptionList2, 2014, 1, "fileName");
        OriginalData y = new OriginalData(descriptionList2, 2010, 1, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test equals symmetric differen image quality.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testEqualsSymmetricDifferenImageQuality() throws InputFormatException {

        OriginalData x = new OriginalData(descriptionList2, 2014, 0, "fileName");
        OriginalData y = new OriginalData(descriptionList2, 2014, 1, "fileName");
        assertDifferent(x, y);
    }

    /**
     * Test equals symmetric different file name.
     *
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testEqualsSymmetricDifferentFileName() throws InputFormatException {

        OriginalData x = new OriginalData(descriptionList2, 2014, 1, "fileName");
        OriginalData y = new OriginalData(descriptionList2, 2014, 1, "differnt/fileName");
        assertDifferent(x, y);
    }

    /**
     * Assert different.
     *
     * @param x the x
     * @param y the y
     */
    private void assertDifferent(final OriginalData x, final OriginalData y) {

        Assert.assertTrue(!x.equals(y) && !y.equals(x));
        Assert.assertTrue(x.hashCode() != y.hashCode());
    }

    /**
     * Assert the same.
     *
     * @param x the x
     * @param y the y
     */
    private void assertTheSame(final OriginalData x, final OriginalData y) {

        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertTrue(x.hashCode() == y.hashCode());
    }

}
