package uk.ac.standrews.cs.digitising_scotland.tools.stringutils;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.stringutils.StringUtils;

import com.google.common.collect.TreeMultiset;

public class StringUtilsTest {

    @Test
    public void testNoOverlap() {

        String originalString = "Here is a string";
        String[] expected = {"Here", "is", "a", "string"};
        TreeMultiset<String> set = StringUtils.getMultiSetFromString(originalString);
        for (String string : expected) {
            Assert.assertTrue(set.contains(string));
            Assert.assertEquals(1, set.count(string));
        }
    }

    @Test
    public void testMultipleString() {

        String originalString = "Here is a string a is Here string";
        String[] expected = {"Here", "is", "a", "string"};
        TreeMultiset<String> set = StringUtils.getMultiSetFromString(originalString);
        for (String string : expected) {
            Assert.assertTrue(set.contains(string));
            Assert.assertEquals(2, set.count(string));
        }
    }

}
