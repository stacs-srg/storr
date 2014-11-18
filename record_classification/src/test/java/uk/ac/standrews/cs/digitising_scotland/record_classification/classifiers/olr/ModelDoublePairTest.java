/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests basic functionality of ModelDoublePair class which stores an {@link OLRShuffled} model and an associated double.
 * @author jkc25
 *
 */
public class ModelDoublePairTest {

    private ModelDoublePair pair1;
    private ModelDoublePair pair2;
    private ModelDoublePair pair3;
    private ModelDoublePair pair4;

    private OLRShuffled model1;
    private OLRShuffled model2;
    private OLRShuffled model3;

    @Before
    public void setUp() throws Exception {

        model1 = new OLRShuffled();
        model2 = new OLRShuffled();
        model3 = new OLRShuffled();

        pair1 = new ModelDoublePair(model1, 0.8);
        pair2 = new ModelDoublePair(model2, 0.5);
        pair3 = new ModelDoublePair(model3, 0.5);
        pair4 = new ModelDoublePair(model1, 0.8);

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

    @Test
    public void testEquals() {

        Assert.assertNotEquals(pair1, pair2);
        Assert.assertNotEquals(pair2, pair3);
        Assert.assertEquals(pair1, pair4);

    }

}
