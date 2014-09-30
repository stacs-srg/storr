package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ModelDoublePairTest {

    ModelDoublePair pair1;
    ModelDoublePair pair2;
    ModelDoublePair pair3;

    OLRShuffled model1;
    OLRShuffled model2;
    OLRShuffled model3;

    @Before
    public void setUp() throws Exception {

        model1 = new OLRShuffled();
        model2 = new OLRShuffled();
        model3 = new OLRShuffled();

        pair1 = new ModelDoublePair(model1, 0.8);
        pair2 = new ModelDoublePair(model2, 0.5);
        pair3 = new ModelDoublePair(model3, 0.5);

    }

    @Test
    public void testGetCorrect() {

        Assert.assertEquals(0.8, pair1.getPropCorrect(), 0.001);
        Assert.assertEquals(0.5, pair2.getPropCorrect(), 0.001);

    }

    @Test
    public void testGetModel() {

        Assert.assertEquals(model1, pair1.getModel());
        Assert.assertEquals(model2, pair2.getModel());

    }

    @Test
    public void testCompareTo() {

        Assert.assertEquals(1, pair1.compareTo(pair2));
        Assert.assertEquals(-1, pair2.compareTo(pair1));
        Assert.assertEquals(-1, pair2.compareTo(pair1));

    }

}
