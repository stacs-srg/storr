package uk.ac.standrews.cs.digitising_scotland.parser.machinelearning.Tokenizing;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.standrews.cs.digitising_scotland.parser.machinelearning.tokenizing.TokenStreamIterator;
import uk.ac.standrews.cs.digitising_scotland.parser.machinelearning.tokenizing.TokenStreamIteratorFactory;

public class TokenStreamIteratorFactoryTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testFactory() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("brown", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("fox", it.next().toString());
        }

    }

    @Test
    public void testGetIndex() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        Assert.assertEquals(-1, it.getIndex());
        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
            Assert.assertEquals(0, it.getIndex());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
            Assert.assertEquals(1, it.getIndex());

        }

    }

    @Test
    public void testHasNext() {

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("brown", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("fox", it.next().toString());
        }

        Assert.assertTrue(!it.hasNext());

    }

    @Test
    public void testRemove() {

        expectedEx.expect(UnsupportedOperationException.class);

        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator("The quick brown fox");

        if (it.hasNext()) {
            Assert.assertEquals("The", it.next().toString());
        }
        if (it.hasNext()) {
            Assert.assertEquals("quick", it.next().toString());
            it.remove();
        }

    }
}
